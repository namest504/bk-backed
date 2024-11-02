package k_paas.balloon.keeper.infrastructure.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface BalloonPositionRepository extends JpaRepository<BalloonPosition, Long> {

    // 시연을 위해 임시로 12시간 조건 해제
    @Query("SELECT bp FROM BalloonPosition bp")
    List<BalloonPosition> findPositions();
}
