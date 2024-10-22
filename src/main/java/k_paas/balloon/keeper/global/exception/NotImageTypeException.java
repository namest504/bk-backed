package k_paas.balloon.keeper.global.exception;

public class NotImageTypeException extends BkException{

    public static final String MESSAGE = "이미지 타입이 아닙니다.";

    public NotImageTypeException() {
        super(MESSAGE);
    }
}
