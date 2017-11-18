package httpserver;

import java.net.*;
import java.io.*;

public class HttpServer {

    // Use a non-privileged port to avoid root.
    static final int HTTP_PORT = 8080;
        
    public static void main(String[] args) throws IOException {

        RequestHandler defaultHandler = new RequestHandler(args[0]);

        try (ServerSocket serverSocket = new ServerSocket(HTTP_PORT)) { 
            while (true) {
	            new HttpServerThread(serverSocket.accept(), defaultHandler).start();
	        }

	    } catch (IOException e) {
            System.err.println("Could not listen on port " + HTTP_PORT);
            System.exit(-1);
        }
    }
}
