package k_paas.balloon.keeper.application.domain.balloon.report.service;

import k_paas.balloon.keeper.application.domain.balloon.report.dto.BalloonReportRequest;
import k_paas.balloon.keeper.global.exception.UnsupportedImageTypeException;
import k_paas.balloon.keeper.infrastructure.client.simulation.SimulationClient;
import k_paas.balloon.keeper.infrastructure.persistence.objectStorage.ncp.NcpObjectStorageService;
import k_paas.balloon.keeper.infrastructure.persistence.repository.BalloonReport;
import k_paas.balloon.keeper.infrastructure.persistence.repository.BalloonReportRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

import static k_paas.balloon.keeper.support.DomainCreateSupport.createBalloonReport;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class BalloonReportServiceTest {

    @InjectMocks
    private BalloonReportService balloonReportService;

    @Mock
    private BalloonReportRepository balloonReportRepository;

    @Mock
    private SimulationClient simulationClient;

    @Mock
    private NcpObjectStorageService ncpObjectStorageService;

    @Mock
    private ReportImageTypeValidator reportImageTypeValidator;

    @DisplayName("신고 풍선 이미지 유효할 경우 기본 데이터 저장")
    @Test
    void createReportBalloonInitData_ValidImage() throws Exception {
        // given
        MultipartFile file = new MockMultipartFile("file", "image.png", "image/png", getClass().getResourceAsStream("/image.png"));
        given(reportImageTypeValidator.isImage(file)).willReturn(true);
        BalloonReportRequest request = new BalloonReportRequest(123.456, 78.9);

        given(ncpObjectStorageService.putObject(any(String.class), any(String.class))).willReturn("some-key-uuid");

        given(balloonReportRepository.save(any(BalloonReport.class)))
                .willAnswer(i -> createBalloonReport(
                        19L,
                        "582F9E45-D52B-4397-8983-1D9D4CB8B0FA",
                        "reportImage/tmp/temp-177787114308618921401234567890.jpeg",
                        null,
                        37.5390208,
                        127.156224,
                        null,
                        null,
                        null,
                        LocalDateTime.of(2024, 10, 25, 1, 41, 53, 992517000)
                ));

        // when
        String result = balloonReportService.createReportBalloonInitData(file, request);

        // then
        assertEquals(result, "582F9E45-D52B-4397-8983-1D9D4CB8B0FA");
        then(balloonReportRepository).should(times(1)).save(any(BalloonReport.class));
        then(simulationClient).should(times(1)).fetchImage(any());
        then(ncpObjectStorageService).should(times(1)).putObject(any(String.class), any(String.class));
    }

    @DisplayName("신고 풍선 이미지 형식 검증 실패시 UnsupportedImageTypeException 예외 처리")
    @Test
    void createReportBalloonInitData_InvalidImage() throws Exception {
        // given
        MultipartFile file = new MockMultipartFile("file", "invalid.txt", "text/plain", "Invalid content".getBytes());
        given(reportImageTypeValidator.isImage(file)).willReturn(false);
        BalloonReportRequest request = new BalloonReportRequest(123.456, 78.9);

        // when & then
        assertThrows(UnsupportedImageTypeException.class, () ->
                balloonReportService.createReportBalloonInitData(file, request)
        );

        then(balloonReportRepository).shouldHaveNoInteractions();
        then(simulationClient).shouldHaveNoInteractions();
        then(ncpObjectStorageService).shouldHaveNoInteractions();
    }
}