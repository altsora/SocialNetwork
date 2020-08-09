package sn.utils;

import java.sql.Timestamp;
import java.time.*;

public final class TimeUtil {
    public final static ZoneId TIME_ZONE = ZoneId.systemDefault();
    public final static ZoneOffset ZONE_OFFSET = ZoneOffset.UTC;

    public static long getTimestampFromLocalDateTime(LocalDateTime localDateTime) {
        return localDateTime.toInstant(ZONE_OFFSET).getEpochSecond();
    }

    public static LocalDateTime getLocalDateTimeFromTimestamp(long timestamp) {
        return LocalDateTime.ofInstant(Instant.ofEpochSecond(timestamp), TIME_ZONE);
    }

    public static long getTimestampFromLocalDate(LocalDate localDate) {
        return getTimestampFromLocalDateTime(localDate.atStartOfDay());
    }
}
