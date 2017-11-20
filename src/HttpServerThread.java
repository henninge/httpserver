package httpserver;

import java.net.*;
import java.io.*;

public class HttpServerThread extends Thread {
    private Socket socket = null;
    private RequestHandler handler = null;
    private long connectionCounter;

    public HttpServerThread(Socket socket, RequestHandler handler, long connectionCounter) {
        super("HttpServerThread");
        this.socket = socket;
        this.handler = handler;
        this.connectionCounter = connectionCounter;
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
            // Add a few extra seconds.
            socket.setSoTimeout((HttpServer.DEFAULT_TIMEOUT + 5) * 1000 );

            OutputStream socketOut = socket.getOutputStream();
            BufferedReader socketIn = new BufferedReader(
                    new InputStreamReader(socket.getInputStream()));

            HttpRequest request = null;
            HttpResponse response = null;

            do {
                try {
                    request = HttpRequest.fromReader(socketIn);
                    response = handler.handle(request);
                } catch (HttpError he) {
                    response = new StatusResponse(he.status);
                } catch (NoRequestException | java.net.SocketTimeoutException nre) {
                    // No request found, send no response and close the connection.
                    response = null;
                } catch (RuntimeException re) {
                    HttpStatus serverError = HttpStatus.ServerError();
                    serverError.setDetail(re.toString());
                    response = new StatusResponse(serverError);
                    re.printStackTrace();
                }

                if (response != null) {
                    response.write(socketOut);
                    // Log request to stdout
                    System.out.println(
                        String.format("[%1$5d][%2$s][%3$s]",
                            connectionCounter, request.toString(), response.toString()
                    ));
                }

            } while (response != null && response.isPersistent());

            // Clean up
            socketOut.close();
            socketIn.close();
            socket.close();

        } catch (java.net.SocketException se) {
            se.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
