package com.fcv.citas.shared.web;

import java.time.Instant;
import java.util.List;

/** Forma común de error de la API (RF-20). `details` va vacío salvo errores de validación de campos. */
public record ApiError(Instant timestamp, int status, String error, String message, List<String> details) {

    public static ApiError of(int status, String error, String message) {
        return new ApiError(Instant.now(), status, error, message, List.of());
    }

    public static ApiError of(int status, String error, String message, List<String> details) {
        return new ApiError(Instant.now(), status, error, message, details);
    }
}
