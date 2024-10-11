package k_paas.balloon.keeper.batch.reader;

import static k_paas.balloon.keeper.global.constant.ClimateContants.ARRAY_X_INDEX;
import static k_paas.balloon.keeper.global.constant.ClimateContants.ARRAY_Y_INDEX;
import static k_paas.balloon.keeper.global.constant.ClimateContants.ISOBARIC_ALTITUDE;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import k_paas.balloon.keeper.batch.dto.UpdateClimateServiceSpec;
import k_paas.balloon.keeper.batch.service.ClimateAsyncService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.item.ItemReader;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ClimateReader implements ItemReader<List<UpdateClimateServiceSpec>>, StepExecutionListener {

    private int currentAltitudeIndex = 0;
    private static final int CHUNK_SIZE = 100;
    private boolean isCompleted = false;
    private List<UpdateClimateServiceSpec> buffer = new ArrayList<>();

    private final ClimateAsyncService climateAsyncService;

    public ClimateReader(ClimateAsyncService climateAsyncService) {
        this.climateAsyncService = climateAsyncService;
    }

    @Override
    public void beforeStep(StepExecution stepExecution) {
        // Job이 새로 시작할 때 currentAltitudeIndex 초기화
        this.currentAltitudeIndex = 0;
        this.isCompleted = false;
        this.buffer.clear();
        log.info("ClimateReader initialized with currentAltitudeIndex: {}", currentAltitudeIndex);
    }

    @Override
    public List<UpdateClimateServiceSpec> read() {
        if (isCompleted) {
            return null;
        }

        List<UpdateClimateServiceSpec> chunk = new ArrayList<>();

        while (chunk.size() < CHUNK_SIZE && !isCompleted) {
            if (buffer.isEmpty()) {
                if (currentAltitudeIndex >= ISOBARIC_ALTITUDE.length) {
                    isCompleted = true;
                    break;
                }
                buffer = processClimateData(currentAltitudeIndex, 0);

                currentAltitudeIndex++;
            }

            int itemsToAdd = Math.min(CHUNK_SIZE - chunk.size(), buffer.size());
            chunk.addAll(buffer.subList(0, itemsToAdd));
            buffer = new ArrayList<>(buffer.subList(itemsToAdd, buffer.size()));
        }

        if (chunk.isEmpty()) {
            isCompleted = true;
            return null;
        }

//        log.info("Returning chunk with size: {}", chunk.size());
        return chunk;
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
                .pressure(ISOBARIC_ALTITUDE[altitude])
                .uVector(uVectorArray[y][x])
                .vVector(vVectorArray[y][x])
                .build();
    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        // 이후 처리가 필요할 경우 여기서 추가 가능
        return ExitStatus.COMPLETED;
    }
}
