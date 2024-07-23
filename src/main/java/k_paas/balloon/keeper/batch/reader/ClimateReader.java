package k_paas.balloon.keeper.batch.reader;

import static k_paas.balloon.keeper.global.constant.ClimateContants.ARRAY_X_INDEX;
import static k_paas.balloon.keeper.global.constant.ClimateContants.ARRAY_Y_INDEX;
import static k_paas.balloon.keeper.global.constant.ClimateContants.ISOBARIC_ALTITUDE;
import static k_paas.balloon.keeper.global.constant.ClimateContants.MAX_PREDICT_HOUR;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import k_paas.balloon.keeper.batch.dto.UpdateClimateServiceSpec;
import k_paas.balloon.keeper.batch.service.ClimateAsyncService;
import org.springframework.batch.item.ItemReader;
import org.springframework.stereotype.Component;

@Component
public class ClimateReader implements ItemReader<List<UpdateClimateServiceSpec>> {

    private List<List<UpdateClimateServiceSpec>> chunkedDataList;
    private int currentIndex = 0;

    private final ClimateAsyncService climateAsyncService;

    public ClimateReader(ClimateAsyncService climateAsyncService) {
        this.climateAsyncService = climateAsyncService;
    }

    @Override
    public List<UpdateClimateServiceSpec> read() {
        if (chunkedDataList == null) {
            chunkedDataList = new ArrayList<>();
            List<UpdateClimateServiceSpec> allData = new ArrayList<>();
            for (int altitude = 0; altitude < ISOBARIC_ALTITUDE.length; altitude++) {
                for (int predictHour = 0; predictHour <= MAX_PREDICT_HOUR; predictHour++) {
                    allData.addAll(processClimateData(altitude, predictHour));
                }
            }
            // 데이터를 청크 단위로 분할
            int chunkSize = 1000; // 청크 크기
            for (int i = 0; i < allData.size(); i += chunkSize) {
                chunkedDataList.add(allData.subList(i, Math.min(i + chunkSize, allData.size())));
            }
            currentIndex = 0;
        }

        if (currentIndex < chunkedDataList.size()) {
            return chunkedDataList.get(currentIndex++);
        } else {
            return null; // 모든 청크를 다 읽으면 null을 반환하여 읽기를 종료함
        }
    }

    private List<UpdateClimateServiceSpec> processClimateData(int altitude, int predictHour) {
        CompletableFuture<String[][]> completableUVectors = sendClimateRequest(2002, altitude, predictHour);
        CompletableFuture<String[][]> completableVVectors = sendClimateRequest(2003, altitude, predictHour);

        List<UpdateClimateServiceSpec> result = CompletableFuture.allOf(completableUVectors, completableVVectors)
                .thenApply(r -> {
                    String[][] uVectorArray = completableUVectors.join();
                    String[][] vVectorArray = completableVVectors.join();
                    List<UpdateClimateServiceSpec> updateClimateServiceSpecs = saveClimateData(altitude, predictHour, uVectorArray, vVectorArray);
                    return updateClimateServiceSpecs;
                }).join();
        return result;
    }

    private CompletableFuture<String[][]> sendClimateRequest(int parameterIndex, int altitude, int predictHour) {
        return climateAsyncService.sendRequest(
                String.valueOf(parameterIndex),
                String.valueOf(ISOBARIC_ALTITUDE[altitude]),
                String.valueOf(predictHour)
        );
    }

    private List<UpdateClimateServiceSpec> saveClimateData(int altitude, int predictHour, String[][] uVectorArray, String[][] vVectorArray) {
        List<UpdateClimateServiceSpec> result = new ArrayList<>();

        // TODO: Y 마지막 인덱스 데이터 null 발생
        for (int y = 0; y < ARRAY_Y_INDEX - 1; y++) {
            for (int x = 0; x < ARRAY_X_INDEX; x++) {
                result.add(createUpdateClimateSpec(y, x, altitude, predictHour, uVectorArray, vVectorArray));
            }
        }
        return result;
    }

    private UpdateClimateServiceSpec createUpdateClimateSpec(int y, int x, int altitude, int predictHour, String[][] uVectorArray, String[][] vVectorArray) {
        return UpdateClimateServiceSpec.builder()
                .y(y)
                .x(x)
                .prespredictHour(predictHour)
                .pressure(altitude)
                .UVector(uVectorArray[y][x])
                .VVector(vVectorArray[y][x])
                .predictHour(predictHour)
                .pressureValue(ISOBARIC_ALTITUDE[altitude])
                .build();
    }
}
