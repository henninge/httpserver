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

        HttpResponse response = null;

        if (pathfile.isDirectory()) {
            response = new DirectoryResponse(request, pathfile);
        }


        return response;
    }
}