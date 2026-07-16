package com.ayastech.noteservice.palatine.palatineTranscription.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateTranscriptionRequest (
    @NotBlank(message = "Title must not be blank")
    @Size(max = 100, message = "Title must not exceed 100 characters")
    String title,

    @NotBlank(message = "There has to be Task ID")
    String taskId,

    String status
) {
}
