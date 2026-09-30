package com.fcv.citas.scheduling.domain.port.in;

import com.fcv.citas.scheduling.domain.model.AppointmentDetails;

import java.time.LocalDate;
import java.util.List;

/**
 * HU-020: el PROFESSIONAL consulta su agenda (sus citas APPROVED) por día/semana y sede.
 * Solo devuelve las citas del profesional autenticado; nunca las de otros (RF-16).
 */
public interface ProfessionalAgendaUseCase {
    List<AppointmentDetails> agenda(Long professionalUserId, LocalDate from, LocalDate to, Long locationId);
}
