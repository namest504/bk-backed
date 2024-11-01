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

    /**
     * 기후 데이터가 포함된 최신 CSV 파일의 경로를 검색
     * 경로가 메모리에 저장되어 있지 않으면 NCP Object Storage 서비스에서 최신 경로를 가져옴
     * 최근 경로를 찾을 수 없으면 RuntimeException이 발생
     */
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


    /**
     * {@link SimulationClient#initiateLearningProcess()} 메서드를 호출하여 학습 프로세스를 실행
     */
    @Override
    public void initLearning() {
        simulationClient.initiateLearningProcess();
    }
}
