package com.ayastech.noteservice.palatine.palatineTranscription.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.web.bind.annotation.RequestParam;

public record UpdateTranscriptionRequest(
        String content,
        String status
) {
}
