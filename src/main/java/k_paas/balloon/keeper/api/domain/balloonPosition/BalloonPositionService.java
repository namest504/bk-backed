package k_paas.balloon.keeper.api.domain.balloonPosition;

import java.util.List;
import k_paas.balloon.keeper.infrastructure.persistence.database.BalloonPositionRepository;
import org.springframework.stereotype.Service;

@Service
public class BalloonPositionService {

    private final BalloonPositionRepository balloonPositionRepository;

    public BalloonPositionService(BalloonPositionRepository balloonPositionRepository) {
        this.balloonPositionRepository = balloonPositionRepository;
    }

    public List<BalloonPosition> findAll() {
        return balloonPositionRepository.findAll();
    }
}
