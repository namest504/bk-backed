package k_paas.balloon.keeper.global.exception;

public class InternalServiceConnectionException extends BkException {


    public static final String SIMULATION_SERVER = "시뮬레이션 서버";
    public static final String CLIMATE_API = "기상 데이터 API";
    public static final String NCP_OBJECT_STORAGE = "NCP Object Storage";

    private static final String Message = "내부 서비스 [%s] 와의 문제가 발생했습니다.";

    public InternalServiceConnectionException(String serviceName) {
        super(Message.formatted(serviceName));
    }
}
