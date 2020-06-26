package com.kakaopay.flex.exception;

public class InvalidRequestException extends RuntimeException {

    private static final long serialVersionUID = -5160166229281497537L;

    public InvalidRequestException() {
        super();
    }

    public InvalidRequestException(String s) {
        super(s);
    }
}