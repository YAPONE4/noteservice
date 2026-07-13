package com.ayastech.noteservice;

import com.ayastech.noteservice.note.Note;
import com.ayastech.noteservice.note.NoteRepository;
import com.ayastech.noteservice.note.NoteService;
import com.ayastech.noteservice.note.dto.CreateNoteRequest;
import com.ayastech.noteservice.note.dto.NoteResponse;
import com.ayastech.noteservice.note.error.NoteNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;


import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class NoteServiceTest {

    @Mock
    private NoteRepository noteRepository;

    @InjectMocks
    private NoteService noteService;

    @Test
    void testCreateNote() {
        CreateNoteRequest request = new CreateNoteRequest("Example Title", "Example Content");
        Note note = new Note("Example Title", "Example Content");
        when(noteRepository.save(any(Note.class))).thenReturn((note));

        NoteResponse noteResponse = noteService.createNote(request);

        assertEquals("Example Title", noteResponse.title());
        assertEquals("Example Content", noteResponse.content());
        ArgumentCaptor<Note> noteCaptor = ArgumentCaptor.forClass(Note.class);
        verify(noteRepository).save(noteCaptor.capture());
        Note noteInRepository = noteCaptor.getValue();
        assertEquals(request.title(), noteInRepository.getTitle());
        assertEquals(request.content(), noteInRepository.getContent());
    }

    @Test
    void testReturnNoteById() {
        Note note = new Note("ID Return Example Title", "ID Return Example Content");
        when(noteRepository.findById(1L)).thenReturn(Optional.of(note));

        NoteResponse response = noteService.getById(1L);

        assertEquals("ID Return Example Title", response.title());
        assertEquals("ID Return Example Content", response.content());
    }

    @Test
    void testThrowExceptionOnNonExistingNote() {
        when(noteRepository.findById(9999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> noteService.getById(9999L))
                .isInstanceOf(NoteNotFoundException.class)
                .hasMessageContaining("9999");
        verify(noteRepository).findById(9999L);
    }

}
