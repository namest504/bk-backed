package k_paas.balloon.keeper.global.exception;

public class NotFoundException extends BkException{

    private static final String Message = "[%s] 데이터가 존재하지 않습니다.";

    public static final String LOCAL_FILE = "LocalFile";
    public static final String REQUEST = "요청 관련";

    public NotFoundException(String message) {
        super(Message.formatted(message));
    }
}
