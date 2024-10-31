package k_paas.balloon.keeper.application.advice;

public record ErrorResponse(
        String message,
        int errorCode
) {
}