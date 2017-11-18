package httpserver;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class RequestHandler {

    protected Path basedir;

    public RequestHandler(String basedir) {
        this.basedir = Paths.get(basedir).toAbsolutePath().normalize();
    }

    public HttpResponse handle(HttpRequest request) throws HttpError {

        Path filepath = Paths.get(basedir.toString(), request.path).normalize();

        // Check the path
        if (!filepath.startsWith(basedir)) {
            // The path is outside of the basedir.
            throw new HttpError(HttpStatus.Forbidden(), request.path );
        }

        if (!filepath.toFile().exists()) {
            // The file or directory does not exist.
            throw new HttpError(HttpStatus.NotFound(), request.path );
        }

        HttpResponse response = null;

        if (filepath.toFile().isDirectory()) {
            response = new DirectoryResponse(request, filepath, basedir);
        }

        return response;
    }
}