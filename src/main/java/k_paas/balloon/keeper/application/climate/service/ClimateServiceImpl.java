package k_paas.balloon.keeper.application.climate.service;

import k_paas.balloon.keeper.application.climate.dto.ClimateApiDto;
import k_paas.balloon.keeper.application.climate.type.ClimateApiVariableType;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class ClimateServiceImpl implements ClimateService{
    private static final Integer ARRAY_X_INDEX = 602; /* X 격자 갯수 */
    private static final Integer ARRAY_Y_INDEX = 781; /* Y 격자 갯수 */
    private static final Integer MAX_PREDICT_HOUR = 48; /* 기준 시간으로부터 최대 예측 시간 */
    private static final Integer[] ISOBARIC_ALTITUDE = {50, 70, 100, 150, 200, 250, 300, 350, 400, 450, 500, 550, 600, 650, 700, 750, 800, 850, 875, 900, 925, 950, 975, 1000}; /* 등압면 고도(hPa) */

    /* 기후 데이터 업데이트 함수
    * 4차원까지 구현했으며, 나머지 1차원은 인덱스가 2개 뿐이기 때문에 4차원 배열을 복사할 예정
    * 1차원은 Y 좌표, 2차원은 X 좌표, 3차원은 고도, 4차원은 기준 시간으로부터 예측 시간 */
    public void updateClimateData() throws IOException {
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
                    resApiArray[k] = climateApiResponse(String.valueOf(ClimateApiVariableType.values()[k].getVariableNumber()),
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

    /* 기후 api url 문자열 생성 후, 통신과 결과 값 2차원 배열에 저장하는 함수 호출 */
    public String[][] climateApiResponse(String varn, String level, String predictHour) throws IOException {
        StringBuilder urlBuilder = new StringBuilder("https://apihub.kma.go.kr/api/typ06/cgi-bin/url/nph-um_grib_xy_txt1"); /*URL*/
        urlBuilder.append("?" + URLEncoder.encode("group", StandardCharsets.UTF_8) + "=" + URLEncoder.encode("UMKR", StandardCharsets.UTF_8)); /* 모델 구분(한반도 모델 -> 1.5km) */
        urlBuilder.append("&" + URLEncoder.encode("nwp", StandardCharsets.UTF_8) + "=" + URLEncoder.encode("N512", StandardCharsets.UTF_8)); /* 모델기반 종류 (여기선 상관x) */
        urlBuilder.append("&" + URLEncoder.encode("data", StandardCharsets.UTF_8) + "=" + URLEncoder.encode("P", StandardCharsets.UTF_8)); /* 자료 종류(P = 등압면) */
        urlBuilder.append("&" + URLEncoder.encode("varn", StandardCharsets.UTF_8) + "=" + URLEncoder.encode(varn, StandardCharsets.UTF_8)); /* 변수 종류 (grib 파일 참고) U, V 벡터를 위한 변수 */
        urlBuilder.append("&" + URLEncoder.encode("level", StandardCharsets.UTF_8) + "=" + URLEncoder.encode(level, StandardCharsets.UTF_8)); /* 고도 특정 고도만 출력 가능 (grib 파일 참고) */
        urlBuilder.append("&" + URLEncoder.encode("map", StandardCharsets.UTF_8) + "=" + URLEncoder.encode("F", StandardCharsets.UTF_8)); /* 사용 영역 (F -> 자료 전체 영역) */
        urlBuilder.append("&" + URLEncoder.encode("tmfc", StandardCharsets.UTF_8) + "=" + URLEncoder.encode("2024070900", StandardCharsets.UTF_8)); /* 분석 시간 */
        urlBuilder.append("&" + URLEncoder.encode("hf", StandardCharsets.UTF_8) + "=" + URLEncoder.encode(predictHour, StandardCharsets.UTF_8)); /* 예측 시간 (+00 hour)*/
        urlBuilder.append("&" + URLEncoder.encode("authKey", StandardCharsets.UTF_8) + "=" + URLEncoder.encode("KolWTlbHQaqJVk5WxwGqnw", StandardCharsets.UTF_8)); /* 발급된 API 인증키 */

        return urlResponseTo2dStringArray(urlBuilder.toString());
    }

    /* api 통신 후 2차원 문자열 배열에 저장하는 로직 */
    public String[][] urlResponseTo2dStringArray(String urlBuilder) throws IOException{
        URL url = new URL(urlBuilder);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Content-type", "application/json; text/plain; charset=utf-8");
        BufferedReader rd;

        /* 통신 오류일 경우 */
        if(conn.getResponseCode() < 200 && conn.getResponseCode() > 300) {
            throw new IllegalArgumentException("잘못된 변수");
        }
        System.out.println(conn.getResponseCode());
        rd = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));
        String line;
        String[][] resStringArray = new String[ARRAY_Y_INDEX][ARRAY_X_INDEX];
        StringBuilder sb = new StringBuilder();
        int resArrayIndex = 0;
        boolean appendCondition = false;

        while ((line = rd.readLine()) != null) {
            /* "#j"로 시작하는 줄일 경우, 그 아래부터 값들이 나오기 때문에
            * 하나의 row에 해당하는 string을 StringBuilder로 연결한 것을 파싱하여 각각의 인덱스로 저장*/
            if (line.startsWith("#j")) {
                appendCondition = true;

                //가장 처음 #j 가 나올 경우, 아래 로직을 수행하면 안되므로
                if (sb.isEmpty()) {
                    continue;
                }

                resStringArray[resArrayIndex] = sb.toString().split(" ");
                resArrayIndex++;
                sb.setLength(0);
                continue;
            }

            // "# "로 시작하는 라인일 경우, 문자열 배열 파싱과는 관련없는 문자열이므로
            if (line.startsWith("# ")) {
                appendCondition = false;
            }

            if (appendCondition) {
                sb.append(line);
            }
        }

        conn.disconnect();
        rd.close();
        return resStringArray;
    }


}
