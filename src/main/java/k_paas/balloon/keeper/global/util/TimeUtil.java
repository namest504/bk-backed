package k_paas.balloon.keeper.global.util;

import lombok.extern.slf4j.Slf4j;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
public class TimeUtil {
    public static String getCurrentTime() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHH");
        ZoneId utcZone = ZoneId.of("UTC");

        ZonedDateTime now = ZonedDateTime.now(utcZone);
        log.info("raw timestamp = {}", now.format(formatter));

        return now.minusHours(24).format(formatter);
    }
}
