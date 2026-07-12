package com.ayastech.noteservice.note.error;

public class NoteNotFoundException extends RuntimeException {

    public NoteNotFoundException(Long id) {
        super("Note number " + id + " not found");
    }

}
