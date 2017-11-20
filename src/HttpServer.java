package httpserver;

import java.net.*;
import java.io.*;

public class HttpServer {

    // Use a non-privileged port to avoid root.
    public static final int DEFAULT_HTTP_PORT = 8080;
    // Use the current directory by default.
    public static final String DEFAULT_DIRECTORY = ".";
    // 30 second timeout.
    public static final int DEFAULT_TIMEOUT = 30;
        
    public static void main(String[] args) throws IOException {
        int http_port = DEFAULT_HTTP_PORT;
        if (args.length > 1) {
            try {
                http_port = Integer.parseInt(args[1]);
            } catch (NumberFormatException nfe) {
                System.out.println("Invalid HTTP port: " + args[1]);
                System.exit(-2);
            }
        }

        String root_directory = DEFAULT_DIRECTORY;
        if (args.length > 0) {
            root_directory = args[0];
        }

        System.out.println(String.format(
            "Listening on Port %1$d, serving from '%2$s'.", http_port, root_directory));

        RequestHandler defaultHandler = new RequestHandler(root_directory);
        long connnectionCounter = 0;

        try (ServerSocket serverSocket = new ServerSocket(http_port)) { 
            while (true) {
	            new HttpServerThread(serverSocket.accept(), defaultHandler, ++connnectionCounter).start();
	        }

	    } catch (IOException e) {
            System.err.println("Could not listen on port " + http_port);
            System.exit(-1);
        }
    }
}
