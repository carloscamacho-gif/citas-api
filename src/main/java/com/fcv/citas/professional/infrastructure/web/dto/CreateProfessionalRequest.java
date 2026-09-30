package com.fcv.citas.professional.infrastructure.web.dto;

import com.fcv.citas.auth.domain.model.DocumentType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;

public record CreateProfessionalRequest(@NotBlank String firstName, @NotBlank String lastName,
        @NotNull DocumentType documentType, @NotBlank String documentNumber, @Email @NotBlank String email,
        @NotBlank String phone, @Size(min=8) String password, @NotBlank String professionalCode,
        @NotBlank String licenseNumber, @NotEmpty List<Long> specialtyIds, @NotNull Long primarySpecialtyId,
        @NotEmpty List<Long> locationIds) {}
