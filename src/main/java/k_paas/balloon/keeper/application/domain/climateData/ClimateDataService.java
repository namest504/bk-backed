package k_paas.balloon.keeper.application.domain.climateData;

public interface ClimateDataService {
    ClimateDataPathResponse getRecentCsvFilePath();

    void initLearning();
}
