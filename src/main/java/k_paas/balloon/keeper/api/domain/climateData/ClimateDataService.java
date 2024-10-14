package k_paas.balloon.keeper.api.domain.climateData;

import k_paas.balloon.keeper.infrastructure.persistence.memory.ClimateDataInMemoryStore;
import k_paas.balloon.keeper.infrastructure.persistence.objectStorage.ncp.NcpObjectStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ClimateDataService {

    private final ClimateDataInMemoryStore climateDataInMemoryStore;
    private final NcpObjectStorageService ncpObjectStorageService;

    public String getRecentCsvFilePath() {
        String recentPath = climateDataInMemoryStore.get(ClimateDataInMemoryStore.RECENT_PATH);

        if (recentPath == null) {
            recentPath = ncpObjectStorageService.getLatestObjectPath();

            if (recentPath == null) {
                throw new RuntimeException("Not Found Recent Data");
            }
            climateDataInMemoryStore.put(ClimateDataInMemoryStore.RECENT_PATH, recentPath);

        }

        return recentPath;
    }
}
