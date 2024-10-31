package k_paas.balloon.keeper.infrastructure.persistence.repository;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

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
    private LocalDateTime startPredictionTime;

    @OneToMany(mappedBy = "balloonPosition", fetch = FetchType.LAZY)
    List<BalloonComment> balloonComments;
}
