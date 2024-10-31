package k_paas.balloon.keeper.application.domain.balloon;

import k_paas.balloon.keeper.application.domain.balloon.comment.dto.BalloonCommentRequest;
import k_paas.balloon.keeper.application.domain.balloon.comment.dto.BalloonCommentResponse;
import k_paas.balloon.keeper.application.domain.balloon.comment.service.BalloonCommentService;
import k_paas.balloon.keeper.application.domain.balloon.position.dto.BalloonPositionResponse;
import k_paas.balloon.keeper.application.domain.balloon.position.service.BalloonPositionService;
import k_paas.balloon.keeper.application.domain.balloon.report.dto.BalloonReportDto;
import k_paas.balloon.keeper.application.domain.balloon.report.dto.BalloonReportRequest;
import k_paas.balloon.keeper.application.domain.balloon.report.dto.BalloonReportWithCount;
import k_paas.balloon.keeper.application.domain.balloon.report.service.BalloonReportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedModel;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class BalloonServiceImpl implements BalloonService {

    private final BalloonPositionService balloonPositionService;
    private final BalloonCommentService balloonCommentService;
    private final BalloonReportService balloonReportService;

    @Transactional(readOnly = true)
    @Override
    public List<BalloonPositionResponse> findAll() {
        return balloonPositionService.findAll();
    }

    @Transactional(readOnly = true)
    @Override
    public PagedModel<BalloonCommentResponse> getPagedComments(Long balloonPositionId, Pageable pageable) {
        return balloonCommentService.getPagedComments(balloonPositionId, pageable);
    }

    @Transactional
    @Override
    public void createComment(Long balloonPositionId, BalloonCommentRequest request) {
        balloonCommentService.createComment(balloonPositionId, request);
    }

    @Transactional
    @Override
    public String createReportBalloonInitData(MultipartFile file, BalloonReportRequest request) {
        return balloonReportService.createReportBalloonInitData(file, request);
    }

    @Transactional(readOnly = true)
    @Override
    public List<BalloonReportDto> getReportedBalloon(List<String> reportedBalloonSerialCodes) {
        return balloonReportService.getReportedBalloon(reportedBalloonSerialCodes);
    }

    @Transactional(readOnly = true)
    @Override
    public List<BalloonReportWithCount> getReportBalloonCount() {
        return balloonReportService.getReportBalloonCount();
    }
}