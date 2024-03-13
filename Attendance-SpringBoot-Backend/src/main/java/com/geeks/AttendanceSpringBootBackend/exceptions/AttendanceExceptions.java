package com.geeks.AttendanceSpringBootBackend.exceptions;

public class AttendanceExceptions extends RuntimeException {

    private String message;

    public AttendanceExceptions() {}

    public AttendanceExceptions(String msg) {
        super(msg);
        this.message = msg;
    }
}
