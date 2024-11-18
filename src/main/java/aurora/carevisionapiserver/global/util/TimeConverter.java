package aurora.carevisionapiserver.global.util;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

import com.google.cloud.Timestamp;

public class TimeConverter {
    public static LocalDateTime convertTimestampToLocalDateTime(Timestamp timestamp) {
        Instant instant = Instant.ofEpochSecond(timestamp.getSeconds(), timestamp.getNanos());
        return instant.atZone(ZoneId.systemDefault()).toLocalDateTime();
    }
}
