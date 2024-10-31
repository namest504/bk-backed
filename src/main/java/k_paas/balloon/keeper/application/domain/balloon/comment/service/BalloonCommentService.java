package k_paas.balloon.keeper.application.domain.balloon.comment.service;

import k_paas.balloon.keeper.application.domain.balloon.comment.dto.BalloonCommentRequest;
import k_paas.balloon.keeper.application.domain.balloon.comment.dto.BalloonCommentResponse;
import k_paas.balloon.keeper.global.exception.NotFoundException;
import k_paas.balloon.keeper.infrastructure.persistence.repository.BalloonComment;
import k_paas.balloon.keeper.infrastructure.persistence.repository.BalloonCommentRepository;
import k_paas.balloon.keeper.infrastructure.persistence.repository.BalloonPosition;
import k_paas.balloon.keeper.infrastructure.persistence.repository.BalloonPositionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedModel;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static k_paas.balloon.keeper.global.exception.NotFoundException.REQUEST;

@Service
@RequiredArgsConstructor
public class BalloonCommentService {

    private final BalloonCommentRepository balloonCommentRepository;
    private final BalloonPositionRepository balloonPositionRepository;

    @Transactional(readOnly = true)
    public PagedModel<BalloonCommentResponse> getPagedComments(Long balloonPositionId, Pageable pageable) {
        final Page<BalloonComment> balloonCommentPage = balloonCommentRepository.findAllCommentsWithPagination(balloonPositionId, pageable);
        List<BalloonCommentResponse> balloonCommentResponses = balloonCommentPage.stream()
                .map(BalloonCommentResponse::from)
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
                .orElseThrow(() -> new NotFoundException(REQUEST));

        balloonCommentRepository.save(
                BalloonComment.builder()
                        .balloonPosition(balloonPosition)
                        .content(request.content())
                        .build()
        );
    }
}
