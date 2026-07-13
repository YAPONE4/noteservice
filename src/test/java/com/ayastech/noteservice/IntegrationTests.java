package com.ayastech.noteservice;

import com.ayastech.noteservice.note.Note;
import com.ayastech.noteservice.note.NoteRepository;
import com.ayastech.noteservice.note.dto.CreateNoteRequest;
import com.ayastech.noteservice.note.dto.UpdateNoteRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.hamcrest.Matchers.not;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class IntegrationTests {

    @Autowired
    private NoteRepository noteRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    void cleanDatabase() {
        noteRepository.deleteAll();
    }

    @Test
    void testCreateNote() throws Exception {
        CreateNoteRequest request = new CreateNoteRequest(
                "Integration test note",
                "Integration test note content");

        mockMvc.perform(post("/api/notes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
                .andExpect(status().isCreated())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.title").value("Integration test note"))
                .andExpect(jsonPath("$.content").value("Integration test note content"))
                .andExpect(jsonPath("$.createdAt").exists())
                .andExpect(jsonPath("$.updatedAt").exists());

        assertThat(noteRepository.count()).isEqualTo(1);
        Note savedNote = noteRepository.findAll().getFirst();
        assertEquals("Integration test note", savedNote.getTitle());
        assertEquals("Integration test note content", savedNote.getContent());
        assertThat(savedNote.getId()).isNotNull();
        assertThat(savedNote.getCreatedAt()).isNotNull();
        assertThat(savedNote.getUpdatedAt()).isNotNull();
    }

    @Test
    void testReturnBadRequestOnBlankTitle() throws Exception {
        CreateNoteRequest request = new CreateNoteRequest(
                "",
                "Integration test note content");

        mockMvc.perform(post("/api/notes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").value("Request validation failed"))
                .andExpect(jsonPath("$.path").value("/api/notes"))
                .andExpect(jsonPath("$.fieldErrors.title").exists());

        assertThat(noteRepository.count()).isZero();
    }

    @Test
    void testReturnNoteById() throws Exception {
        Note savedNote = noteRepository.saveAndFlush(new Note(
                "Integration test note",
                "Integration test note content")
        );

        mockMvc.perform(
                get("/api/notes/{id}", savedNote.getId())
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(savedNote.getId()))
                .andExpect(jsonPath("$.title").value("Integration test note"))
                .andExpect(jsonPath("$.content").value("Integration test note content"))
                .andExpect(jsonPath("$.createdAt").exists())
                .andExpect(jsonPath("$.updatedAt").exists());
    }

    @Test
    void testNoteNotFound() throws Exception {
        Long id = 9999L;

        mockMvc.perform(
                get("/api/notes/{id}", id)
        )
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message")
                        .value("Note (id " + id + ") not found"))
                .andExpect(jsonPath("$.path").value("/api/notes/" + id))
                .andExpect(jsonPath("$.fieldErrors").exists());
    }

    @Test
    void testReturnPaginatedSearchResults() throws Exception {
        noteRepository.saveAllAndFlush(List.of(
                new Note("First note", "First note content"),
                new Note("extra", "extra content"),
                new Note("Second note", "Second note content"),
                new Note("Third note", "Third note content")
        ));

        mockMvc.perform(
                get("/api/notes")
                        .param("page", "0")
                        .param("size", "2")
                        .param("query", "note")
        )
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.totalElements").value(3))
                .andExpect(jsonPath("$.totalPages").value(2))
                .andExpect(jsonPath("$.size").value(2))
                .andExpect(jsonPath("$.number").value(0))
                .andExpect(jsonPath("$.first").value(true))
                .andExpect(jsonPath("$.last").value(false));
    }

    @Test
    void testUpdateNote() throws Exception {
        Note savedNote = noteRepository.saveAndFlush(new Note(
                "Integration test note",
                "Integration test content")
        );
        UpdateNoteRequest request = new UpdateNoteRequest(
                "Updated integration test note",
                "Updated integration test content"
        );

        mockMvc.perform(
                put("/api/notes/{id}", savedNote.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated integration test note"))
                .andExpect(jsonPath("$.content").value("Updated integration test content"))
                .andExpect(jsonPath("$.createdAt").exists())
                .andExpect(jsonPath("$.updatedAt").exists());

        Note updatedNote = noteRepository.findById(savedNote.getId()).orElseThrow();
        assertThat(updatedNote.getUpdatedAt().isAfter(savedNote.getUpdatedAt()));
    }

    @Test
    void testDeleteNote() throws Exception {
        Note savedNote = noteRepository.saveAndFlush(new Note(
                "Note to Delete",
                "Note to Delete content"
            )
        );

        mockMvc.perform(
                delete("/api/notes/{id}", savedNote.getId())
        )
                .andExpect(status().isNoContent());

        assertThat(noteRepository.count()).isZero();
    }
}
