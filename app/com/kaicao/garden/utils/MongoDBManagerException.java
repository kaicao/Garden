package com.kaicao.garden.utils;

import javax.xml.ws.http.HTTPException;

/**
 * Created by kaicao on 26/10/14.
 */
public class MongoDBManagerException extends ServiceException {
    private String message;
    private Throwable cause;

    public MongoDBManagerException(String message, Throwable cause) {
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
