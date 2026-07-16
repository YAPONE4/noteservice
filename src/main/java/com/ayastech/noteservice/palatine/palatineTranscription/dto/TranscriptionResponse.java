package com.ayastech.noteservice.palatine.palatineTranscription.dto;

import java.time.Instant;

public record TranscriptionResponse(
        Long id,
        String title,
        String taskId,
        String status,
        String content,
        Instant createdAt,
        Instant updatedAt
) {
}
