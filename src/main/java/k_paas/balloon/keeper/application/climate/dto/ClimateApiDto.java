package k_paas.balloon.keeper.application.climate.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class ClimateApiDto {
    private String UVector;
    private String VVector;
    private Integer pressure;
    private Integer predictHour;

    @Builder
    public ClimateApiDto(String UVector, String VVector, Integer pressure, Integer predictHour) {
        this.UVector = UVector;
        this.VVector = VVector;
        this.pressure = pressure;
        this.predictHour = predictHour;
    }

    @Override
    public String toString() {
        return "ClimateApiDto {"
                + "\n" + "uVector = " + UVector
                + "\n" + "vVector = " + VVector
                + "\n" + "pressure = " + pressure
                + "\n" + "predictHour = " + predictHour
                + "\n" + "}";
    }
}
