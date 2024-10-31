package k_paas.balloon.keeper.application.domain.balloon.report.service;

import k_paas.balloon.keeper.application.domain.balloon.report.dto.BalloonReportDto;
import k_paas.balloon.keeper.application.domain.balloon.report.dto.BalloonReportRequest;
import k_paas.balloon.keeper.application.domain.balloon.report.dto.BalloonReportWithCount;
import k_paas.balloon.keeper.global.exception.InternalServiceConnectionException;
import k_paas.balloon.keeper.global.exception.NotFoundException;
import k_paas.balloon.keeper.global.exception.UnsupportedImageTypeException;
import k_paas.balloon.keeper.global.util.ImageValidateUtil;
import k_paas.balloon.keeper.infrastructure.client.SimulationClient;
import k_paas.balloon.keeper.infrastructure.client.SimulationImageDto;
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
import java.time.LocalDateTime;
import java.time.ZoneId;
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

    @Transactional
    public String createReportBalloonInitData(MultipartFile file, BalloonReportRequest request) {
        if (!ImageValidateUtil.isImage(file)) {
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
                        .reportTime(LocalDateTime.now(ZoneId.of("Asia/Seoul")))
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
