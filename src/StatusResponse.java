package httpserver;

import java.io.IOException;
import java.io.OutputStream;

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

    public void writeBody(OutputStream out) throws IOException {
        out.write(status.toResponseBody().getBytes("UTF-8"));
    }
}
