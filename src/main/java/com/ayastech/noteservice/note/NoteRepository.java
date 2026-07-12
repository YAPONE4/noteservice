package com.ayastech.noteservice.note;

import org.springframework.data.jpa.repository.JpaRepository;

public interface NoteRepository extends JpaRepository<Note, Long> {

    Note findByTitle(String title);

}
