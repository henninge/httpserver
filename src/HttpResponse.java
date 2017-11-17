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

    HttpResponse(HttpStatus response_status) {
        status = response_status;
        headers = new HashMap<String, String>();
        body = null;
    }

    public void setHeader(String header_name, String header_value) {
        headers.put(header_name.toLowerCase(), header_value.trim());
    }

    public void setBody(String response_body) {
        body = response_body;
    }

    public void setBody(String response_body, String content_type) {
        body = response_body;
        setHeader("content-type", content_type);
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
