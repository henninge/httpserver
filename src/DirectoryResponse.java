package httpserver;

import java.io.IOException;
import java.io.File;
import java.io.PrintWriter;

public class DirectoryResponse extends HttpResponse {

    private File directory;

    public DirectoryResponse(HttpRequest httpRequest, File directoryFile) {
        super(httpRequest);
        directory = directoryFile;
        setHeader("Content-Type", "text/html");
    }

    public void writeBody(PrintWriter writer) {
        try {
            writer.println(String.format(
                "<html><body><h1>%1s</h2><ul>", directory.getCanonicalPath()));
            for (File file: directory.listFiles()) {
                writer.println(String.format(
                    "<li><a href=\"%1s\">%2s</a></li>", file.getName(), file.getName()));
            }
            writer.println("</ul></body></html>");
        } catch (IOException ioe) {
            writer.println("I/O Error: " + ioe.getMessage());
        }
    }
}
