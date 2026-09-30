package com.fcv.citas.scheduling.domain.port.in;
import com.fcv.citas.scheduling.domain.model.AvailabilityBlock;
import java.time.LocalDate;
import java.util.List;
public interface AvailabilityBlockUseCase {
    AvailabilityBlock create(Long userId, AvailabilityBlockCommand command);
    AvailabilityBlock update(Long userId, Long blockId, AvailabilityBlockCommand command);
    void delete(Long userId, Long blockId);
    List<AvailabilityBlock> list(Long userId, LocalDate date, Long locationId);
}
