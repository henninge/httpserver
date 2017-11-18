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

        try (
            PrintWriter socketOut = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader socketIn = new BufferedReader(
                    new InputStreamReader(socket.getInputStream()));
        ) {
            HttpRequest request;
            HttpResponse response;

            try {
                request = HttpRequest.fromReader(socketIn);
                response = handler.handle(request);
            } catch (HttpError he) {
                response = new StatusResponse(he.status);
            }

            response.write(socketOut);
            socketOut.flush();
            this.cleanUp();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
