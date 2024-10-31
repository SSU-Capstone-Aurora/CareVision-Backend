package aurora.carevisionapiserver.global.util;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Period;

public class TimeAgoUtil {

    private static final int MINUTES_IN_HOUR = 60;
    private static final int HOURS_IN_DAY = 24;
    private static final int DAYS_IN_MONTH = 30;
    private static final int MONTHS_IN_YEAR = 12;

    public static String getTimeAgoMessage(LocalDateTime requestTime) {
        LocalDateTime now = LocalDateTime.now();
        Duration duration = Duration.between(requestTime, now);
        Period period = Period.between(requestTime.toLocalDate(), now.toLocalDate());

        if (duration.toMinutes() < 1) {
            return "방금 전";
        } else if (duration.toMinutes() < MINUTES_IN_HOUR) {
            return duration.toMinutes() + "분 전";
        } else if (duration.toHours() < HOURS_IN_DAY) {
            return duration.toHours() + "시간 전";
        } else if (period.getDays() < DAYS_IN_MONTH) {
            return period.getDays() + "일 전";
        } else if (period.getMonths() < MONTHS_IN_YEAR) {
            int months = period.getMonths() + period.getYears() * MONTHS_IN_YEAR;
            return months + "개월 전";
        } else {
            return period.getYears() + "년 전";
        }
    }
}
