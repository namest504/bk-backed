package k_paas.balloon.keeper.api.domain.balloon.dto;

public record BalloonReportWithCount(
        Double centerLatitude,
        Double centerLongitude,
        String streetAddress,
        Long reportCount
) {
    public BalloonReportWithCount(Double centerLatitude, Double centerLongitude, String streetAddress, Long reportCount) {
        this.centerLatitude = centerLatitude;
        this.centerLongitude = centerLongitude;
        this.streetAddress = streetAddress;
        this.reportCount = reportCount;
    }
}
