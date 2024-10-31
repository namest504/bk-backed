package k_paas.balloon.keeper.application.domain.balloon.dto;

import jakarta.validation.constraints.NotBlank;

public record BalloonCommentRequest(
        @NotBlank
        String content
) {

}
