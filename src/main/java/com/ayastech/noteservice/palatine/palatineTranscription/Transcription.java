package com.ayastech.noteservice.palatine.palatineTranscription;


import com.ayastech.noteservice.palatine.palatineTranscription.dto.TranscriptionResponse;
import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "transcriptions")
public class Transcription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 100, nullable = false)
    private String title;

    @Column(nullable = false, updatable = false)
    private String taskId;

    @Column
    private String status;

    @Lob
    @Column
    private String content;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @Column(nullable = false)
    private Instant updatedAt;

    public Transcription(String title, String taskId, String status) {
        this.title = title;
        this.taskId = taskId;
        this.status = status;
    }

    protected Transcription() {

    }

    public void update(String content, String status) {
        this.content = content;
        this.status = status;
    }

    public TranscriptionResponse toResponse() {
        return new TranscriptionResponse(
                this.id,
                this.title,
                this.taskId,
                this.status,
                this.content,
                this.createdAt,
                this.updatedAt
        );
    }

    @PrePersist
    private void onCreate() {
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
    }

    @PreUpdate
    private void onUpdate() {
        this.updatedAt = Instant.now();
    }

    public Long getId() {
        return id;
    }

    public String getContent() {
        return content;
    }

    public String getStatus() {
        return status;
    }

    public String getTaskId() {
        return taskId;
    }

    public String getTitle() {
        return title;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }
}
