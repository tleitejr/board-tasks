package br.com.dio.exception;

public class BoardInvalidException extends RuntimeException {

    public BoardInvalidException(final String message) {
        super(message);
    }
}