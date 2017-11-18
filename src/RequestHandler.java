package httpserver;

import java.io.File;
import java.io.IOException;


public class RequestHandler {

    public HttpResponse handle(HttpRequest request) throws HttpError {

        File pathfile = new File(".", request.path);

        // Check the path
        if (!pathfile.exists()) {
            throw new HttpError(HttpStatus.NotFound(), request.path );
        }

        if (pathfile.isDirectory()) {

        }

        HttpResponse response = new DirectoryResponse(request);
        response.setHeader("Content-type", "text/plain");

        return response;
    }
}