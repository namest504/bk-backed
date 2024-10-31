package k_paas.balloon.keeper.application.domain.balloon.report.dto;

import k_paas.balloon.keeper.infrastructure.persistence.repository.BalloonReport;

import java.time.LocalDateTime;

public record BalloonReportDto(
        Long id,
        String serialCode,
        Boolean isCheckedStatus,
        Double reportedLatitude,
        Double reportedLongitude,
        Double centerLatitude,
        Double centerLongitude,
        String streetAddress,
        LocalDateTime reportedTime
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
                balloonReport.getStreetAddress(),
                balloonReport.getCreatedAt()
        );
    }
}
