package aurora.carevisionapiserver.global.util;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Period;

public class TimeAgoUtil {

    private static final String JUST_NOW = "방금 전";
    private static final String MINUTES_AGO = "분 전";
    private static final String HOURS_AGO = "시간 전";
    private static final String DAYS_AGO = "일 전";
    private static final String MONTHS_AGO = "개월 전";
    private static final String YEARS_AGO = "년 전";

    public static String getTimeAgoMessage(LocalDateTime requestTime) {
        LocalDateTime now = LocalDateTime.now();
        Duration duration = Duration.between(requestTime, now);
        Period period = Period.between(requestTime.toLocalDate(), now.toLocalDate());

        if (duration.toMinutes() < 1) {
            return JUST_NOW;
        } else if (duration.toMinutes() < 60) {
            return duration.toMinutes() + MINUTES_AGO;
        } else if (duration.toHours() < 24) {
            return duration.toHours() + HOURS_AGO;
        } else if (period.getDays() < 30) {
            return period.getDays() + DAYS_AGO;
        } else if (period.getMonths() < 12) {
            int months = period.getMonths() + period.getYears() * 12;
            return months + MONTHS_AGO;
        } else {
            return period.getYears() + YEARS_AGO;
        }
    }
}
