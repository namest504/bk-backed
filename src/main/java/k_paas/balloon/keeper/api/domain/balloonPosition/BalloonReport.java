package k_paas.balloon.keeper.api.domain.balloonPosition;

import jakarta.persistence.*;
import k_paas.balloon.keeper.global.util.CodeGenerateUtil;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@Table(name = "balloon_report",
        indexes = @Index(
                columnList = "serialCode"
        )
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BalloonReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 풍선 일련 코드
     */
    private String serialCode;

    /**
     * 신고 이미지 경로
     */
    private String imagePath;

    /**
     * 오물 풍선 이미지 여부 체크 상태
     * @default null
     * 이미지 상태 값 수정은 simulation server에서 수행
     */
    private Boolean isCheckedStatus;

    /**
     * 실제 신고 위도
     */
    private Double reportedLatitude;

    /**
     * 실제 신고 경도
     */
    private Double reportedLongitude;

    /**
     * 지역 중심 좌표 위도
     */
    private Double centerLatitude;

    /**
     * 지역 중심 좌표 경도
     */
    private Double centerLongitude;

    /**
     * 지역 도로명 주소
     */
    private String streetAddress;

    private LocalDateTime createdAt;

    @Builder
    private BalloonReport(
            String imagePath,
            Double reportedLatitude,
            Double reportedLongitude,
            LocalDateTime reportTime
    ) {
        this.imagePath = imagePath;
        this.serialCode = CodeGenerateUtil.generateCode();
        this.reportedLatitude = reportedLatitude;
        this.reportedLongitude = reportedLongitude;
        this.isCheckedStatus = null; // 초기 생성시 null로 생성
        this.centerLatitude = null; // 초기 생성시 null로 생성
        this.centerLongitude = null; // 초기 생성시 null로 생성
        this.streetAddress = null; // 초기 생성시 null로 생성
        this.createdAt = reportTime;
    }
}
