package k_paas.balloon.keeper.infrastructure.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import static k_paas.balloon.keeper.batch.ClimateContants.ARRAY_X_INDEX;
import static k_paas.balloon.keeper.batch.ClimateContants.ARRAY_Y_INDEX;

@Slf4j
@Component
public class ClimateClientImpl implements ClimateClient {

    /* 기후 api url 문자열 생성 후, 통신과 결과 값 2차원 배열에 저장하는 함수 호출 */
    private final RestTemplate restTemplate;

    public ClimateClientImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public String[][] fetchGetClimateData(String varn, String level, String predictHour, String timeStamp) {
        String baseUrl = "https://apihub.kma.go.kr/api/typ06/cgi-bin/url/nph-um_grib_xy_txt1";
        String[][] result = this.parseResponseBody(getApiResponse(buildUrl(varn, level, predictHour, timeStamp, baseUrl)));
        return result;
    }

    private String buildUrl(String varn, String level, String predictHour, String timeStamp, String baseUrl) {
        try {
            return new StringBuilder(baseUrl)
                    .append("?" + encodeParam("group", "UMKR")) /* 모델 구분(한반도 모델 -> 1.5km) */
                    .append("&" + encodeParam("nwp", "N512")) /* 모델기반 종류 (여기선 상관x) */
                    .append("&" + encodeParam("data", "P")) /* 자료 종류(P = 등압면) */
                    .append("&" + encodeParam("varn", varn)) /* 변수 종류 (grib 파일 참고) U, V 벡터를 위한 변수 */
                    .append("&" + encodeParam("level", level)) /* 고도 특정 고도만 출력 가능 (grib 파일 참고) */
                    .append("&" + encodeParam("map", "F")) /* 사용 영역 (F -> 자료 전체 영역) */
                    .append("&" + encodeParam("tmfc", timeStamp)) /* 분석 시간 */
                    .append("&" + encodeParam("hf", predictHour)) /* 예측 시간 (+00 hour) */
                    .append("&" + encodeParam("authKey", "KolWTlbHQaqJVk5WxwGqnw")) /* 발급된 API 인증키 */
                    .toString();
        } catch (Exception e) {
            throw new RuntimeException("Error while building URL", e);
        }
    }

    private String encodeParam(String key, String value) throws java.io.UnsupportedEncodingException {
        return URLEncoder.encode(key, StandardCharsets.UTF_8) + "=" + URLEncoder.encode(value, StandardCharsets.UTF_8);
    }

    private String getApiResponse(String url) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-type", "application/json; text/plain; charset=utf-8");
        HttpEntity<?> entity = new HttpEntity<>(headers);
        ResponseEntity<String> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                entity,
                String.class);
        return response.getBody();
    }

    public String[][] parseResponseBody(String responseBody) {
        String[] lines = responseBody.split("\n");
        String[][] resStringArray = new String[ARRAY_Y_INDEX][ARRAY_X_INDEX];
        StringBuilder sb = new StringBuilder();
        int resArrayIndex = 0;
        boolean appendCondition = false;

        for(String line : lines) {
                /*
                * "#j"로 시작하는 줄일 경우, 그 아래부터 값들이 나오기 때문에
                * 하나의 row에 해당하는 string을 StringBuilder로 연결한 것을 파싱하여 각각의 인덱스로 저장
                * */
            if (line.startsWith("#j")) {
                appendCondition = true;

                //가장 처음 #j 가 나올 경우, 아래 로직을 수행하면 안되므로
                if (sb.isEmpty()) {
                    continue;
                }
                resStringArray[resArrayIndex] = sb.toString().trim().split("\\s+");
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
        return resStringArray;
    }
}
