package k_paas.balloon.keeper.batch.climate.runner;

import jakarta.validation.ValidationException;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record ClimateRunnerRequest(
        @NotNull(message = "UTC 시각은 필수입니다.")
        @Pattern(regexp = "\\d{10}", message = "UTC 시각의 형식이 잘못되었습니다. 형식은 yyyyMMddHH이어야 합니다.")
        String utcTime,

        @NotNull(message = "예측 시간은 필수입니다.")
        @Pattern(regexp = "^(00|06|12|18|24)$", message = "예측 시간은 6시간 단위로 00, 06, 12, 18, 24만 허용됩니다.")
        String predictHour
) {
    public static ClimateRunnerRequest of(String utcTime, String predictHour) {
        return new ClimateRunnerRequest(utcTime, predictHour);
    }

    public ClimateRunnerRequest(String utcTime, String predictHour) {
        if (!isValidUtcTime(utcTime)) {
            throw new ValidationException("UTC 시각은 6시간 단위로 설정되어야 합니다.");
        }
        this.utcTime = utcTime;
        this.predictHour = predictHour;
    }

    private boolean isValidUtcTime(String utcTime) {
        if (utcTime == null || utcTime.length() != 10) {
            return false;
        }

        int hour;
        try {
            hour = Integer.parseInt(utcTime.substring(8, 10));
        } catch (NumberFormatException e) {
            return false;
        }

        return hour % 6 == 0;
    }
}
