package k_paas.balloon.keeper.infrastructure.persistence.database;

import k_paas.balloon.keeper.api.domain.balloonPosition.BalloonPosition;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BalloonPositionRepository extends JpaRepository<BalloonPosition, Long> {
}
