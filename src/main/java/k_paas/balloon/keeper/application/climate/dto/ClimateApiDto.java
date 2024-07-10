package k_paas.balloon.keeper.application.climate.dto;

import lombok.Builder;

@Builder
public class ClimateApiDto {
    private String UVector;
    private String VVector;
    private Integer pressure;
    private Integer predictHour;
}
