package httpserver;

import java.lang.RuntimeException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;

public class HttpRequest {
    // Possible HTTP request methods.
    static enum Method {GET, HEAD, POST};

    protected Method requestMethod;
    protected String path;
    protected String httpHost;

    HttpRequest(String requestLine) {
        parseRequestLine(requestLine);
    } 

    public String toString() {
        return String.format("%1s %2s HTTP/1.1", requestMethod, path);
    }

    private void parseRequestLine(String requestLine) {
        String[] rlParts = requestLine.split(" ");

        // Exactly 3 parts are expected.
        if (rlParts.length != 3) {
            throw new RuntimeException("HTTP protocol error: Malformed request line.");
        }

        // Check and select method enum.
        switch (rlParts[0]) {
            case "GET":
                requestMethod = Method.GET;
                break;
            case "HEAD":
                requestMethod = Method.HEAD;
                break;
            case "POST":
                requestMethod = Method.POST;
                break;
            default:
                throw new RuntimeException("501 Not Implemented" + rlParts[0]);
        }

        // Parse the target to get the path.
        try {
            URI uri = new URI(rlParts[1]);
            path = uri.getPath();
       } catch (URISyntaxException urise) {
            throw new RuntimeException("HTTP protocol error: Malformed request line.");
        }
 
        // Check Protocol.
        if (!rlParts[2].equals("HTTP/1.1")) {
            throw new RuntimeException("505 HTTP Version Not Supported: " + rlParts[2]);            
        }
    }    
}
