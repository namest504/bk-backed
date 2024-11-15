package k_paas.balloon.keeper.application.domain.balloon.report.dto;

public record ReportBalloonImageCodeResponse(
        String code
) {

    public static ReportBalloonImageCodeResponse from(String code){
        return new ReportBalloonImageCodeResponse(code);
    }
}
