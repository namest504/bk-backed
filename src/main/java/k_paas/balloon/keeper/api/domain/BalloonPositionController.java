package k_paas.balloon.keeper.api.domain;

import static org.springframework.http.HttpStatus.OK;

import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class BalloonPositionController {

    private final BalloonPositionService balloonPositionService;

    public BalloonPositionController(BalloonPositionService balloonPositionService) {
        this.balloonPositionService = balloonPositionService;
    }

    @GetMapping("/balloons")
    public ResponseEntity<List<BalloonPosition>> getAllConcurrentBalloonPosition() {
        final List<BalloonPosition> result = balloonPositionService.findAll();

        return ResponseEntity.status(OK)
                .body(result);
    }
}
