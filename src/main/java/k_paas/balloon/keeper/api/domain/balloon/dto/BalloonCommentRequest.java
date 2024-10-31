package k_paas.balloon.keeper.api.domain.balloon.dto;

import jakarta.validation.constraints.NotBlank;

public record BalloonCommentRequest(
        @NotBlank
        String content
) {

}
