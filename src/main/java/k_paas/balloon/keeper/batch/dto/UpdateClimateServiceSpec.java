package k_paas.balloon.keeper.batch.dto;

import k_paas.balloon.keeper.domain.climate.entity.Climate;
import k_paas.balloon.keeper.domain.climate.entity.Climate.ClimateData;
import lombok.Builder;

public record UpdateClimateServiceSpec(
        Integer y,
        Integer x,
        Integer prespredictHour,
        Integer pressure,
        String UVector,
        String VVector,
        Integer pressureValue,
        Integer predictHour
) {

    @Builder
    public UpdateClimateServiceSpec {
    }

    @Override
    public String toString() {
        return "UpdateClimateSpec{" +
                "y=" + y +
                ", x=" + x +
                ", prespredictHour=" + prespredictHour +
                ", pressure=" + pressure +
                ", UVector='" + UVector + '\'' +
                ", VVector='" + VVector + '\'' +
                ", pressureValue=" + pressureValue +
                ", predictHour=" + predictHour +
                '}';
    }

    public Climate toEntity(){
        return Climate.builder()
                .y(this.y)
                .x(this.x)
                .prespredictHour(this.prespredictHour)
                .pressure(this.pressure)
                .climateData(
                        ClimateData.builder()
                                .UVector(this.UVector)
                                .VVector(this.VVector)
                                .pressure(this.pressureValue)
                                .predictHour(this.predictHour)
                        .build())
                .build();
    }
}
