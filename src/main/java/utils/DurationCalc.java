package utils;

public final class DurationCalc {
    /**
     * Converts milliseconds into a user readable String
     * @param duration in milliseconds (has to be of type long)
     * @return the duration in such a format HH:MM:SS
     */
    public static String longToString(long duration) {
        if (duration == 0) {
            return "00:00";
        }
        String strHours;
        String strMinutes;
        String strSeconds;
        if (duration / 3600000 >= 1) {
            if (Math.floorDiv(duration, 3600000) <= 9) {
                strHours = String.format("0%s", Math.floorDiv(duration, 3600000));
            } else {
                strHours = String.format("%s", Math.floorDiv(duration, 3600000));
            }
            duration -= Math.floorDiv(duration, 3600000) * 3600000L;
            if (Math.floorDiv(duration, 60000) <= 9) {
                strMinutes = String.format("0%s", Math.floorDiv(duration, 60000));
            } else {
                strMinutes = String.format("%s", Math.floorDiv(duration, 60000));
            }
            duration -= Math.floorDiv(duration, 60000) * 60000L;
            if (Math.floorDiv(duration, 1000) <= 9) {
                strSeconds = String.format("0%s", Math.floorDiv(duration, 1000));
            } else {
                strSeconds = String.format("%s", Math.floorDiv(duration, 1000));
            }
            return String.format(
                    "%s:%s:%s",
                    strHours,
                    strMinutes,
                    strSeconds
            );
        } else {
            if (Math.floorDiv(duration, 60000) <= 9) {
                strMinutes = String.format("0%s", Math.floorDiv(duration, 60000));
            } else {
                strMinutes = String.format("%s", Math.floorDiv(duration, 60000));
            }
            duration -= Math.floorDiv(duration, 60000) * 60000L;
            if (Math.floorDiv(duration, 1000) <= 9) {
                strSeconds = String.format("0%s", Math.floorDiv(duration, 1000));
            } else {
                strSeconds = String.format("%s", Math.floorDiv(duration, 1000));
            }
            return String.format(
                    "%s:%s",
                    strMinutes,
                    strSeconds
            );
        }
    }

    /**
     * Basically like longToString, but with zeroes before the actual time
     * @param duration The duration of the Song in milliseconds
     * @return The time in such Format: 00:00:00 - HH:MM:SS
     */
    public static String longToStringPlus(long duration) {
        String strDuration = longToString(duration);
        int strDurationLength = strDuration.split(":").length;
        return switch (strDurationLength) {
            case 3 -> String.format("**00:00:00 - %s**", strDuration);
            case 2 -> String.format("**00:00 - %s**", strDuration);
            default -> String.format("**00 - %s**", strDuration);
        };
    }
}
