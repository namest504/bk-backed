package k_paas.balloon.keeper.batch.climate.job;

import k_paas.balloon.keeper.batch.climate.dto.ClimateDataDto;
import k_paas.balloon.keeper.global.async.ClimateRequestAsyncWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.item.ItemReader;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static k_paas.balloon.keeper.batch.util.ClimateContants.*;

@Slf4j
@Component
public class ClimateReader implements ItemReader<List<ClimateDataDto>>, StepExecutionListener {

    private static final int CHUNK_SIZE = 100;

    private int currentAltitudeIndex;
    private boolean isCompleted = false;
    private String timestamp;
    private String predictHour;
    private List<ClimateDataDto> buffer = new ArrayList<>();

    private final ClimateRequestAsyncWrapper climateRequestAsyncWrapper;

    public ClimateReader(ClimateRequestAsyncWrapper climateRequestAsyncWrapper) {
        this.climateRequestAsyncWrapper = climateRequestAsyncWrapper;
    }

    /**
     * 새 작업 실행시 이전에 실행했던 Reader 작업 후 변수 대한 초기화 수행
     * @param stepExecution instance of {@link StepExecution}.
     */
    @Override
    public void beforeStep(StepExecution stepExecution) {
        this.timestamp = (String) stepExecution.getJobExecution().getJobParameters().getParameters().get("batchStartedTime").getValue();
        this.predictHour = (String) stepExecution.getJobExecution().getJobParameters().getParameters().get("predictHour").getValue();
        this.isCompleted = false;
        this.buffer.clear();
        this.currentAltitudeIndex = 0;
        log.info("[{}][{}][{}][{}]", timestamp, predictHour, isCompleted, currentAltitudeIndex);
    }

    @Override
    public List<ClimateDataDto> read() {
        // 작업이 끝났다면 null 처리
        if (isCompleted) {
            return null;
        }

        List<ClimateDataDto> chunk = new ArrayList<>();

        while (chunk.size() < CHUNK_SIZE && !isCompleted) {
            if (buffer.isEmpty()) {
                if (currentAltitudeIndex >= ISOBARIC_ALTITUDE.length) {
                    isCompleted = true;
                    break;
                }
                buffer = processClimateData(currentAltitudeIndex, predictHour, timestamp);

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

    /**
     * U벡터와 V벡터에 대한 값을 각각 비동기로 수행 후 통합을 수행
     * @param altitude 고도
     * @param predictHour 현재로 부터 예측 시간 (요구 사항 변경으로 인한 0 현재값으로 고정)
     * @param timeStamp
     * @return
     */
    private List<ClimateDataDto> processClimateData(int altitude, String predictHour, String timeStamp) {
        CompletableFuture<String[][]> completableUVectors = sendClimateRequest(2002, altitude, predictHour, timeStamp);
        CompletableFuture<String[][]> completableVVectors = sendClimateRequest(2003, altitude, predictHour, timeStamp);

        List<ClimateDataDto> result = CompletableFuture.allOf(completableUVectors, completableVVectors)
                .thenApply(r -> {
                    String[][] uVectorArray = completableUVectors.join();
                    String[][] vVectorArray = completableVVectors.join();
                    List<ClimateDataDto> climateDataDtos = saveClimateData(altitude, uVectorArray, vVectorArray);
                    return climateDataDtos;
                }).join();
        return result;
    }

    /**
     * 요청 작업을 비동기 방식으로 바꿔주는 Wrapper 클래스 사용
     * @param parameterIndex
     * @param altitude
     * @param predictHour
     * @param timeStamp
     * @return
     */
    private CompletableFuture<String[][]> sendClimateRequest(int parameterIndex, int altitude, String predictHour, String timeStamp) {
        return climateRequestAsyncWrapper.sendRequest(
                String.valueOf(parameterIndex),
                String.valueOf(ISOBARIC_ALTITUDE[altitude]),
                predictHour,
                timeStamp
        );
    }

    /**
     * 비동기 요청 수행 후 결과를 병합 시키는 작업 수행
     * @param altitude
     * @param predictHour
     * @param uVectorArray
     * @param vVectorArray
     * @return
     */
    private List<ClimateDataDto> saveClimateData(int altitude, String[][] uVectorArray, String[][] vVectorArray) {
        List<ClimateDataDto> result = new ArrayList<>();

        // TODO: Y 마지막 인덱스 데이터 null 발생
        for (int y = 0; y < ARRAY_Y_INDEX - 1; y++) {
            for (int x = 0; x < ARRAY_X_INDEX; x++) {
                result.add(createUpdateClimateSpec(y, x, altitude, uVectorArray, vVectorArray));
            }
        }
        return result;
    }

    /**
     * 병합 결과 DTO 로 변환 Mapper
     * @param y
     * @param x
     * @param altitude
     * @param predictHour
     * @param uVectorArray
     * @param vVectorArray
     * @return
     */
    private ClimateDataDto createUpdateClimateSpec(int y, int x, int altitude, String[][] uVectorArray, String[][] vVectorArray) {
        ClimateDataDto spec = ClimateDataDto.builder()
                .y(y)
                .x(x)
                .pressure(ISOBARIC_ALTITUDE[altitude])
                .uVector(uVectorArray[y][x])
                .vVector(vVectorArray[y][x])
                .build();

        if (!spec.isValid()) {
            throw new IllegalArgumentException("Invalid Null Field UpdateClimateServiceSpec");
        }

        return spec;
    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        // 이후 처리가 필요할 경우 여기서 추가 가능
        return ExitStatus.COMPLETED;
    }
}
