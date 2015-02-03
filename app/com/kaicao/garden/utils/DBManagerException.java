package com.kaicao.garden.utils;

/**
 * Created by kaicao on 26/10/14.
 */
public class DBManagerException extends ServiceException {
    private String message;
    private Throwable cause;

    public DBManagerException(String message, Throwable cause) {
        this.message = message;
        this.cause = cause;
    }

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public Throwable getCause() {
        return cause;
    }
}
