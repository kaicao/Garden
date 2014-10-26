package com.kaicao.garden.utils;

import javax.xml.ws.http.HTTPException;

/**
 * Created by kaicao on 26/10/14.
 */
public class ServiceException extends HTTPException {
    private final static int STATUS = 500;

    public ServiceException() {
        super(STATUS);
    }
}
