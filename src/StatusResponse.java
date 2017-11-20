package httpserver;

import java.io.IOException;
import java.io.OutputStream;

public class StatusResponse extends HttpResponse {

    public StatusResponse() {
        super();
        persistent = false;
    }

    public StatusResponse(HttpStatus responseStatus) {
        super(responseStatus, new HttpRequest());
        persistent = false;
    }

    public StatusResponse(HttpStatus responseStatus, HttpRequest httpRequest) {
        super(responseStatus, httpRequest);
        persistent = false;
    }

    public void writeBody(OutputStream out) throws IOException {
        out.write(status.toResponseBody().getBytes("UTF-8"));
    }
}
