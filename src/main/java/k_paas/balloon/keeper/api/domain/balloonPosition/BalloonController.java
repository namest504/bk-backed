package k_paas.balloon.keeper.api.domain.balloonPosition;

import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.HttpStatus.OK;

import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.web.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
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

    @GetMapping("/{id}/comments")
    public ResponseEntity<PagedModel<BalloonCommentResponse>> getPagedComments(
            @PathVariable(value = "id") Long balloonPositionId,
            @ModelAttribute @Valid PageableRequest pageableRequest) {
        final PagedModel<BalloonCommentResponse> pagedComments = balloonService.getPagedComments(balloonPositionId, pageableRequest.toPageable());
        return ResponseEntity.ok(pagedComments);
    }

    @PostMapping("/{id}/comments")
    public ResponseEntity<Void> createComment(
            @PathVariable(value = "id") Long balloonPositionId,
            @RequestBody @Valid BalloonCommentRequest request) {
        balloonService.createComment(balloonPositionId, request);
        return ResponseEntity.status(NO_CONTENT)
                .build();
    }


}
