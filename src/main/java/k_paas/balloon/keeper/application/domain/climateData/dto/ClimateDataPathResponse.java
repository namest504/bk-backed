package k_paas.balloon.keeper.application.domain.climateData.dto;

public record ClimateDataPathResponse(
        String dataPath
) {

    public static ClimateDataPathResponse from(String path) {
        return new ClimateDataPathResponse(path);
    }
}
