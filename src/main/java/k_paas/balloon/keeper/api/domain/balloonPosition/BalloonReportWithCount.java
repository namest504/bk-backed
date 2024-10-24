package k_paas.balloon.keeper.api.domain.balloonPosition;

public record BalloonReportWithCount(
        Double centerLatitude,
        Double centerLongitude,
        String streetAddress,
        Long reportCount
) {
}
