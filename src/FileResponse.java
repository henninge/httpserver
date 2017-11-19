package httpserver;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Date;

public class FileResponse extends HttpResponse {

    private Path file;

    public FileResponse(HttpRequest httpRequest, Path filePath) {
        super(httpRequest);
        file = filePath;
        int fileSize;

        try {
            setHeader("Content-Size", String.format("%1d", Files.size(file)));
        } catch (IOException ioe) {
            // Just leave out the header.
        }

        setHeader(
            "Content-Disposition",
            String.format("attachment; filename=\"%1s\"", file.getFileName()));
        setHeader("Content-Type", "application/octet-stream");

        try {
            Date lastModified = new Date(Files.getLastModifiedTime(file).toMillis());
            setHeader("Last-Modified", rfc1123Format.format(lastModified));
        } catch (IOException ioe) {
            // Just leave out the header.
        }
    }

    public void writeBody(OutputStream out) throws IOException {
        Files.copy(file, out);
    }
}
