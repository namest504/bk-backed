package k_paas.balloon.keeper.api.domain.balloonPosition;

import k_paas.balloon.keeper.global.util.ImageValidateUtil;
import k_paas.balloon.keeper.infrastructure.client.SimulationClient;
import k_paas.balloon.keeper.infrastructure.client.SimulationImageDto;
import k_paas.balloon.keeper.infrastructure.persistence.database.BalloonCommentRepository;
import k_paas.balloon.keeper.infrastructure.persistence.database.BalloonPositionRepository;
import k_paas.balloon.keeper.infrastructure.persistence.database.BalloonReportRepository;
import k_paas.balloon.keeper.infrastructure.persistence.objectStorage.ncp.NcpObjectStorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedModel;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class BalloonService {

    private final BalloonPositionRepository balloonPositionRepository;
    private final BalloonCommentRepository balloonCommentRepository;
    private final SimulationClient simulationClient;
    private final BalloonReportRepository balloonReportRepository;
    private final NcpObjectStorageService ncpObjectStorageService;

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
        if (!ImageValidateUtil.isValidImage(file)) {
            throw new RuntimeException("File is not Image Format");
        }
        String objectKey = getNcpObjectKey(file);
        if(objectKey == null) {
            throw new RuntimeException("Failed Upload Image to NCP");
        }
        BalloonReport balloonReport = balloonReportRepository.save(
                BalloonReport.builder()
                        .imagePath(objectKey)
                        .reportedLatitude(request.latitude())
                        .reportedLongitude(request.longitude())
                        .build()
        );

        simulationClient.fetchImage(
                SimulationImageDto.of(
                        balloonReport.getId(),
                        balloonReport.getSerialCode(),
                        objectKey
                )
        );
        return balloonReport.getSerialCode();
    }

    private String getNcpObjectKey(MultipartFile file) {
        String objectKey = null;
        try {
            // 임시 파일 생성
            File tempFile = File.createTempFile("temp-", file.getOriginalFilename());
            file.transferTo(tempFile);

            // 파일 경로를 사용하여 S3에 업로드
            objectKey = ncpObjectStorageService.putObject(tempFile.getAbsolutePath(), "reportImage/");

            // 임시 파일 삭제
            tempFile.delete();
        } catch (Exception e) {
            log.error(e.getMessage());
        }

        return objectKey;
    }

    @Transactional(readOnly = true)
    public List<BalloonReportDto> getReportedBalloon(List<String> reportedBalloonSerialCodes) {
        List<BalloonReport> balloonReportsBySerialCodes = balloonReportRepository.findBalloonReportsBySerialCodes(reportedBalloonSerialCodes);
        return balloonReportsBySerialCodes.stream()
                .map(BalloonReportDto::from)
                .collect(Collectors.toList());
    }
}
