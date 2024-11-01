package k_paas.balloon.keeper.application.domain.balloon.report.service;

import k_paas.balloon.keeper.application.domain.balloon.report.dto.BalloonReportDto;
import k_paas.balloon.keeper.application.domain.balloon.report.dto.BalloonReportRequest;
import k_paas.balloon.keeper.application.domain.balloon.report.dto.BalloonReportWithCount;
import k_paas.balloon.keeper.global.exception.InternalServiceConnectionException;
import k_paas.balloon.keeper.global.exception.NotFoundException;
import k_paas.balloon.keeper.global.exception.UnsupportedImageTypeException;
import k_paas.balloon.keeper.infrastructure.client.simulation.SimulationClient;
import k_paas.balloon.keeper.infrastructure.client.simulation.SimulationImageDto;
import k_paas.balloon.keeper.infrastructure.persistence.objectStorage.ncp.NcpObjectStorageService;
import k_paas.balloon.keeper.infrastructure.persistence.repository.BalloonReport;
import k_paas.balloon.keeper.infrastructure.persistence.repository.BalloonReportRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import static k_paas.balloon.keeper.global.exception.InternalServiceConnectionException.NCP_OBJECT_STORAGE;
import static k_paas.balloon.keeper.global.exception.NotFoundException.LOCAL_FILE;

@Slf4j
@Service
@RequiredArgsConstructor
public class BalloonReportService {

    private final BalloonReportRepository balloonReportRepository;
    private final SimulationClient simulationClient;
    private final NcpObjectStorageService ncpObjectStorageService;
    private final ReportImageTypeValidator reportImageTypeValidator;

    /**
     * 이미지 파일과 요청 데이터를 사용하여 새로운 풍선 신고 데이터를 저장
     *
     * @return 신고 풍선에 대한 일련 코드
     */
    @Transactional
    public String createReportBalloonInitData(MultipartFile file, BalloonReportRequest request) {
        if (!reportImageTypeValidator.isImage(file)) {
            throw new UnsupportedImageTypeException();
        }
        String objectKey = generateNcpObjectKey(file);
        if(objectKey == null) {
            throw new InternalServiceConnectionException(NCP_OBJECT_STORAGE);
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

    /**
     * 풍선 일련번호 목록을 기반으로 신고 풍선 목록을 검색
     */
    @Transactional(readOnly = true)
    public List<BalloonReportDto> getReportedBalloon(List<String> reportedBalloonSerialCodes) {
        List<BalloonReport> balloonReportsBySerialCodes = balloonReportRepository.findBalloonReportsBySerialCodes(reportedBalloonSerialCodes);
        return balloonReportsBySerialCodes.stream()
                .map(BalloonReportDto::from)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<BalloonReportWithCount> getReportBalloonCount() {
        return balloonReportRepository.findBalloonReportsWithCount();
    }

    /**
     * NCP Object Storage 에 파일을 저장하고 객체 접근 경로를 반환
     */
    private String generateNcpObjectKey(MultipartFile file) {
        String objectKey = null;
        File tempFile = null;
        try {
            tempFile = createTempFile(file.getOriginalFilename());
            file.transferTo(tempFile);
            objectKey = ncpObjectStorageService.putObject(tempFile.getAbsolutePath(), "reportImage");
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new NotFoundException(LOCAL_FILE);
        } finally {
            if (tempFile != null && tempFile.exists()) {
                tempFile.delete();
            }
        }
        return objectKey;
    }

    private File createTempFile(String originalFilename) throws IOException {
        return File.createTempFile("temp-", originalFilename);
    }
}
