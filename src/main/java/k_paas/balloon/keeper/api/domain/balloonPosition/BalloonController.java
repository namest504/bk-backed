package k_paas.balloon.keeper.api.domain.balloonPosition;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.web.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/api/balloons")
@Slf4j
@RequiredArgsConstructor
public class BalloonController {

    private final BalloonService balloonService;

    @GetMapping
    public ResponseEntity<List<BalloonPositionResponse>> getAllConcurrentBalloonPosition() {
        final List<BalloonPositionResponse> result = balloonService.findAll();
        log.info("/api/balloons excuted");

        return ResponseEntity.status(OK)
                .body(result);
    }

    @GetMapping("/{id}/comments")
    public ResponseEntity<PagedModel<BalloonCommentResponse>> getPagedComments(
            @PathVariable(value = "id") Long balloonPositionId,
            @ModelAttribute @Valid PageableRequest pageableRequest
    ) {
        final PagedModel<BalloonCommentResponse> pagedComments = balloonService.getPagedComments(balloonPositionId, pageableRequest.toPageable());

        return ResponseEntity.ok(pagedComments);
    }

    @PostMapping("/{id}/comments")
    public ResponseEntity<Void> createComment(
            @PathVariable(value = "id") Long balloonPositionId,
            @RequestBody @Valid BalloonCommentRequest request
    ) {
        balloonService.createComment(balloonPositionId, request);
        return ResponseEntity.status(NO_CONTENT)
                .build();
    }

    @GetMapping("/report")
    public ResponseEntity<List<BalloonReportWithCount>> getReportBalloon(){
        final List<BalloonReportWithCount> response = balloonService.getReportBalloonCount();

        return ResponseEntity.status(OK)
                .body(response);
    }

    @PostMapping("/report")
    public ResponseEntity<ReportBalloonImageCodeResponse> reportBalloonImage(
            @RequestPart("image") MultipartFile file,
            @RequestPart BalloonReportRequest request
    ) {
        final String result = balloonService.createReportBalloonInitData(file, request);
        final ReportBalloonImageCodeResponse response = ReportBalloonImageCodeResponse.from(result);

        return ResponseEntity.status(OK)
                .body(response);
    }

    @GetMapping("/report/status")
    public ResponseEntity<List<BalloonReportDto>> createReportBalloonData(
            @RequestParam("codes") List<String> codes
    ) {
        final List<BalloonReportDto> reportedBalloon = balloonService.getReportedBalloon(codes);

        return ResponseEntity.status(OK)
                .body(reportedBalloon);
    }

}
