package k_paas.balloon.keeper.application.domain.balloon.position.service;

import k_paas.balloon.keeper.application.domain.balloon.position.dto.BalloonPositionResponse;
import k_paas.balloon.keeper.infrastructure.persistence.repository.BalloonPositionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BalloonPositionService {

    private final BalloonPositionRepository balloonPositionRepository;

    @Transactional(readOnly = true)
    public List<BalloonPositionResponse> findAll() {
        return balloonPositionRepository.findPositions()
                .stream()
                .map(BalloonPositionResponse::from)
                .collect(Collectors.toList());
    }
}
