package k_paas.balloon.keeper.api.domain.balloonPosition;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "learning_result")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BalloonPosition {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "latitude")
    private Double latitude;

    @Column(name = "longitude")
    private Double longitude;

    @Column(name = "administrative_district", length = 100)
    private String administrativeDistrict;

    @Column(name = "district_code", length = 15)
    private String districtCode;

    @Column(name = "risk")
    private Double risk;

    @Column(name = "reportCount")
    private Integer reportCount;

    @Column(name = "status", length = 20)
    private String status;

    @Column(name = "start_prediction_time")
    private Instant startPredictionTime;

    @OneToMany(mappedBy = "balloonPosition")
    List<BalloonComment> balloonComments;

    public BalloonPosition(
            Double latitude,
            Double longitude,
            String administrativeDistrict,
            String districtCode,
            Double risk,
            Integer reportCount,
            String status,
            Instant startPredictionTime) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.administrativeDistrict = administrativeDistrict;
        this.districtCode = districtCode;
        this.risk = risk;
        this.reportCount = reportCount;
        this.status = status;
        this.startPredictionTime = startPredictionTime;
    }
}
