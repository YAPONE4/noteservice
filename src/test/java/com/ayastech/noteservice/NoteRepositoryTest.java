package com.ayastech.noteservice;

import com.ayastech.noteservice.note.Note;
import com.ayastech.noteservice.note.NoteRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.junit.jupiter.api.Assertions.*;


@DataJpaTest
public class NoteRepositoryTest {

    @Autowired
    private NoteRepository noteRepository;

    @Test
    void testSaveAndFindNote() {

        noteRepository.save(new Note("Example Title", "Example Content"));

        Note foundNote = noteRepository.findByTitle("Example Title");

        assertNotNull(foundNote);
        assertNotNull(foundNote.getCreatedAt());
        assertNotNull(foundNote.getUpdatedAt());
        assertEquals("Example Title", foundNote.getTitle());
    }
}
