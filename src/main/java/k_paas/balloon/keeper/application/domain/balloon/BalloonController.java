package k_paas.balloon.keeper.application.domain.balloon;

import jakarta.validation.Valid;
import k_paas.balloon.keeper.application.domain.balloon.comment.dto.BalloonCommentRequest;
import k_paas.balloon.keeper.application.domain.balloon.comment.dto.BalloonCommentResponse;
import k_paas.balloon.keeper.application.domain.balloon.position.dto.BalloonPositionResponse;
import k_paas.balloon.keeper.application.domain.balloon.report.dto.BalloonReportDto;
import k_paas.balloon.keeper.application.domain.balloon.report.dto.BalloonReportRequest;
import k_paas.balloon.keeper.application.domain.balloon.report.dto.BalloonReportWithCount;
import k_paas.balloon.keeper.application.domain.balloon.report.dto.ReportBalloonImageCodeResponse;
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

    /**
     * 모든 풍선 위치를 검색
     */
    @GetMapping
    public ResponseEntity<List<BalloonPositionResponse>> getAllConcurrentBalloonPosition() {
        final List<BalloonPositionResponse> result = balloonService.findAll();
        log.info("/api/balloons excuted");

        return ResponseEntity.status(OK)
                .body(result);
    }

    /**
     * 특정 풍선에 대한 페이지 처리된 댓글 목록을 검색
     */
    @GetMapping("/{id}/comments")
    public ResponseEntity<PagedModel<BalloonCommentResponse>> getPagedComments(
            @PathVariable(value = "id") Long balloonPositionId,
            @ModelAttribute @Valid PageableRequest pageableRequest
    ) {
        final PagedModel<BalloonCommentResponse> pagedComments = balloonService.getPagedComments(balloonPositionId, pageableRequest.toPageable());

        return ResponseEntity.ok(pagedComments);
    }

    /**
     * 특정 풍선에 대한 새 댓글 생성
     */
    @PostMapping("/{id}/comments")
    public ResponseEntity<Void> createComment(
            @PathVariable(value = "id") Long balloonPositionId,
            @RequestBody @Valid BalloonCommentRequest request
    ) {
        balloonService.createComment(balloonPositionId, request);
        return ResponseEntity.status(NO_CONTENT)
                .build();
    }

    /**
     * 신고 개수가 포함된 풍선 신고 목록을 검색
     */
    @GetMapping("/report")
    public ResponseEntity<List<BalloonReportWithCount>> getReportBalloon(){
        final List<BalloonReportWithCount> response = balloonService.getReportBalloonCount();

        return ResponseEntity.status(OK)
                .body(response);
    }

    /**
     * 이미지 파일과 요청 데이터를 사용하여 초기 신고 데이터 생성
     */
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

    /**
     * Creates a report for balloon data based on the given list of codes.
     *
     * @param codes A list of strings representing balloon codes to include in the report
     * @return ResponseEntity containing a list of BalloonReportDto objects representing the reported balloon data
     */
    @GetMapping("/report/status")
    public ResponseEntity<List<BalloonReportDto>> createReportBalloonData(
            @RequestParam("codes") List<String> codes
    ) {
        final List<BalloonReportDto> reportedBalloon = balloonService.getReportedBalloon(codes);

        return ResponseEntity.status(OK)
                .body(reportedBalloon);
    }

}
