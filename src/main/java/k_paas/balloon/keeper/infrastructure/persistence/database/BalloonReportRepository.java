package k_paas.balloon.keeper.infrastructure.persistence.database;

import k_paas.balloon.keeper.api.domain.balloonPosition.BalloonReport;
import k_paas.balloon.keeper.api.domain.balloonPosition.BalloonReportWithCount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BalloonReportRepository extends JpaRepository<BalloonReport, Long> {

    @Query("SELECT br FROM BalloonReport br WHERE br.serialCode in :serialCodes")
    List<BalloonReport> findBalloonReportsBySerialCodes(@Param("serialCodes") List<String> serialCodes);

    @Query("""
            select new k_paas.balloon.keeper.api.domain.balloonPosition.BalloonReportWithCount(br.centerLatitude, br.centerLongitude, br.streetAddress, count(br.id))
            from BalloonReport br
            where br.isCheckedStatus = true
            group by br.reportedLatitude, br.reportedLongitude, br.streetAddress
            """)
    List<BalloonReportWithCount> findBalloonReportsWithCount();
}
