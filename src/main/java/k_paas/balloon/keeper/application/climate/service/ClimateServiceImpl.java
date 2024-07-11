package k_paas.balloon.keeper.application.climate.service;

import static k_paas.balloon.keeper.global.constant.ClimateContants.ARRAY_X_INDEX;
import static k_paas.balloon.keeper.global.constant.ClimateContants.ARRAY_Y_INDEX;
import static k_paas.balloon.keeper.global.constant.ClimateContants.ISOBARIC_ALTITUDE;
import static k_paas.balloon.keeper.global.constant.ClimateContants.MAX_PREDICT_HOUR;

import k_paas.balloon.keeper.application.climate.dto.ClimateApiDto;
import k_paas.balloon.keeper.application.climate.type.ClimateApiVariableType;
import k_paas.balloon.keeper.infrastructure.client.ClimateClient;
import org.springframework.stereotype.Service;

@Service
public class ClimateServiceImpl implements ClimateService {

    private final ClimateClient climateClient;

    public ClimateServiceImpl(ClimateClient climateClient) {
        this.climateClient = climateClient;
    }

    /* 기후 데이터 업데이트 함수
     * 4차원까지 구현했으며, 나머지 1차원은 인덱스가 2개 뿐이기 때문에 4차원 배열을 복사할 예정
     * 1차원은 Y 좌표, 2차원은 X 좌표, 3차원은 고도, 4차원은 기준 시간으로부터 예측 시간 */
    public void updateClimateData() {
        String[][][] resApiArray = new String[ClimateApiVariableType.values().length][][]; // variable length, Y index, X index
        ClimateApiDto[][][][] finalArray = new ClimateApiDto[ARRAY_Y_INDEX][ARRAY_X_INDEX][ISOBARIC_ALTITUDE.length][MAX_PREDICT_HOUR];//Y index, X index, altitude, predict hour

        for (int i = 0; i < ISOBARIC_ALTITUDE.length; i++) { // 고도 별
            System.out.println("#i = " + i);
            for (int j = 0; j <= MAX_PREDICT_HOUR; j++) { // 예측 시간 별
                System.out.println("#j = " + j);

                /* U, V 벡터에 대해 각각 api를 날려야 하므로
                 * resApiArray[0] 은 U 벡터에 대한 array
                 * resApiArray[1] 은 V 벡터에 대한 array */
                for (int k = 0; k < ClimateApiVariableType.values().length; k++) {
                    resApiArray[k] = climateClient.climateApiResponse(String.valueOf(ClimateApiVariableType.values()[k].getVariableNumber()),
                            String.valueOf(ISOBARIC_ALTITUDE[i]), String.valueOf(j));
                }
                /* 각 인덱스에 접근하여 각 값에 해당하는 dto 생성 및 DB에 저장할 배열에 저장 */
                for (int l = 0; l < ARRAY_Y_INDEX; l++) {
                    for (int n = 0; n < ARRAY_X_INDEX; n++) {
                        ClimateApiDto climateApiDto = ClimateApiDto.builder()
                                .UVector(resApiArray[0][l][n])
                                .VVector(resApiArray[1][l][n])
                                .pressure(ISOBARIC_ALTITUDE[i])
                                .predictHour(j)
                                .build();

                        finalArray[l][n][i][j] = climateApiDto;
                    }
                }
            }
        }
        System.out.println("success!");
    }
}
