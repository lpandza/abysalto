package hr.abysalto.hiring.mid.restclient.productapi.exception;

public class ProductApiException extends RuntimeException {

    private final int statusCode;

    public ProductApiException(String message) {
        super(message);
        this.statusCode = 0;
    }

    public ProductApiException(String message, int statusCode) {
        super(message);
        this.statusCode = statusCode;
    }

    public ProductApiException(String message, Throwable cause) {
        super(message, cause);
        this.statusCode = 0;
    }

    public ProductApiException(String message, int statusCode, Throwable cause) {
        super(message, cause);
        this.statusCode = statusCode;
    }

    public int getStatusCode() {
        return statusCode;
    }
}