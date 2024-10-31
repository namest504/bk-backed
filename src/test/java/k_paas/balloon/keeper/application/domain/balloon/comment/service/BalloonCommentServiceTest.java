package k_paas.balloon.keeper.application.domain.balloon.comment.service;

import k_paas.balloon.keeper.application.domain.balloon.comment.dto.BalloonCommentRequest;
import k_paas.balloon.keeper.global.exception.NotFoundException;
import k_paas.balloon.keeper.infrastructure.persistence.repository.BalloonComment;
import k_paas.balloon.keeper.infrastructure.persistence.repository.BalloonCommentRepository;
import k_paas.balloon.keeper.infrastructure.persistence.repository.BalloonPosition;
import k_paas.balloon.keeper.infrastructure.persistence.repository.BalloonPositionRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static k_paas.balloon.keeper.support.DomainCreateSupport.createBalloonPosition;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BalloonCommentServiceTest {

    @Mock
    private BalloonCommentRepository balloonCommentRepository;

    @Mock
    private BalloonPositionRepository balloonPositionRepository;

    @InjectMocks
    private BalloonCommentService balloonCommentService;

    @DisplayName("댓글을 작성할 때 유효한 요청이 주어지면 댓글 저장")
    @Test
    void findById_whenCreateComment_ThenShouldPersistComment() throws Exception {
        // Given
        Long validBalloonPositionId = 2L;
        String validContent = "testComment";
        BalloonCommentRequest validRequest = new BalloonCommentRequest(validContent);
        BalloonPosition validBalloonPosition = createBalloonPosition(
                validBalloonPositionId,
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

        given(balloonPositionRepository.findById(validBalloonPositionId))    // mocked repository response
                .willReturn(Optional.of(validBalloonPosition));

        // When
        balloonCommentService.createComment(validBalloonPositionId, validRequest);

        // Then
        verify(balloonCommentRepository, times(1)).save(any(BalloonComment.class));    // validate repository was called
    }

    @DisplayName("댓글을 생성할 때 잘못된 위치 ID가 제공되면 NotFound 예외 발생")
    @Test
    void findById_whenCreateComment_thenShouldThrowNotFoundException() {
        // Given
        Long invalidBalloonPositionId = 3L;
        String validContent = "This is a valid comment.";
        BalloonCommentRequest validRequest = new BalloonCommentRequest(validContent);

        when(balloonPositionRepository.findById(invalidBalloonPositionId))    // mocked repository response
                .thenReturn(Optional.empty());

        // When & Then
        assertThrows(NotFoundException.class, () ->
                balloonCommentService.createComment(invalidBalloonPositionId, validRequest));    // validate the exception thrown is correct
    }
}