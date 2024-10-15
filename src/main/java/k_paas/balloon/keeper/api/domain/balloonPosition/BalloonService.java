package k_paas.balloon.keeper.api.domain.balloonPosition;

import java.util.List;
import k_paas.balloon.keeper.infrastructure.persistence.database.BalloonCommentRepository;
import k_paas.balloon.keeper.infrastructure.persistence.database.BalloonPositionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BalloonService {

    private final BalloonPositionRepository balloonPositionRepository;
    private final BalloonCommentRepository balloonCommentRepository;

    @Transactional(readOnly = true)
    public List<BalloonPosition> findAll() {
        return balloonPositionRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Page<BalloonComment> getPagedComments(Long balloonPositionId, Pageable pageable) {
        return balloonCommentRepository.findAllCommentsWithPagination(balloonPositionId, pageable);
    }

    @Transactional
    public void createComment(Long balloonPositionId, BalloonCommentRequest request) {
        final BalloonPosition balloonPosition = balloonPositionRepository.findById(balloonPositionId)
                .orElseThrow(() -> new RuntimeException("MissMatching balloonPositionId"));

        balloonCommentRepository.save(
                BalloonComment.builder()
                        .balloonPosition(balloonPosition)
                        .content(request.content())
                        .build()
        );
    }
}
