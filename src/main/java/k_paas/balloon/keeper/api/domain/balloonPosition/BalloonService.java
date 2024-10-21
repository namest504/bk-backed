package k_paas.balloon.keeper.api.domain.balloonPosition;

import k_paas.balloon.keeper.global.util.ImageValidateUtil;
import k_paas.balloon.keeper.infrastructure.client.SimulationClient;
import k_paas.balloon.keeper.infrastructure.client.SimulationImageDto;
import k_paas.balloon.keeper.infrastructure.persistence.database.BalloonCommentRepository;
import k_paas.balloon.keeper.infrastructure.persistence.database.BalloonPositionRepository;
import k_paas.balloon.keeper.infrastructure.persistence.database.BalloonReportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedModel;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BalloonService {

    private final BalloonPositionRepository balloonPositionRepository;
    private final BalloonCommentRepository balloonCommentRepository;
    private final SimulationClient simulationClient;
    private final BalloonReportRepository balloonReportRepository;

    @Transactional(readOnly = true)
    public List<BalloonPositionResponse> findAll() {
        return balloonPositionRepository.findPositionsWithinLast12Hours()
                .stream()
                .map(BalloonPositionResponse::from)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public PagedModel<BalloonCommentResponse> getPagedComments(Long balloonPositionId, Pageable pageable) {
        final Page<BalloonComment> balloonCommentPage = balloonCommentRepository.findAllCommentsWithPagination(balloonPositionId, pageable);
        List<BalloonCommentResponse> balloonCommentResponses = balloonCommentPage.stream()
                .map(BalloonCommentResponse::from)
                .collect(Collectors.toList());

        return new PagedModel<>(
                new PageImpl<>(
                        balloonCommentResponses,
                        pageable,
                        balloonCommentPage.getTotalElements()
                ));
    }

    @Transactional
    public void createComment(Long balloonPositionId, BalloonCommentRequest request) {
        final BalloonPosition balloonPosition = balloonPositionRepository.findById(balloonPositionId)
                .orElseThrow(() -> new RuntimeException("MissMatching balloonPositionId"));

        balloonCommentRepository.save(
                BalloonComment.builder()
                        .balloonPosition(balloonPosition)
                        .content(request.content())
                        .build()
        );
    }

    @Transactional
    public String createReportBalloonInitData(MultipartFile file, BalloonReportRequest request) {
        if(!ImageValidateUtil.isValidImage(file)) {
            throw new RuntimeException("File is not Image Format");
        }
        BalloonReport balloonReport = balloonReportRepository.save(
                BalloonReport.builder()
                        .reportedLatitude(request.latitude())
                        .reportedLongitude(request.longitude())
                        .build()
        );
        simulationClient.fetchImage(
                SimulationImageDto.of(
                        balloonReport.getId(),
                        balloonReport.getSerialCode(),
                        file
                )
        );
        return balloonReport.getSerialCode();
    }

    @Transactional(readOnly = true)
    public List<BalloonReportDto> getReportedBalloon(List<String> reportedBalloonSerialCodes) {
        List<BalloonReport> balloonReportsBySerialCodes = balloonReportRepository.findBalloonReportsBySerialCodes(reportedBalloonSerialCodes);
        return balloonReportsBySerialCodes.stream()
                .map(BalloonReportDto::from)
                .collect(Collectors.toList());
    }
}
