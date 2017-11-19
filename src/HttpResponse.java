package httpserver;

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

public abstract class HttpResponse {

    protected HttpStatus status;
    protected HashMap<String, String> headers;
    protected HttpRequest request;

    public HttpResponse() {
        this(new HttpRequest());
    }

    public HttpResponse(HttpRequest httpRequest) {
        this(HttpStatus.OK(), httpRequest);
    }

    public HttpResponse(HttpStatus responseStatus, HttpRequest httpRequest) {
        status = responseStatus;
        headers = new HashMap<String, String>();
        request = httpRequest;
    }

    public void setHeader(String headerName, String headerValue) {
        headers.put(headerName.toLowerCase(), headerValue.trim());
    }

    public boolean hasBody() {
        return !(status.equals(HttpStatus.NoContent()) || request.getMethod() == HttpRequest.Method.HEAD);
    }

    protected void addRequiredHeaders() {
        setHeader("Connection", "Close");
    }

    public void write(PrintWriter writer) {
        // Finalize Reponse
        addRequiredHeaders();

        writer.println(status.toResponseLine());
        for (Map.Entry<String, String> header: headers.entrySet()) {
            writer.println(String.format("%1s: %2s", header.getKey(), header.getValue()));
        }

        if (hasBody()) {
            writer.println("");
            writeBody(writer);
        }
    }

    public abstract void writeBody(PrintWriter writer);

}
