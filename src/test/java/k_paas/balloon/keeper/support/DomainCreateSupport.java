package k_paas.balloon.keeper.support;

import k_paas.balloon.keeper.infrastructure.persistence.repository.BalloonPosition;

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
}
