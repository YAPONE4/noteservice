package com.ayastech.noteservice.note;

import com.ayastech.noteservice.note.dto.CreateNoteRequest;
import com.ayastech.noteservice.note.dto.NoteResponse;
import com.ayastech.noteservice.note.error.NoteNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class NoteService {

    private final NoteRepository noteRepository;

    public NoteService(NoteRepository noteRepository) {
        this.noteRepository = noteRepository;
    }

    private static NoteResponse toResponse(Note note) {
        return new NoteResponse(
                note.getId(),
                note.getTitle(),
                note.getContent(),
                note.getCreatedAt(),
                note.getUpdatedAt()
        );
    }

    public NoteResponse createNote(CreateNoteRequest request) {
        Note note = new Note(
                request.title(),
                request.content()
        );
        Note savedNote = noteRepository.save(note);
        return toResponse(savedNote);
    }

    public NoteResponse getById(Long id) {
        return toResponse(noteRepository.findById(id).orElseThrow(() -> new NoteNotFoundException(id)));
    }
}
