package com.cos.photogramstart.handler.ex;

import java.util.Map;

public class CustomException extends RuntimeException {

    private static final long serialVersionUID = -807520916259076805L;

    public CustomException(String message) {
        super(message);
    }
}
