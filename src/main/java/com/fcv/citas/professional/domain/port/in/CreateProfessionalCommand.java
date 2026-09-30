package com.fcv.citas.professional.domain.port.in;

import com.fcv.citas.auth.domain.model.DocumentType;
import java.util.List;

public record CreateProfessionalCommand(String firstName, String lastName, DocumentType documentType,
        String documentNumber, String email, String phone, String rawPassword, String professionalCode,
        String licenseNumber, List<Long> specialtyIds, Long primarySpecialtyId, List<Long> locationIds) {}
