package k_paas.balloon.keeper.infrastructure.persistence.repository;

import k_paas.balloon.keeper.api.domain.balloonPosition.BalloonPosition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface BalloonPositionRepository extends JpaRepository<BalloonPosition, Long> {

//    @Query("""
//            SELECT bp
//            FROM BalloonPosition bp
//            WHERE FUNCTION('TIMESTAMPDIFF', HOUR, bp.startPredictionTime, CURRENT_TIMESTAMP) <= 12 ORDER BY bp.startPredictionTime DESC
//            """)
//    List<BalloonPosition> findPositionsWithinLast12Hours();

    @Query("SELECT bp FROM BalloonPosition bp")
    List<BalloonPosition> findPositionsWithinLast12Hours();
}
