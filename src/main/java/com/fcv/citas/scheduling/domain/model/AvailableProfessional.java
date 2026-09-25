package com.fcv.citas.scheduling.domain.model;
import java.time.LocalDateTime;
import java.util.List;
public record AvailableProfessional(Long id, String name, List<Window> slots) {
    public record Window(LocalDateTime startAt, LocalDateTime endAt) {}
}
