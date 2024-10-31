package k_paas.balloon.keeper.application.domain.balloon.dto;

import k_paas.balloon.keeper.infrastructure.persistence.repository.BalloonPosition;

import java.time.Instant;

public record BalloonPositionResponse(
        Long id,
        Double latitude,
        Double longitude,
        String administrativeDistrict,
        String districtCode,
        Double risk,
        Integer reportCount,
        String status,
        Instant startPredictionTime
) {
    public static BalloonPositionResponse from(BalloonPosition balloonPosition) {
        return new BalloonPositionResponse(
                balloonPosition.getId(),
                balloonPosition.getLatitude(),
                balloonPosition.getLongitude(),
                balloonPosition.getAdministrativeDistrict(),
                balloonPosition.getDistrictCode(),
                balloonPosition.getRisk(),
                balloonPosition.getReportCount(),
                balloonPosition.getStatus(),
                balloonPosition.getStartPredictionTime()
        );
    }
}