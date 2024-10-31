package aurora.carevisionapiserver.global.util;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Period;

public class TimeAgoUtil {

    public static String getTimeAgoMessage(LocalDateTime requestTime) {
        LocalDateTime now = LocalDateTime.now();
        Duration duration = Duration.between(requestTime, now);
        Period period = Period.between(requestTime.toLocalDate(), now.toLocalDate());

        if (duration.toMinutes() < 1) {
            return "방금 전";
        } else if (duration.toMinutes() < 60) {
            return duration.toMinutes() + "분 전";
        } else if (duration.toHours() < 24) {
            return duration.toHours() + "시간 전";
        } else if (period.getDays() < 30) {
            return period.getDays() + "일 전";
        } else if (period.getMonths() < 12) {
            int months = period.getMonths() + period.getYears() * 12;
            return months + "개월 전";
        } else {
            return period.getYears() + "년 전";
        }
    }
}
