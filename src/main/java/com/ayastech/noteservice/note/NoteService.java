package com.ayastech.noteservice.note;

import com.ayastech.noteservice.note.dto.CreateNoteRequest;
import com.ayastech.noteservice.note.dto.NoteResponse;
import com.ayastech.noteservice.note.dto.UpdateNoteRequest;
import com.ayastech.noteservice.note.error.NoteNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class NoteService {

    private final NoteRepository noteRepository;

    public NoteService(NoteRepository noteRepository) {
        this.noteRepository = noteRepository;
    }

    private NoteResponse toResponse(Note note) {
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

    private Note findNote(Long id) {
        return noteRepository.findById(id).orElseThrow(() -> new NoteNotFoundException(id));
    }

    public NoteResponse getById(Long id) {
        return toResponse(findNote(id));
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
        return notes.map(this::toResponse);
    }

    public NoteResponse updateNote(Long id, UpdateNoteRequest request) {
        Note note = findNote(id);
        note.update(request.title(), request.content());
        noteRepository.saveAndFlush(note);
        return toResponse(note);
    }

    public void deleteNote(Long id) {
        Note note = findNote(id);
        noteRepository.delete(note);
    }
}
