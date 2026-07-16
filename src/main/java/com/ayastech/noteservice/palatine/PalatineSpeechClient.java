package com.ayastech.noteservice.palatine;

import com.ayastech.noteservice.palatine.palatineTranscription.dto.TranscriptionResponse;
import com.ayastech.noteservice.palatine.palatineTranscription.dto.TranscriptionStartResponse;
import com.ayastech.noteservice.palatine.palatineTranscription.dto.UpdateTranscriptionRequest;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;
import org.springframework.web.multipart.MultipartFile;

@Component
public class PalatineSpeechClient {

    private final RestClient restClient;
    private final PalatineProperties properties;

    public PalatineSpeechClient (
            RestClient.Builder builder,
            PalatineProperties properties
    ) {
        this.properties = properties;
        this.restClient = builder
                .baseUrl(properties.baseUrl())
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + properties.token())
                .build();
    }

    public TranscriptionStartResponse transcribe(MultipartFile file) {
        MultiValueMap<String, Object> parts = new LinkedMultiValueMap<>();

        parts.add("file", file.getResource());

        return restClient.post()
                .uri(uriBuilder -> uriBuilder
                        .path("/transcribe/do_transcribe")
                        .queryParam("model", properties.model())
                        .build()

                )
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .accept(MediaType.APPLICATION_JSON)
                .body(parts)
                .retrieve()
                .body(TranscriptionStartResponse.class);
    }

    public JsonNode getResult(String taskId) {
        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/transcribe/task_status/{taskId}")
                        .build(taskId)
                )
                .retrieve()
                .body(JsonNode.class);
    }
}
