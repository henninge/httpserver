package httpserver;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.DirectoryStream;
import java.nio.file.Path;

public class DirectoryResponse extends HttpResponse {

    private Path directory;
    private Path basedir;

    public DirectoryResponse(HttpRequest httpRequest, Path directoryPath, Path basedirPath) {
        super(httpRequest);
        directory = directoryPath;
        basedir = basedirPath;
        setHeader("Content-Type", "text/html; charset=UTF-8");
        // We cannot send a Content-Length header for directories
        // which is needed for persistent connections.
        persistent = false;
    }

    private String getHref(Path filePath) {
        return "/" + basedir.relativize(filePath).toString();
    }


    private String getDisplayName(Path filePath) {
        String fileName = filePath.getFileName().toString();
        if (Files.isDirectory(filePath)) {
            return fileName + "/";
        } else {
            return fileName;
        }
    }

    public void writeBody(OutputStream out) throws IOException {
        PrintWriter writer = new PrintWriter(out, true);

        writer.println(String.format(
            "<html><body><h1>%1s</h2><ul>", getHref(directory)));

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(directory)) {
           for (Path file: stream) {
                writer.println(String.format(
                    "<li><a href=\"%1s\">%2s</a></li>", getHref(file), getDisplayName(file)));
           }
        }
        writer.println("</ul></body></html>");
    }
}
