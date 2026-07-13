package com.ayastech.noteservice.note;

import com.ayastech.noteservice.note.dto.CreateNoteRequest;
import com.ayastech.noteservice.note.dto.NoteResponse;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/notes")
public class NoteController {

    private final NoteService noteService;

    public NoteController(NoteService noteService) {
        this.noteService = noteService;
    }

    @PostMapping
    public ResponseEntity<NoteResponse> createNote(@Valid @RequestBody CreateNoteRequest request) {
        NoteResponse noteResponse = noteService.createNote(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(noteResponse);
    }

    @GetMapping("/{id}")
    public NoteResponse getById(@PathVariable Long id) {
        return noteService.getById(id);
    }

    @GetMapping
    public Page<NoteResponse> getNotes(
            @RequestParam(required = false) String query,
            @PageableDefault(
                    size = 10,
                    sort = "createdAt"
            )
            Pageable pageable
    ) {
        return noteService.getNotes(query, pageable);
    }
}
