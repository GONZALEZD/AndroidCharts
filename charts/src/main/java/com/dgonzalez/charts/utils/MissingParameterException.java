package com.dgonzalez.charts.utils;

/**
 * @author david.gonzalez (gonzalez.david31@gmail.com)
 */
public class MissingParameterException extends RuntimeException {
    public MissingParameterException(String detailMessage) {
        super(detailMessage);
    }
}
