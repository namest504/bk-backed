package k_paas.balloon.keeper.infrastructure.persistence.repository;

import k_paas.balloon.keeper.application.domain.balloon.report.dto.BalloonReportWithCount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BalloonReportRepository extends JpaRepository<BalloonReport, Long> {

    @Query("SELECT br FROM BalloonReport br WHERE br.serialCode in :serialCodes")
    List<BalloonReport> findBalloonReportsBySerialCodes(@Param("serialCodes") List<String> serialCodes);

    @Query("""
        select new k_paas.balloon.keeper.application.domain.balloon.report.dto.BalloonReportWithCount(br.centerLatitude, br.centerLongitude, br.streetAddress, count(br.id))
        from BalloonReport br
        where br.isCheckedStatus = true
        group by br.centerLatitude, br.centerLongitude, br.streetAddress
        """)
    List<BalloonReportWithCount> findBalloonReportsWithCount();
}
