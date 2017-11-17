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
    
    public void run() {

        // Prepare Writer and reader for socket streams.
        try (
            PrintWriter socketOut = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader socketIn = new BufferedReader(
                    new InputStreamReader(socket.getInputStream()));
        ) {
            HttpRequest request = HttpRequest.fromReader(socketIn);
            System.out.println(request.toString());

            for (java.util.Map.Entry<String, String> header: request.headers.entrySet()) {
                System.out.println(String.format("%1s: %2s", header.getKey(), header.getValue()));
            }

            this.cleanUp();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
