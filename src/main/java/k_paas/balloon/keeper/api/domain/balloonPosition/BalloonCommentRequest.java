package k_paas.balloon.keeper.api.domain.balloonPosition;

import jakarta.validation.constraints.NotBlank;

public record BalloonCommentRequest(
        @NotBlank
        String content
) {

}
