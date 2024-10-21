package k_paas.balloon.keeper.infrastructure.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.URI;

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
            log.info("Succeed initiateLearningProcess");
        } else {
            log.error("Failed fetch");
        }
    }

    @Override
    public void fetchImage(SimulationImageDto simulationImageDto) {
        URI uri = UriComponentsBuilder.fromHttpUrl(API_URL)
                .path("/api/process-image")
                .build()
                .toUri();

        log.info("request url: [{}]", uri);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("balloonReportId", simulationImageDto.balloonReportId());
        body.add("serialCode", simulationImageDto.serialCode());
        body.add("file", createFileResource(simulationImageDto.file()));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(uri, requestEntity, String.class);

        if (response.getStatusCode().is2xxSuccessful()) {
            log.info("Succeed fetchImage");
        } else {
            log.error("Failed fetch");
        }
    }

    private Resource createFileResource(MultipartFile file){
        ByteArrayResource byteArrayResource = null;
        try{
            byteArrayResource = new ByteArrayResource(file.getBytes()) {
                @Override
                public String getFilename() {
                    return file.getOriginalFilename();
                }
            };
        } catch (IOException e) {
            log.error("Failed fetch");
        }
        return byteArrayResource;
    }
}
