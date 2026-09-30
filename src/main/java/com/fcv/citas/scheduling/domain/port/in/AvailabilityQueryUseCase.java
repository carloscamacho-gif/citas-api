package com.fcv.citas.scheduling.domain.port.in;
import com.fcv.citas.scheduling.domain.model.AvailableProfessional;
import java.time.LocalDate;
import java.util.List;
public interface AvailabilityQueryUseCase {
    List<AvailableProfessional> search(Long locationId, Long specialtyId, Long professionalId, LocalDate date);
}
