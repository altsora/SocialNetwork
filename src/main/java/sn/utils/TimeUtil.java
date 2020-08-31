package sn.utils;

import java.time.*;

public final class TimeUtil {
    public static final ZoneId TIME_ZONE = ZoneId.of("UTC");
    public static final ZoneOffset ZONE_OFFSET = ZoneOffset.UTC;

    private TimeUtil() {
    }

    public static Long getTimestampFromLocalDateTime(LocalDateTime localDateTime) {
        return localDateTime != null ? localDateTime.toInstant(ZONE_OFFSET).getEpochSecond() : null;
    }

    public static Long getTimestampFromLocalDate(LocalDate localDate) {
        return localDate != null ? getTimestampFromLocalDateTime(localDate.atStartOfDay()) : null;
    }

    public static LocalDateTime getLocalDateTimeFromTimestamp(long timestamp) {
        return LocalDateTime.ofInstant(Instant.ofEpochSecond(timestamp), TIME_ZONE);
    }

    public static LocalDate getLocalDateFromTimestamp(long timestamp) {
        return LocalDate.ofInstant(Instant.ofEpochSecond(timestamp), TIME_ZONE);
    }
}