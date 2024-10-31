package k_paas.balloon.keeper.application.domain.balloon.position.service;

import k_paas.balloon.keeper.application.domain.balloon.position.dto.BalloonPositionResponse;
import k_paas.balloon.keeper.infrastructure.persistence.repository.BalloonPosition;
import k_paas.balloon.keeper.infrastructure.persistence.repository.BalloonPositionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static k_paas.balloon.keeper.support.DomainCreateSupport.createBalloonPosition;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class BalloonPositionServiceTest {

    @Mock
    private BalloonPositionRepository balloonPositionRepository;

    @InjectMocks
    private BalloonPositionService balloonPositionService;

    @Test
    void findAllTest() throws Exception {
        // Given
        BalloonPosition mockBalloonPosition = createBalloonPosition(
                1L,
                37.11350165875032,
                127.4755220357661,
                "대한민국 안성시 신흥리",
                "41550",
                99.9,
                0,
                "미처리",
                LocalDateTime.of(
                        2024,
                        10,
                        25,
                        11,
                        56,
                        25,
                        190051)
        );

        given(balloonPositionRepository.findPositionsWithinLast12Hours())
                .willReturn(List.of(mockBalloonPosition));

        // When
        List<BalloonPositionResponse> result = balloonPositionService.findAll();

        // Then
        assertThat(result).isNotEmpty();
        assertThat(result.size()).isEqualTo(1);
        assertThat(result.get(0).id()).isEqualTo(mockBalloonPosition.getId());
        assertThat(result.get(0).latitude()).isEqualTo(mockBalloonPosition.getLatitude());
        assertThat(result.get(0).longitude()).isEqualTo(mockBalloonPosition.getLongitude());
        assertThat(result.get(0).administrativeDistrict()).isEqualTo(mockBalloonPosition.getAdministrativeDistrict());
        assertThat(result.get(0).districtCode()).isEqualTo(mockBalloonPosition.getDistrictCode());
        assertThat(result.get(0).risk()).isEqualTo(mockBalloonPosition.getRisk());
        assertThat(result.get(0).reportCount()).isEqualTo(mockBalloonPosition.getReportCount());
        assertThat(result.get(0).status()).isEqualTo(mockBalloonPosition.getStatus());
        assertThat(result.get(0).startPredictionTime()).isEqualTo(mockBalloonPosition.getStartPredictionTime());
    }
}