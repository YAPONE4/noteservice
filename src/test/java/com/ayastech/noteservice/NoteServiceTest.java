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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;


import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


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

    @Test
    void testFindAllNotesWhenNoQuery() {

        Pageable pageable = PageRequest.of(0, 10);

        Note note = new Note("First note", "First content");

        Page<Note> repositoryResult = new PageImpl<>(
                List.of(note),
                pageable,
                1
        );

        when(noteRepository.findAll(pageable)).thenReturn(repositoryResult);

        Page<NoteResponse> result = noteService.getNotes(null, pageable);

        assertThat(result.getContent()).hasSize(1);

        assertThat(result.getContent().getFirst().title())
                .isEqualTo("First note");

        assertThat(result.getContent().getFirst().content())
                .isEqualTo("First content");

        assertThat(result.getNumber()).isZero();
        assertThat(result.getSize()).isEqualTo(10);
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getTotalPages()).isEqualTo(1);

        verify(noteRepository).findAll(pageable);

        verify(noteRepository, never()).findByTitleContainingIgnoreCase(
                        anyString(),
                        any(Pageable.class)
                );
    }

    @Test
    void testSearchNotesWithQuery() {
        Pageable pageable = PageRequest.of(0, 10);
        Note matchingNote = new Note("Learning Spring Boot", "Spring content");
        Page<Note> repositoryResult = new PageImpl<>(List.of(matchingNote), pageable, 1);
        when(noteRepository.findByTitleContainingIgnoreCase(
                "spring",
                pageable)).thenReturn(repositoryResult);

        Page<NoteResponse> result = noteService.getNotes("  spring  ", pageable);

        assertThat(result.getContent()).hasSize(1);

        assertThat(result.getContent().getFirst().title())
                .isEqualTo("Learning Spring Boot");

        assertThat(result.getTotalElements()).isEqualTo(1);
        verify(noteRepository).findByTitleContainingIgnoreCase(
                        "spring",
                        pageable
                );

        verify(noteRepository, never())
                .findAll(any(Pageable.class));
    }
}
