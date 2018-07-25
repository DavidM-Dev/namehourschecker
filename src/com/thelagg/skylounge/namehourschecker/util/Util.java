package com.thelagg.skylounge.namehourschecker.util;

public class Util {
    public static String getTime(final int seconds) {
        if (seconds < 60) {
            return seconds + "s";
        }
        final int minutes = seconds / 60;
        final int s = 60 * minutes;
        final int secondsLeft = seconds - s;
        if (minutes < 60) {
            if (secondsLeft > 0) {
                return String.valueOf(minutes + "m " + secondsLeft + "s");
            }
            return String.valueOf(minutes + "m");
        }
        else {
            if (minutes < 1440) {
                final int hours = minutes / 60;
                String time = hours + "h";
                final int inMins = 60 * hours;
                final int leftOver = minutes - inMins;
                if (leftOver >= 1) {
                    time = time + " " + leftOver + "m";
                }
                if (secondsLeft > 0) {
                    time = time + " " + secondsLeft + "s";
                }
                return time;
            }
            final int days = minutes / 1440;
            String time = days + "d";
            final int inMins = 1440 * days;
            final int leftOver = minutes - inMins;
            if (leftOver >= 1) {
                if (leftOver < 60) {
                    time = time + " " + leftOver + "m";
                }
                else {
                    final int hours2 = leftOver / 60;
                    time = time + " " + hours2 + "h";
                    final int hoursInMins = 60 * hours2;
                    final int minsLeft = leftOver - hoursInMins;
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
