package k_paas.balloon.keeper.infrastructure.client;

public record SimulationImageDto(
        Long balloonReportId,
        String serialCode,
        String path
) {
    public static SimulationImageDto of(
            Long balloonReportId,
            String serialCode,
            String path
    ) {
        return new SimulationImageDto(
                balloonReportId,
                serialCode,
                path
        );
    }
}
