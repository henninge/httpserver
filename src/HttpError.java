package httpserver;

public class HttpError extends Exception {
    public HttpStatus status;

    public HttpError() {
        status = HttpStatus.ServerError();
    }

    public HttpError(HttpStatus httpStatus) {
        status = httpStatus;
    }

    public HttpError(HttpStatus httpStatus, String detail) {
        status = httpStatus;
        status.setDetail(detail);
    }

    public String getMessage() {
        return status.toResponseBody();
    }
}