package k_paas.balloon.keeper.api.domain.balloonPosition;

public record ReportBalloonImageCodeResponse(
        String code
) {

    public static ReportBalloonImageCodeResponse from(String code){
        return new ReportBalloonImageCodeResponse(code);
    }
}
