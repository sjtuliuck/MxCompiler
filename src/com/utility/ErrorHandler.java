package com.utility;

public class ErrorHandler {
    private int cnt;

    public ErrorHandler() {
        cnt = 0;
    }

    public void error(Location location, String message) {
        cnt++;
        System.out.println(location.toString() + "\t" + message);
    }

    public int getCnt() {
        return cnt;
    }
}
