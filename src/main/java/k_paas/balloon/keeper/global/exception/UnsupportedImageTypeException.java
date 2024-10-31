package k_paas.balloon.keeper.global.exception;

public class UnsupportedImageTypeException extends BkException{

    public static final String MESSAGE = "지원되는 이미지 타입이 아닙니다.";

    public UnsupportedImageTypeException() {
        super(MESSAGE);
    }
}
