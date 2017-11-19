package httpserver;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;

public class FileResponse extends HttpResponse {

    private Path file;

    private static String hexByte(byte value) {
        String hex = Integer.toHexString(value);
        // toHexString produces no leading 0s and
        // 8 digits for for negative values.
        if (value >= 16) {
            return hex;
        } else if (value >= 0) {
            return "0" + hex;
        } else {
            return hex.substring(6);
        }
    }

    private static String getSha1Digest(String... inputs) {
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("SHA-1");
            for (String input: inputs) {
                md.update(input.getBytes("UTF-8"));
            }
        } catch (UnsupportedEncodingException | NoSuchAlgorithmException e) {
            // Not happening.
        }

        StringBuffer hexDigest = new StringBuffer();
        for (byte hashDigit: md.digest()) {
            hexDigest.append(hexByte(hashDigit));
        }
        return hexDigest.toString();
    }

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

        Date lastModified;
        try {
            lastModified = new Date(Files.getLastModifiedTime(file).toMillis());
            setHeader("Last-Modified", rfc1123Format.format(lastModified));
        } catch (IOException ioe) {
            // Just leave out the header.
            lastModified = null;
        }

        if (lastModified != null) {
            setHeader("Etag", getSha1Digest(
                rfc1123Format.format(lastModified),
                file.toString()
            ));
        }
    }

    public void writeBody(OutputStream out) throws IOException {
        Files.copy(file, out);
    }
}
