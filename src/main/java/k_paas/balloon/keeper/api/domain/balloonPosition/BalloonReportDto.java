package k_paas.balloon.keeper.api.domain.balloonPosition;

public record BalloonReportDto(
        Long id,
        String serialCode,
        Boolean isCheckedStatus,
        Double reportedLatitude,
        Double reportedLongitude,
        Double centerLatitude,
        Double centerLongitude,
        String streetAddress
) {
    public static BalloonReportDto from(BalloonReport balloonReport) {
        return new BalloonReportDto(
                balloonReport.getId(),
                balloonReport.getSerialCode(),
                balloonReport.getIsCheckedStatus(),
                balloonReport.getReportedLatitude(),
                balloonReport.getReportedLongitude(),
                balloonReport.getCenterLatitude(),
                balloonReport.getCenterLongitude(),
                balloonReport.getStreetAddress()
        );
    }
}
