package k_paas.balloon.keeper.api.domain.balloonPosition;

import static org.springframework.http.HttpStatus.OK;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@Slf4j
public class BalloonPositionController {

    private final BalloonPositionService balloonPositionService;

    public BalloonPositionController(BalloonPositionService balloonPositionService) {
        this.balloonPositionService = balloonPositionService;
    }

    @GetMapping("/balloons")
    public ResponseEntity<List<BalloonPosition>> getAllConcurrentBalloonPosition() {
        final List<BalloonPosition> result = balloonPositionService.findAll();

        log.info("/api/balloons excuted");
        return ResponseEntity.status(OK)
                .body(result);
    }

    @GetMapping("/climate/data-path")
    public ResponseEntity<String> getResentBatchedCsvPath() {
        return ResponseEntity.status(OK)
                .body("done");
    }
}
