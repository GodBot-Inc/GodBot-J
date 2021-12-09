package com.godbot.utils.audio;

import java.util.Arrays;

public final class DurationCalc {

    /**
     * Converts the duration from the passed YT String a milliseconds
     * @param duration passed in a String from YouTube
     * @return the duration converted into milliseconds
     */
    public static long ytStringToLong(String duration) {
        long time = 0;
        if (duration.contains("PT") || duration.contains("PM")) {
            duration = duration.substring(2);
        }
        if (duration.contains("H")) {
            // multiply hours with 3600000 to get milliseconds
            System.out.println(Arrays.toString(duration.split("H")));
            time += Long.parseLong(duration.split("H")[0]) * 3600000;
        }
        if (duration.contains("M")) {
            if (time != 0) {
                time += Long.parseLong(duration.split("H")[1].split("M")[0]) * 60000;
//                System.out.println(Arrays.toString(duration.split("M")));
//                System.out.println(duration.split("M")[0].substring(1));
//                time += Long.parseLong(duration.split("M")[0].substring(1)) * 60000;
            } else {
                time += Long.parseLong(duration.split("M")[0]) * 60000;
            }
        }
        if (duration.contains("S")) {
            if (duration.contains("M")) {
                time += Long.parseLong(duration.split("M")[1].split("S")[0]) * 1000;
            } else if (duration.contains("H")) {
                time += Long.parseLong(duration.split("H")[1].split("S")[0]) * 1000;
            } else {
                time += Long.parseLong(duration.split("S")[0]) * 1000;
//                time += Long.parseLong(duration.substring(0, 1)) * 1000;
            }
        }
        return time;
    }

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
}
