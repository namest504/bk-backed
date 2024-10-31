package k_paas.balloon.keeper.infrastructure.persistence.repository;


import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BalloonComment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "content")
    private String content;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "balloon_position_id")
    private BalloonPosition balloonPosition;

    @Builder
    public BalloonComment(String content, BalloonPosition balloonPosition) {
        this.content = content;
        this.balloonPosition = balloonPosition;
        this.createdAt = LocalDateTime.now();
    }
}
