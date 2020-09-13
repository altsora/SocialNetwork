package sn.utils;

import java.time.*;

public final class TimeUtil {
    public static final ZoneId TIME_ZONE = ZoneId.of("UTC");
    public static final ZoneOffset ZONE_OFFSET = ZoneOffset.UTC;

    private TimeUtil() {
    }

    public static long getTimestampFromLocalDateTime(LocalDateTime localDateTime) {
        return localDateTime.toInstant(ZONE_OFFSET).getEpochSecond();
    }

    public static long getTimestampFromLocalDate(LocalDate localDate) {
        return getTimestampFromLocalDateTime(localDate.atStartOfDay());
    }

    public static LocalDateTime getLocalDateTimeFromTimestamp(long timestamp) {
        return LocalDateTime.ofInstant(Instant.ofEpochSecond(timestamp), TIME_ZONE);
    }

    public static LocalDate getLocalDateFromTimestamp(long timestamp) {
        return LocalDate.ofInstant(Instant.ofEpochSecond(timestamp), TIME_ZONE);
    }
}
