package k_paas.balloon.keeper.infrastructure.client;

import java.net.URI;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Slf4j
@Component
public class SimulationClientImpl implements SimulationClient{

    private final static String API_URL = "http://my-simulation-server-service:8001";
    private final RestTemplate restTemplate;

    public SimulationClientImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public void sendObjectPath(String path) {
        URI uri = UriComponentsBuilder.fromHttpUrl(API_URL)
                .path("/climate-data")
                .queryParam("object_name", path)
                .build()
                .toUri();
        log.info("request url: [{}]", uri);
        ResponseEntity<String> response = restTemplate.getForEntity(uri.toString(), String.class);

        // 응답 결과 출력
        if (response.getStatusCode().is2xxSuccessful()) {
            log.info("Succeed fetch Object path data");
        } else {
            log.error("Failed fetch");
        }
    }

    @Override
    public void initiateLearningProcess() {
        URI uri = UriComponentsBuilder.fromHttpUrl(API_URL)
                .path("/climate-data-get")
                .build()
                .toUri();
        log.info("request url: [{}]", uri);
        ResponseEntity<String> response = restTemplate.getForEntity(uri.toString(), String.class);

        // 응답 결과 출력
        if (response.getStatusCode().is2xxSuccessful()) {
            log.info("Succeed fetch Object path data");
        } else {
            log.error("Failed fetch");
        }
    }
}
