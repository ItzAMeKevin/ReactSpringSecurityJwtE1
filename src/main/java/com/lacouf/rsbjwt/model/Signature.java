package com.lacouf.rsbjwt.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Lob;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Signature {
    @Lob
    @Column(name = "signature_image")
    private byte[] imageData;

    @Column(name = "signature_content_type")
    private String contentType;

    @Column(name = "signature_created_at")
    private Instant createdAt;
}
