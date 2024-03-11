package ji.vegan.backend.util;

import java.time.LocalTime;

public class DateUtils {
    public static boolean isDateRange(LocalTime currentTime, LocalTime startTime, LocalTime endTime) {
        return !currentTime.isBefore(startTime) && !currentTime.isAfter(endTime);
    }
}
