package k_paas.balloon.keeper.support;

import k_paas.balloon.keeper.infrastructure.persistence.repository.BalloonPosition;
import k_paas.balloon.keeper.infrastructure.persistence.repository.BalloonReport;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.time.LocalDateTime;

public class DomainCreateSupport {

    private DomainCreateSupport() {
    }

    public static BalloonPosition createBalloonPosition(
            Long id,
            Double latitude,
            Double longitude,
            String administrativeDistrict,
            String districtCode,
            Double risk,
            Integer reportCount,
            String status,
            LocalDateTime startPredictionTime) throws Exception {

        Constructor<BalloonPosition> constructor = BalloonPosition.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        BalloonPosition balloonPosition = constructor.newInstance();

        Field idField = BalloonPosition.class.getDeclaredField("id");
        idField.setAccessible(true);
        idField.set(balloonPosition, id);

        Field latitudeField = BalloonPosition.class.getDeclaredField("latitude");
        latitudeField.setAccessible(true);
        latitudeField.set(balloonPosition, latitude);

        Field longitudeField = BalloonPosition.class.getDeclaredField("longitude");
        longitudeField.setAccessible(true);
        longitudeField.set(balloonPosition, longitude);

        Field administrativeDistrictField = BalloonPosition.class.getDeclaredField("administrativeDistrict");
        administrativeDistrictField.setAccessible(true);
        administrativeDistrictField.set(balloonPosition, administrativeDistrict);

        Field districtCodeField = BalloonPosition.class.getDeclaredField("districtCode");
        districtCodeField.setAccessible(true);
        districtCodeField.set(balloonPosition, districtCode);

        Field riskField = BalloonPosition.class.getDeclaredField("risk");
        riskField.setAccessible(true);
        riskField.set(balloonPosition, risk);

        Field reportCountField = BalloonPosition.class.getDeclaredField("reportCount");
        reportCountField.setAccessible(true);
        reportCountField.set(balloonPosition, reportCount);

        Field statusField = BalloonPosition.class.getDeclaredField("status");
        statusField.setAccessible(true);
        statusField.set(balloonPosition, status);

        Field startPredictionTimeField = BalloonPosition.class.getDeclaredField("startPredictionTime");
        startPredictionTimeField.setAccessible(true);
        startPredictionTimeField.set(balloonPosition, startPredictionTime);

        return balloonPosition;
    }

    public static BalloonReport createBalloonReport(
            Long id,
            String serialCode,
            String imagePath,
            Boolean isCheckedStatus,
            Double reportedLatitude,
            Double reportedLongitude,
            Double centerLatitude,
            Double centerLongitude,
            String streetAddress,
            LocalDateTime createdAt) throws Exception {

        Constructor<BalloonReport> constructor = BalloonReport.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        BalloonReport balloonReport = constructor.newInstance();

        Field idField = BalloonReport.class.getDeclaredField("id");
        idField.setAccessible(true);
        idField.set(balloonReport, id);

        Field serialCodeField = BalloonReport.class.getDeclaredField("serialCode");
        serialCodeField.setAccessible(true);
        serialCodeField.set(balloonReport, serialCode);

        Field imagePathField = BalloonReport.class.getDeclaredField("imagePath");
        imagePathField.setAccessible(true);
        imagePathField.set(balloonReport, imagePath);

        Field isCheckedStatusField = BalloonReport.class.getDeclaredField("isCheckedStatus");
        isCheckedStatusField.setAccessible(true);
        isCheckedStatusField.set(balloonReport, isCheckedStatus);

        Field reportedLatitudeField = BalloonReport.class.getDeclaredField("reportedLatitude");
        reportedLatitudeField.setAccessible(true);
        reportedLatitudeField.set(balloonReport, reportedLatitude);

        Field reportedLongitudeField = BalloonReport.class.getDeclaredField("reportedLongitude");
        reportedLongitudeField.setAccessible(true);
        reportedLongitudeField.set(balloonReport, reportedLongitude);

        Field centerLatitudeField = BalloonReport.class.getDeclaredField("centerLatitude");
        centerLatitudeField.setAccessible(true);
        centerLatitudeField.set(balloonReport, centerLatitude);

        Field centerLongitudeField = BalloonReport.class.getDeclaredField("centerLongitude");
        centerLongitudeField.setAccessible(true);
        centerLongitudeField.set(balloonReport, centerLongitude);

        Field streetAddressField = BalloonReport.class.getDeclaredField("streetAddress");
        streetAddressField.setAccessible(true);
        streetAddressField.set(balloonReport, streetAddress);

        Field createdAtField = BalloonReport.class.getDeclaredField("createdAt");
        createdAtField.setAccessible(true);
        createdAtField.set(balloonReport, createdAt);

        return balloonReport;
    }
}
