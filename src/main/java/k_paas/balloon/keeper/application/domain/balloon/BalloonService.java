package k_paas.balloon.keeper.application.domain.balloon;

import k_paas.balloon.keeper.application.domain.balloon.comment.dto.BalloonCommentRequest;
import k_paas.balloon.keeper.application.domain.balloon.comment.dto.BalloonCommentResponse;
import k_paas.balloon.keeper.application.domain.balloon.position.dto.BalloonPositionResponse;
import k_paas.balloon.keeper.application.domain.balloon.report.dto.BalloonReportDto;
import k_paas.balloon.keeper.application.domain.balloon.report.dto.BalloonReportRequest;
import k_paas.balloon.keeper.application.domain.balloon.report.dto.BalloonReportWithCount;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedModel;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface BalloonService {
    List<BalloonPositionResponse> findAll();

    PagedModel<BalloonCommentResponse> getPagedComments(Long balloonPositionId, Pageable pageable);

    void createComment(Long balloonPositionId, BalloonCommentRequest request);

    String createReportBalloonInitData(MultipartFile file, BalloonReportRequest request);

    List<BalloonReportDto> getReportedBalloon(List<String> reportedBalloonSerialCodes);

    List<BalloonReportWithCount> getReportBalloonCount();
}
