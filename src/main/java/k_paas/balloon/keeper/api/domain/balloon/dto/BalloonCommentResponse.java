package k_paas.balloon.keeper.api.domain.balloon.dto;

import k_paas.balloon.keeper.infrastructure.persistence.repository.BalloonComment;

import java.time.LocalDateTime;

public record BalloonCommentResponse(
        Long id,
        String content,
        LocalDateTime createdAt
) {

    public static BalloonCommentResponse from(BalloonComment comment) {
        return new BalloonCommentResponse(
                comment.getId(),
                comment.getContent(),
                comment.getCreatedAt()
        );
    }
}
