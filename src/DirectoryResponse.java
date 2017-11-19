package httpserver;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Path;

public class DirectoryResponse extends HttpResponse {

    private Path directory;
    private Path basedir;

    public DirectoryResponse(HttpRequest httpRequest, Path directoryPath, Path basedirPath) {
        super(httpRequest);
        directory = directoryPath;
        basedir = basedirPath;
        setHeader("Content-Type", "text/html; charset=UTF-8");
    }

    private String getHref(Path filePath) {
        return "/" + basedir.relativize(filePath).toString();
    }

    public void writeBody(PrintWriter writer) {
        writer.println(String.format(
            "<html><body><h1>%1s</h2><ul>", getHref(directory)));
        for (File file: directory.toFile().listFiles()) {
            writer.println(String.format(
                "<li><a href=\"%1s\">%2s</a></li>", getHref(file.toPath()), file.getName()));
        }
        writer.println("</ul></body></html>");
    }
}
