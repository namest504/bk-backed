package k_paas.balloon.keeper.batch.dto;

import lombok.Builder;

public record UpdateClimateServiceSpec(
        Integer y,
        Integer x,
        Integer pressure,
        String uVector,
        String vVector
) {

    @Builder
    public UpdateClimateServiceSpec {
    }

    @Override
    public String toString() {
        return "UpdateClimateSpec{" +
                "y=" + y +
                ", x=" + x +
                ", pressure=" + pressure +
                ", uVector='" + uVector + '\'' +
                ", vVector='" + vVector + '\'' +
                '}';
    }
}
