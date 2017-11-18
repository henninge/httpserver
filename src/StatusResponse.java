package httpserver;

import java.io.PrintWriter;

public class StatusResponse extends HttpResponse {

    public StatusResponse() {
        super();
    }

    public StatusResponse(HttpStatus responseStatus) {
        super(responseStatus, new HttpRequest());
    }

    public StatusResponse(HttpStatus responseStatus, HttpRequest httpRequest) {
        super(responseStatus, httpRequest);
    }

    public void writeBody(PrintWriter writer) {
        writer.println(status.toResponseBody());
    }
}
