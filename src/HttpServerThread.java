package httpserver;

import java.net.*;
import java.io.*;

public class HttpServerThread extends Thread {
    private Socket socket = null;
    private RequestHandler handler = null;

    public HttpServerThread(Socket socket, RequestHandler handler) {
        super("HttpServerThread");
        this.socket = socket;
        this.handler = handler;
    }

    private void cleanUp() throws IOException {
        this.socket.close();
    }

    private void outputRequest(HttpRequest request) {
        System.out.println(request.toString());

        for (String headerName: request.getHeaderNames()) {
            String header = request.getHeader(headerName);
            System.out.println(String.format("%1s: %2s", headerName, header));
        }
    }

    public void run() {

        try {
            System.out.println("Opening new connection.");

            OutputStream socketOut = socket.getOutputStream();
            BufferedReader socketIn = new BufferedReader(
                    new InputStreamReader(socket.getInputStream()));

            HttpRequest request;
            HttpResponse response;

            do {
                try {
                    request = HttpRequest.fromReader(socketIn);
                    outputRequest(request);
                    response = handler.handle(request);
                } catch (HttpError he) {
                    response = new StatusResponse(he.status);
                } catch (NoRequestException nre) {
                    // No request found, send no response.
                    response = null;
                } catch (RuntimeException re) {
                    HttpStatus serverError = HttpStatus.ServerError();
                    serverError.setDetail(re.toString());
                    response = new StatusResponse(serverError);
                    re.printStackTrace();
                }

                if (response != null) {
                    response.write(socketOut);
                }
            } while (response != null && response.isPersistent());

            // Clean up
            socketOut.close();
            socketIn.close();
            socket.close();

            System.out.println("Connection closed.");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
