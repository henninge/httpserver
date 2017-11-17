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
            String inputLine;
            boolean processing = true;
            // The firest line is the request line.
            HttpRequest request = new HttpRequest(socketIn.readLine());

            System.out.println("Serving " + request.toString());
            while (processing) {
                inputLine = socketIn.readLine();
                System.out.println("'" + inputLine + "'");

                if (inputLine.equals("")) {
                    processing = false;
                }
            }

            this.cleanUp();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
