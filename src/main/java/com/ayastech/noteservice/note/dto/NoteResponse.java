package com.ayastech.noteservice.note.dto;

import java.time.Instant;

public record NoteResponse (
        Long id,
        String title,
        String content,
        Instant createdAt,
        Instant updatedAt
) {
}
