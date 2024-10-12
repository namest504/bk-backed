package k_paas.balloon.keeper.api.domain;

import static org.springframework.http.HttpStatus.OK;

import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequestMapping("/api")
public class BalloonPositionController {

    private final BalloonPositionService balloonPositionService;
    private final RestTemplate restTemplate;

    public BalloonPositionController(BalloonPositionService balloonPositionService) {
        this.balloonPositionService = balloonPositionService;
    }

    @GetMapping("/balloons")
    public ResponseEntity<List<BalloonPosition>> getAllConcurrentBalloonPosition() {
        final List<BalloonPosition> result = balloonPositionService.findAll();

        return ResponseEntity.status(OK)
                .body(result);
    }

    @GetMapping("/ns")
    public String test(){
        URI uri = UriComponentsBuilder.fromHttpUrl("http://my-simulation-server-service:8001/climate-data?object_name=climate/climate_data_09:17:57.737912968.csv")
                .build()
                .toUri();
        ResponseEntity<String> response = restTemplate.getForEntity(uri.toString(), String.class);

        return response.getStatusCode() + " : " + response.getBody();
    }
}
