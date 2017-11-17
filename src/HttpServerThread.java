package httpserver;

import java.net.*;
import java.io.*;

public class HttpServerThread extends Thread {
    private Socket socket = null;

    public HttpServerThread(Socket socket) {
        super("HttpServerThread");
        this.socket = socket;
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

        // Prepare Writer and reader for socket streams.
        try (
            PrintWriter socketOut = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader socketIn = new BufferedReader(
                    new InputStreamReader(socket.getInputStream()));
        ) {
            HttpRequest request = HttpRequest.fromReader(socketIn);

            outputRequest(request);

            HttpResponse response = new HttpResponse();
            response.setBody("Hallo!", "text/plain");
            response.write(socketOut);
            socketOut.flush();
            this.cleanUp();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
