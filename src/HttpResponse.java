package httpserver;

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

public class HttpResponse {

    private HttpStatus status;
    private HashMap<String, String> headers;
    private String body;

    HttpResponse() {
        this(HttpStatus.OK());
    }

    HttpResponse(HttpStatus responseStatus) {
        status = responseStatus;
        headers = new HashMap<String, String>();
        body = null;
    }

    public void setHeader(String headerName, String headerValue) {
        headers.put(headerName.toLowerCase(), headerValue.trim());
    }

    public void setBody(String responseBody) {
        body = responseBody;
    }

    public void setBody(String responseBody, String contentType) {
        body = responseBody;
        setHeader("content-type", contentType);
    }

    public void write(PrintWriter writer) {
        writer.println(status.toResponseLine());
        for (Map.Entry<String, String> header: headers.entrySet()) {
            writer.println(String.format("%1s: %2s", header.getKey(), header.getValue()));
        }
        if (body == null) {
            body = status.toResponseBody();
        }
        if (body != null) {
            writer.println("");
            writer.print(body);
        }
    }
}
