package k_paas.balloon.keeper.api.domain.balloonPosition;

import java.util.List;
import java.util.stream.Collectors;
import k_paas.balloon.keeper.infrastructure.persistence.database.BalloonCommentRepository;
import k_paas.balloon.keeper.infrastructure.persistence.database.BalloonPositionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedModel;
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
    public PagedModel<BalloonCommentResponse> getPagedComments(Long balloonPositionId, Pageable pageable) {
        final Page<BalloonComment> balloonCommentPage = balloonCommentRepository.findAllCommentsWithPagination(balloonPositionId, pageable);
        List<BalloonCommentResponse> balloonCommentResponses = balloonCommentPage.stream()
                .map(balloonComment ->
                        BalloonCommentResponse.from(balloonComment)
                )
                .collect(Collectors.toList());

        return new PagedModel<>(
                new PageImpl<>(
                        balloonCommentResponses,
                        pageable,
                        balloonCommentPage.getTotalElements()
                ));
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
