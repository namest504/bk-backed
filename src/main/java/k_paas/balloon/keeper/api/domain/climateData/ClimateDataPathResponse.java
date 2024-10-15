package k_paas.balloon.keeper.api.domain.climateData;

public record ClimateDataPathResponse(
        String dataPath
) {

    public static ClimateDataPathResponse from(String path) {
        return new ClimateDataPathResponse(path);
    }
}
