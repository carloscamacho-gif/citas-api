package com.fcv.citas.auth.domain.model;

import java.time.Instant;
import java.util.Set;

/**
 * Modelo de dominio de usuario. Deliberadamente ajeno a JPA/Spring: la capa de
 * infraestructura (infrastructure/persistence) es responsable de mapearlo.
 */
public class User {

    private Long id;
    private final String firstName;
    private final String lastName;
    private final DocumentType documentType;
    private final String documentNumber;
    private final String email;
    private final String phone;
    private final String passwordHash;
    private final Set<RoleName> roles;
    private final boolean active;
    private final Instant createdAt;

    public User(Long id, String firstName, String lastName, DocumentType documentType, String documentNumber,
                String email, String phone, String passwordHash, Set<RoleName> roles, boolean active, Instant createdAt) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.documentType = documentType;
        this.documentNumber = documentNumber;
        this.email = email;
        this.phone = phone;
        this.passwordHash = passwordHash;
        this.roles = roles;
        this.active = active;
        this.createdAt = createdAt;
    }

    /** Fábrica para el autorregistro de un visitante (RF-01): siempre nace con rol USER y activo. */
    public static User register(String firstName, String lastName, DocumentType documentType, String documentNumber,
                                 String email, String phone, String passwordHash) {
        return new User(null, firstName, lastName, documentType, documentNumber, email, phone, passwordHash,
                Set.of(RoleName.USER), true, Instant.now());
    }

    /** Alta administrativa de un profesional ficticio (RF-07). */
    public static User professional(String firstName, String lastName, DocumentType documentType, String documentNumber,
                                    String email, String phone, String passwordHash) {
        return new User(null, firstName, lastName, documentType, documentNumber, email, phone, passwordHash,
                Set.of(RoleName.PROFESSIONAL), true, Instant.now());
    }

    public Long getId() {
        return id;
    }

    public void assignId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public DocumentType getDocumentType() {
        return documentType;
    }

    public String getDocumentNumber() {
        return documentNumber;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public Set<RoleName> getRoles() {
        return roles;
    }

    public boolean isActive() {
        return active;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
