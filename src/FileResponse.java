package httpserver;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
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

    private boolean isEtagInHeader(String header, String etag) {
        if (header.equals("*")) {
            return true;
        }

        for (String headerEtag: header.split(", *")) {
            if (headerEtag.equals("\"" + etag + "\"")) {
                return true;
            }
        }

        return false;
    }

    public FileResponse(HttpRequest httpRequest, Path filePath) throws HttpError {
        super(httpRequest);
        file = filePath;
        int fileSize;

        // Close connections because keep-alive is not working. :-(
        persistent = false;

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
            // The following only make sense if we know the modification time.
            String ifModifiedSinceHeader = request.getHeader("If-Modified-Since");
            Date ifModifiedSince = null;
            boolean isModified = true;
            if (ifModifiedSinceHeader != null) {
                try {
                    ifModifiedSince = rfc1123Format.parse(ifModifiedSinceHeader);
                } catch (ParseException pe) {
                    ifModifiedSince = null;
                }
                if (ifModifiedSince != null) {
                    Date now = new Date(System.currentTimeMillis());
                    if (ifModifiedSince.after(now)) {
                        // Future dates are invalid.
                        ifModifiedSince = null;
                    } else if (!lastModified.after(ifModifiedSince)) {
                        isModified = false;
                    }
                }
            }

            String etag = getSha1Digest(
                rfc1123Format.format(lastModified),
                file.toString()
            );
            setHeader("Etag", etag );

            String ifMatch = request.getHeader("If-Match");
            if (ifMatch != null && !isEtagInHeader(ifMatch, etag)) {
                // Do not serve this request!
                throw new HttpError(HttpStatus.PreconditionFailed(), "If-Match");
            }

            String ifNoneMatch = request.getHeader("If-None-Match");
            if (ifNoneMatch != null ) {
                if (isEtagInHeader(ifNoneMatch, etag)) {
                    if (request.getMethod() == HttpRequest.Method.GET ||
                        request.getMethod() == HttpRequest.Method.HEAD)
                    {
                        // Do not overwrite If-Modified-Since
                        isModified = ifModifiedSinceHeader == null ? false : isModified;
                    } else {
                        // Do not serve this request!
                        throw new HttpError(HttpStatus.PreconditionFailed(), "If-None-Match");
                    }
                } else {
                    // Ignore If-Modified-Since.
                    isModified = true;
                }
            }

            if (!isModified) {
                // Do not serve the file content.
                status = HttpStatus.NotModified();
            }
        }
    }

    public void writeBody(OutputStream out) throws IOException {
        Files.copy(file, out);
        out.flush();
    }
}
