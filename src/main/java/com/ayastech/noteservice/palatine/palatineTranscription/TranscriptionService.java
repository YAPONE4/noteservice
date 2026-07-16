package com.ayastech.noteservice.palatine.palatineTranscription;

import com.ayastech.noteservice.error.exception.InvalidFileException;
import com.ayastech.noteservice.error.exception.NoteNotFoundException;
import com.ayastech.noteservice.note.NoteService;
import com.ayastech.noteservice.palatine.PalatineSpeechClient;
import com.ayastech.noteservice.palatine.palatineTranscription.dto.CreateTranscriptionRequest;
import com.ayastech.noteservice.palatine.palatineTranscription.dto.TranscriptionResponse;
import com.ayastech.noteservice.palatine.palatineTranscription.dto.TranscriptionStartResponse;
import com.ayastech.noteservice.palatine.palatineTranscription.dto.UpdateTranscriptionRequest;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Objects;


@Service
public class TranscriptionService {

    private final PalatineSpeechClient speechClient;
    private final NoteService noteService;
    private final TranscriptionRepository transcriptionRepository;

    public TranscriptionService(
            PalatineSpeechClient speechClient,
            NoteService noteService,
            TranscriptionRepository transcriptionRepository
    ) {
        this.speechClient = speechClient;
        this.noteService = noteService;
        this.transcriptionRepository = transcriptionRepository;
    }

    public TranscriptionResponse createTranscription(CreateTranscriptionRequest request) {
        Transcription transcription = new Transcription(
                request.title(),
                request.taskId(),
                request.status()
        );

        Transcription savedTranscription = transcriptionRepository.save(transcription);

        return savedTranscription.toResponse();
    }

    public Transcription findTranscription(Long id) {
        return transcriptionRepository.findById(id).orElseThrow(() -> new NoteNotFoundException(id));
    }

    public TranscriptionResponse updateTranscription(Long id, UpdateTranscriptionRequest request) {
        Transcription transcription = findTranscription(id);
        transcription.update(
                request.content(),
                request.status()
        );
        return transcriptionRepository.save(transcription).toResponse();
    }

    public TranscriptionResponse createTranscriptionRequestAndGetResponse(MultipartFile file, String fileTitle) {

        if (file.isEmpty() || !Objects.equals(file.getContentType(), "audio/mpeg")) {
            throw new InvalidFileException();
        }

        TranscriptionStartResponse response = speechClient.transcribe(file);

        String transcriptionTitle = fileTitle ==
                null || fileTitle.isBlank()
                ? createTitleFromFilename(file)
                : fileTitle.trim();

        CreateTranscriptionRequest request = new CreateTranscriptionRequest(
                transcriptionTitle,
                response.taskId(),
                response.status()
        );

        return createTranscription(request);
    }

    public TranscriptionResponse getStatusAndUpdateTranscription(Long id) {

        Transcription transcription = findTranscription(id);
        JsonNode result = speechClient.getResult(transcription.getTaskId());

        UpdateTranscriptionRequest updateTranscriptionRequest = new UpdateTranscriptionRequest(
                result.toString(),
                result.get("status").asText()
        );

        return updateTranscription(id, updateTranscriptionRequest);
    }




    private String createTitleFromFilename(MultipartFile file) {
        String filename = file.getOriginalFilename();

        if (filename == null || filename.isBlank()) {
            return "Audio transcription";
        }

        return "Transcription: " + filename;

    }
}