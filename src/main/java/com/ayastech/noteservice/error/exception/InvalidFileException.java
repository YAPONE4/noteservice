package com.ayastech.noteservice.error.exception;

public class InvalidFileException extends RuntimeException {
    public InvalidFileException() {
        super("Selected file is invalid");
    }
}
