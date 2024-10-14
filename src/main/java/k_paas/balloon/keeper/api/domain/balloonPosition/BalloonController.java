package k_paas.balloon.keeper.api.domain.balloonPosition;

import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.HttpStatus.OK;

import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/balloons")
@Slf4j
@RequiredArgsConstructor
public class BalloonController {

    private final BalloonService balloonService;

    @GetMapping
    public ResponseEntity<List<BalloonPosition>> getAllConcurrentBalloonPosition() {
        final List<BalloonPosition> result = balloonService.findAll();
        log.info("/api/balloons excuted");
        return ResponseEntity.status(OK)
                .body(result);
    }

    @GetMapping("/comments")
    public ResponseEntity<Page<BalloonComment>> getPagedComments(
            @ModelAttribute PageableRequest pageableRequest) {
        Pageable pageable = pageableRequest.toPageable();
        Page<BalloonComment> pagedComments = balloonService.getPagedComments(pageable);
        return ResponseEntity.ok(pagedComments);
    }

    @PostMapping("/comments")
    public ResponseEntity<Void> createComment(@RequestBody @Valid BalloonCommentRequest request) {
        balloonService.createComment(request);
        return ResponseEntity.status(NO_CONTENT)
                .build();
    }


}
