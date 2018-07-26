package com.thelagg.skylounge.namehourschecker.util;

public class Util {
    public static String getTime(long seconds) {
        if (seconds < 60) {
            return seconds + "s";
        }
        long minutes = seconds / 60;
        long s = 60 * minutes;
        long secondsLeft = seconds - s;
        if (minutes < 60) {
            if (secondsLeft > 0) {
                return String.valueOf(minutes + "m " + secondsLeft + "s");
            }
            return String.valueOf(minutes + "m");
        }
        else {
            if (minutes < 1440) {
                long hours = minutes / 60;
                String time = hours + "h";
                long inMins = 60 * hours;
                long leftOver = minutes - inMins;
                if (leftOver >= 1) {
                    time = time + " " + leftOver + "m";
                }
                if (secondsLeft > 0) {
                    time = time + " " + secondsLeft + "s";
                }
                return time;
            }
            long days = minutes / 1440;
            String time = days + "d";
            long inMins = 1440 * days;
            long leftOver = minutes - inMins;
            if (leftOver >= 1) {
                if (leftOver < 60) {
                    time = time + " " + leftOver + "m";
                }
                else {
                    long hours2 = leftOver / 60;
                    time = time + " " + hours2 + "h";
                    long hoursInMins = 60 * hours2;
                    long minsLeft = leftOver - hoursInMins;
                    time = time + " " + minsLeft + "m";
                }
            }
            if (secondsLeft > 0) {
                time = time + " " + secondsLeft + "s";
            }
            return time;
        }
    }
}
