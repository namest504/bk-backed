package k_paas.balloon.keeper.application.climate.service;

import static k_paas.balloon.keeper.global.constant.ClimateContants.ARRAY_X_INDEX;
import static k_paas.balloon.keeper.global.constant.ClimateContants.ARRAY_Y_INDEX;
import static k_paas.balloon.keeper.global.constant.ClimateContants.ISOBARIC_ALTITUDE;
import static k_paas.balloon.keeper.global.constant.ClimateContants.MAX_PREDICT_HOUR;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import k_paas.balloon.keeper.application.climate.dto.UpdateClimateServiceSpec;
import k_paas.balloon.keeper.domain.climate.repository.ClimateRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ClimateServiceImpl implements ClimateService {

    private final ClimateAsyncService climateAsyncService;
    private final ClimateRepository climateRepository;

    public ClimateServiceImpl(ClimateAsyncService climateAsyncService, ClimateRepository climateRepository) {
        this.climateAsyncService = climateAsyncService;
        this.climateRepository = climateRepository;
    }

    public void updateClimateData() {
//        List<UpdateClimateSpec> result = new ArrayList<>();
        for (int altitude = 0; altitude < ISOBARIC_ALTITUDE.length; altitude++) {
            for (int predictHour = 0; predictHour <= MAX_PREDICT_HOUR; predictHour++) {
                List<UpdateClimateServiceSpec> updateClimateServiceSpecs = processClimateData(altitude, predictHour);

                updateClimateServiceSpecs.stream()
                        .map(m -> climateRepository.save(m.toEntity()));
//                for (UpdateClimateSpec updateClimateSpec : updateClimateSpecs) {
//                    log.info("{}", updateClimateSpec.toString());
//                }
            }
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
