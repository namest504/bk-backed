package k_paas.balloon.keeper.infrastructure.persistence.database;

import k_paas.balloon.keeper.api.domain.balloonPosition.BalloonComment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BalloonCommentRepository extends JpaRepository<BalloonComment, Long> {

    @Query("SELECT bc FROM BalloonComment bc WHERE bc.balloonPosition.id = :balloonPositionId ORDER BY bc.createdAt DESC")
    Page<BalloonComment> findAllCommentsWithPagination(@Param("balloonPositionId") Long balloonPositionIdPageable, Pageable pageable);

}
