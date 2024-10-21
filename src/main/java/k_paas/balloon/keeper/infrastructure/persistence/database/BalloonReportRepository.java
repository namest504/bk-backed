package k_paas.balloon.keeper.infrastructure.persistence.database;

import k_paas.balloon.keeper.api.domain.balloonPosition.BalloonReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BalloonReportRepository extends JpaRepository<BalloonReport, Long> {

    @Query("SELECT br FROM BalloonReport br WHERE br.serialCode in :serialCodes")
    List<BalloonReport> findBalloonReportsBySerialCodes(@Param("serialCodes") List<String> serialCodes);
}
