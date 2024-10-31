package k_paas.balloon.keeper.infrastructure.client;

import k_paas.balloon.keeper.global.exception.InternalServiceConnectionException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

import static k_paas.balloon.keeper.global.exception.InternalServiceConnectionException.SIMULATION_SERVER;

@Slf4j
@Component
public class SimulationClientImpl implements SimulationClient{

    private final static String API_URL = "http://my-detection-server-service:8003";

    private static final String CLIMATE_DATA = "/climate-data";
    private static final String CLIMATE_DATA_PARAM = "object_name";
    private static final String CLIMATE_DATA_GET = "/climate-data-get";
    private static final String API_PROCESS_IMAGE = "/api/process-image";


    private final RestTemplate restTemplate;

    public SimulationClientImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public void sendObjectPath(String path) {
        URI uri = UriComponentsBuilder.fromHttpUrl(API_URL)
                .path(CLIMATE_DATA)
                .queryParam(CLIMATE_DATA_PARAM, path)
                .build()
                .toUri();
        log.info("request url: [{}]", uri);
        ResponseEntity<String> response = restTemplate.getForEntity(uri.toString(), String.class);

        // 응답 결과 출력
        if (response.getStatusCode().is2xxSuccessful()) {
            log.info("Succeed fetch Object path data");
        } else {
            log.error("Failed fetch");
            throw new InternalServiceConnectionException(SIMULATION_SERVER);
        }
    }

    @Override
    public void initiateLearningProcess() {
        URI uri = UriComponentsBuilder.fromHttpUrl(API_URL)
                .path(CLIMATE_DATA_GET)
                .build()
                .toUri();
        log.info("request url: [{}]", uri);
        ResponseEntity<String> response = restTemplate.getForEntity(uri.toString(), String.class);

        // 응답 결과 출력
        if (response.getStatusCode().is2xxSuccessful()) {
            log.info("Succeed initiateLearningProcess");
        } else {
            log.error("Failed fetch");
            throw new InternalServiceConnectionException(SIMULATION_SERVER);
        }
    }

    @Override
    public void fetchImage(SimulationImageDto simulationImageDto) {
        URI uri = UriComponentsBuilder.fromHttpUrl(API_URL)
                .path(API_PROCESS_IMAGE)
                .build()
                .toUri();

        log.info("request url: [{}]", uri);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<SimulationImageDto> requestEntity = new HttpEntity<>(simulationImageDto, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(uri, requestEntity, String.class);

        if (response.getStatusCode().is2xxSuccessful()) {
            log.info("Succeed fetchImage");
        } else {
            log.error("Failed fetch");
            throw new InternalServiceConnectionException(SIMULATION_SERVER);
        }
    }
}
