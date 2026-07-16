package com.ayastech.noteservice.palatine.palatineTranscription;


import com.ayastech.noteservice.palatine.palatineTranscription.dto.TranscriptionResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping(path = "/api/audio/transcribe")
public class TranscriptionController {

    private final TranscriptionService transcriptionService;

    public TranscriptionController(TranscriptionService transcriptionService) {
        this.transcriptionService = transcriptionService;
    }

    @PostMapping
    public ResponseEntity<TranscriptionResponse> createTranscriptionFromAudioAndGetResponse(
            @RequestParam("file") MultipartFile file,
            @RequestParam(name = "title", required = false) String title
            ) {

        TranscriptionResponse response =
                transcriptionService.createTranscriptionRequestAndGetResponse(file, title);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TranscriptionResponse> getStatusAndUpdateTranscription(@PathVariable Long id) {
        TranscriptionResponse response = transcriptionService.getStatusAndUpdateTranscription(id);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }


}
