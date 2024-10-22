package k_paas.balloon.keeper.global.exception;

public class InvalidAPIKeyException extends BkException {
    private static final String MESSAGE = "올바르지 않은 API Key 입니다.";

    public InvalidAPIKeyException() {
        super(MESSAGE);
    }
}
