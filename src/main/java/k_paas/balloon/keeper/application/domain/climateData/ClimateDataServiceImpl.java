package k_paas.balloon.keeper.application.domain.climateData;

import k_paas.balloon.keeper.infrastructure.client.SimulationClient;
import k_paas.balloon.keeper.infrastructure.persistence.memory.ClimateDataInMemoryStore;
import k_paas.balloon.keeper.infrastructure.persistence.objectStorage.ncp.NcpObjectStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ClimateDataServiceImpl implements ClimateDataService {

    private final ClimateDataInMemoryStore climateDataInMemoryStore;
    private final NcpObjectStorageService ncpObjectStorageService;
    private final SimulationClient simulationClient;

    @Override
    public ClimateDataPathResponse getRecentCsvFilePath() {
        String recentPath = climateDataInMemoryStore.get(ClimateDataInMemoryStore.RECENT_PATH);

        if (recentPath == null) {
            recentPath = ncpObjectStorageService.getLatestObjectPath("climate/");

            if (recentPath == null) {
                throw new RuntimeException("Not Found Recent Data");
            }
            climateDataInMemoryStore.put(ClimateDataInMemoryStore.RECENT_PATH, recentPath);

        }

        return ClimateDataPathResponse.from(recentPath);
    }

    @Override
    public void initLearning() {
        simulationClient.initiateLearningProcess();
    }
}
