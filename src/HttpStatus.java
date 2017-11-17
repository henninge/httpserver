package httpserver;

public class HttpStatus {

    public int code;
    public String message;
    public String detail;

    public HttpStatus(int status_code, String status_message) {
        code = status_code;
        message = status_message;
        detail = "";
    }

    public void setDetail(String status_detail) {
        detail = status_detail;
    }

    public String toResponseLine() {
        return String.format("HTTP/1.1 %1d %2s", code, message);
    }

    public String toResponseBody() {
        if (code >= 300) {
            return String.format("%1d %2s: %3s", code, message, detail);
        } else {
            // No default body for 2xx codes.
            return null;
        }
    }

    static HttpStatus OK() { return new HttpStatus(200, "OK");}
    static HttpStatus Created() { return new HttpStatus(201, "Created");}
    static HttpStatus NoContent() { return new HttpStatus(204, "No Content");}

    static HttpStatus MovedPermanently() { return new HttpStatus(301, "MovedvPermanently");}
    static HttpStatus NotModified() { return new HttpStatus(304, "Not Modified");}
    static HttpStatus TemporaryRedirect() { return new HttpStatus(307, "Temporary Redirect");}

    static HttpStatus BadRequest() { return new HttpStatus(400, "Bad Request");}
    static HttpStatus Forbidden() { return new HttpStatus(403, "Forbidden");}
    static HttpStatus NotFound() { return new HttpStatus(404, "Not Found");}
    static HttpStatus MethodNotAllowed() { return new HttpStatus(405, "Method Not Allowed");}

    static HttpStatus ServerError() { return new HttpStatus(500, "Server Error");}
    static HttpStatus NotImplemented() { return new HttpStatus(501, "Not Implemented");}
    static HttpStatus ServiceUnvavailable() { return new HttpStatus(503, "Service Unvavailable");}
    static HttpStatus HttpVersionNotSupported() { return new HttpStatus(505, "HTTP Version Not Supported");}
}
