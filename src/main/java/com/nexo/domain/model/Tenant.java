package com.nexo.domain.model;

import java.util.UUID;

public class Tenant {

    private final UUID id;
    private String fullName;
    private String email;
    private String documentId;

    public Tenant(UUID id, String fullName, String email, String documentId) {
        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.documentId = documentId;
    }

    public UUID getId() {
        return id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }
}
