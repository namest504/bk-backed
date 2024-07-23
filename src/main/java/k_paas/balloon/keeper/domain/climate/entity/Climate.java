package k_paas.balloon.keeper.domain.climate.entity;

import jakarta.persistence.Id;
import lombok.Builder;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collation = "climate")
public class Climate {
    @Id
    private String id;
    private Integer y;
    private Integer x;
    private Integer prespredictHour;
    private Integer pressure;
    private ClimateData climateData;

    @Builder
    public Climate(Integer y, Integer x, Integer prespredictHour, Integer pressure, ClimateData climateData) {
        this.y = y;
        this.x = x;
        this.prespredictHour = prespredictHour;
        this.pressure = pressure;
        this.climateData = climateData;
    }


    public static class ClimateData {
        private String UVector;
        private String VVector;
        private Integer pressure;
        private Integer predictHour;

        @Builder
        public ClimateData(String UVector, String VVector, Integer pressure, Integer predictHour) {
            this.UVector = UVector;
            this.VVector = VVector;
            this.pressure = pressure;
            this.predictHour = predictHour;
        }
    }
}
