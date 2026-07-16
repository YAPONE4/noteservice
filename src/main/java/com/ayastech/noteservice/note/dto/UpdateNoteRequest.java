package com.ayastech.noteservice.note.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateNoteRequest (
        @NotBlank(message = "Title must not be blank")
        @Size(max = 100, message = "Title must not exceed 100 characters")
        String title,

        @Size(max = 10_000, message = "Content must not exceed 10000 characters")
        String content
) {
}
