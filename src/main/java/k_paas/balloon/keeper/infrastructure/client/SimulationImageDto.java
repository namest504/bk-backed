package k_paas.balloon.keeper.infrastructure.client;

import org.springframework.web.multipart.MultipartFile;

public record SimulationImageDto(
        Long balloonReportId,
        String serialCode,
        MultipartFile file
) {
    public static SimulationImageDto of(
            Long balloonReportId,
            String serialCode,
            MultipartFile file
    ) {
        return new SimulationImageDto(
                balloonReportId,
                serialCode,
                file
        );
    }
}
