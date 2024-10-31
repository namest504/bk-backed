package k_paas.balloon.keeper.application.domain.balloon.comment.dto;

import jakarta.validation.constraints.NotBlank;

public record BalloonCommentRequest(
        @NotBlank
        String content
) {

}
