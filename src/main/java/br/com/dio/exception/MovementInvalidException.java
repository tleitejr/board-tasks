package br.com.dio.exception;

public class MovementInvalidException extends RuntimeException {

    public MovementInvalidException(final String message) {
        super(message);
    }
}