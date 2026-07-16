package com.ayastech.noteservice.palatine.palatineTranscription.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record TranscriptionStartResponse(
    String status,

    @JsonProperty("task_id")
    String taskId
) {
}
