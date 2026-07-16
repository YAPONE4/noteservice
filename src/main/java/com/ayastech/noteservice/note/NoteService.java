package com.ayastech.noteservice.note;

import com.ayastech.noteservice.note.dto.CreateNoteRequest;
import com.ayastech.noteservice.note.dto.NoteResponse;
import com.ayastech.noteservice.note.dto.UpdateNoteRequest;
import com.ayastech.noteservice.error.exception.NoteNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class NoteService {

    private final NoteRepository noteRepository;

    public NoteService(NoteRepository noteRepository) {
        this.noteRepository = noteRepository;
    }

    public NoteResponse createNote(CreateNoteRequest request) {
        Note note = new Note(
                request.title(),
                request.content()
        );
        Note savedNote = noteRepository.save(note);
        return savedNote.toResponse();
    }

    private Note findNote(Long id) {
        return noteRepository.findById(id).orElseThrow(() -> new NoteNotFoundException(id));
    }

    public NoteResponse getById(Long id) {
        return findNote(id).toResponse();
    }

    public Page<NoteResponse> getNotes(
            String query,
            Pageable pageable
    ) {
        Page<Note> notes;
        if (query == null || query.isBlank()) {
            notes = noteRepository.findAll(pageable);
        } else {
            String normalizedQuery = query.trim();
            notes = noteRepository.findByTitleContainingIgnoreCase(normalizedQuery, pageable);
        }
        return notes.map(Note::toResponse);
    }

    public NoteResponse updateNote(Long id, UpdateNoteRequest request) {
        Note note = findNote(id);
        note.update(request.title(), request.content());
        noteRepository.saveAndFlush(note);
        return note.toResponse();
    }

    public void deleteNote(Long id) {
        Note note = findNote(id);
        noteRepository.delete(note);
    }
}
