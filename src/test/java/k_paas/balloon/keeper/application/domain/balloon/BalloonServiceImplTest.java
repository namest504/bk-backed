package k_paas.balloon.keeper.application.domain.balloon;

import k_paas.balloon.keeper.application.domain.balloon.position.dto.BalloonPositionResponse;
import k_paas.balloon.keeper.application.domain.balloon.position.service.BalloonPositionService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class BalloonServiceImplTest {

    @Mock
    private BalloonPositionService balloonPositionService;

    @InjectMocks
    private BalloonServiceImpl balloonService;

    @DisplayName("모든 풍선 위치 응답")
    @Test
    public void findAll_shouldReturnAllBalloonPositions_whenBalloonPositionsArePresent() {

        // given
        BalloonPositionResponse balloon1 = new BalloonPositionResponse(
                1L, 50D, 30D, "강동구", "1234",
                0.1D, 5, "미처리", LocalDateTime.now()
        );

        BalloonPositionResponse balloon2 = new BalloonPositionResponse(
                2L, 52D, 30D, "강남구", "12345",
                0.2D, 3, "미처리", LocalDateTime.now()
        );

        List<BalloonPositionResponse> expectedBalloonPositions = Arrays.asList(balloon1, balloon2);

        given(balloonPositionService.findAll()).willReturn(expectedBalloonPositions);

        // when
        List<BalloonPositionResponse> allBalloons = balloonService.findAll();

        // then
        verify(balloonPositionService, times(1)).findAll();
        assertNotNull(allBalloons);
        assertEquals(expectedBalloonPositions, allBalloons);
    }

    @DisplayName("값이 없으면 null 응답")
    @Test
    public void findAll_shouldReturnNull_whenNoBalloonPositionsArePresent() {

        // given
        given(balloonPositionService.findAll()).willReturn(null);

        // when
        List<BalloonPositionResponse> allBalloons = balloonService.findAll();

        // then
        verify(balloonPositionService, times(1)).findAll();
        assertNull(allBalloons);
    }
}