package com.dgonzalez.charts.utils;

/**
 * @author david.gonzalez (gonzalez.david31@gmail.com)
 */

public class NotPermittedException extends RuntimeException {

    public NotPermittedException(String detailMessage) {
        super(detailMessage);
    }
}
