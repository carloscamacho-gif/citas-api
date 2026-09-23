package com.fcv.citas.catalog.domain.port.in;

public record CreateSpecialtyCommand(String code, String name, int durationMinutes, boolean general) {
}
