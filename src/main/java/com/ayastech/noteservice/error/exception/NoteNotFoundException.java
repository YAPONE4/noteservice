package com.ayastech.noteservice.error.exception;

public class NoteNotFoundException extends RuntimeException {

    public NoteNotFoundException(Long id) {
        super("Note (id " + id + ") not found");
    }

}
