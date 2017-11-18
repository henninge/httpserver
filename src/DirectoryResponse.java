package httpserver;

import java.io.PrintWriter;

public class DirectoryResponse extends HttpResponse {

    public DirectoryResponse() {
        super();
    }

    public DirectoryResponse(HttpRequest httpRequest) {
        super(httpRequest);
    }

    public DirectoryResponse(HttpStatus responseStatus, HttpRequest httpRequest) {
        super(responseStatus, httpRequest);
    }

    public void writeBody(PrintWriter writer) {
        writer.println("Directory!");
    }
}
