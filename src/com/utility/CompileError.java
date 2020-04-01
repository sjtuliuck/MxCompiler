package com.utility;

import com.utility.Location;

public class CompileError extends RuntimeException {
    public CompileError(Location location, String message) {
        super(location.toString() + message);
    }

    public CompileError(String message) {
        super(message);
    }
}
