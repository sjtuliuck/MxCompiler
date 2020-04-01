package com.utility;

public class ErrorHandler {
    private int errorCnt;

    public ErrorHandler() {
        errorCnt = 0;
    }

    public void error(Location location, String message) {
        errorCnt++;
        System.err.println(location.toString() + "\t" + message);
    }

    public int getErrorCnt() {
        return errorCnt;
    }
}
