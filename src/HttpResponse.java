package httpserver;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Date;
import java.util.Map;
import java.util.TimeZone;

public abstract class HttpResponse {

    protected static SimpleDateFormat rfc1123Format;
    static {
        rfc1123Format = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss 'GMT'");
        rfc1123Format.setTimeZone(TimeZone.getTimeZone("UTC"));
    }

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
        Date now = new Date(System.currentTimeMillis());
        setHeader("Date", rfc1123Format.format(now));
        setHeader("Connection", "Close");
    }

    public void write(OutputStream out) throws IOException {
        // Finalize Reponse
        addRequiredHeaders();

        PrintWriter writer = new PrintWriter(out, true);

        writer.println(status.toResponseLine());
        for (Map.Entry<String, String> header: headers.entrySet()) {
            writer.println(String.format("%1s: %2s", header.getKey(), header.getValue()));
        }

        if (hasBody()) {
            writer.println("");
            writeBody(out);
        }
    }

    public abstract void writeBody(OutputStream out) throws IOException;

}
