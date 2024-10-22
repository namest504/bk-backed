package k_paas.balloon.keeper.global.exception;

public class BkException extends RuntimeException{
    public BkException() {
    }

    public BkException(String message) {
        super(message);
    }

    public BkException(String message, Throwable cause) {
        super(message, cause);
    }

    public BkException(Throwable cause) {
        super(cause);
    }

    public BkException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
