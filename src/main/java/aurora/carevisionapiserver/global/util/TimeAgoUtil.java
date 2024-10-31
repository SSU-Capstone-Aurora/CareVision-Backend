package aurora.carevisionapiserver.global.util;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Period;

public class TimeAgoUtil {

    private static final int MINUTES_IN_HOUR = 60;
    private static final int HOURS_IN_DAY = 24;
    private static final int DAYS_IN_MONTH = 30;
    private static final int MONTHS_IN_YEAR = 12;

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
        } else if (duration.toMinutes() < MINUTES_IN_HOUR) {
            return duration.toMinutes() + MINUTES_AGO;
        } else if (duration.toHours() < HOURS_IN_DAY) {
            return duration.toHours() + HOURS_AGO;
        } else if (period.getDays() < DAYS_IN_MONTH) {
            return period.getDays() + DAYS_AGO;
        } else if (period.getMonths() < MONTHS_IN_YEAR) {
            int months = period.getMonths() + period.getYears() * MONTHS_IN_YEAR;
            return months + MONTHS_AGO;
        } else {
            return period.getYears() + YEARS_AGO;
        }
    }
}
