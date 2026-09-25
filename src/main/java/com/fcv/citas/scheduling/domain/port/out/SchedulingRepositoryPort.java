package com.fcv.citas.scheduling.domain.port.out;
import com.fcv.citas.scheduling.domain.model.AvailabilityBlock;
import com.fcv.citas.scheduling.domain.model.AvailabilitySlot;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface SchedulingRepositoryPort {
    AvailabilityBlock saveBlock(AvailabilityBlock block);
    Optional<AvailabilityBlock> findBlock(Long id);
    List<AvailabilityBlock> findBlocks(Long professionalId, LocalDate date, Long locationId);
    boolean overlaps(Long professionalId, LocalDateTime startAt, LocalDateTime endAt, Long excludedBlockId);
    boolean hasCommittedSlots(Long blockId);
    void deleteBlock(Long blockId);
    void replaceSlots(Long blockId, LocalDateTime startAt, LocalDateTime endAt);
    List<AvailabilitySlot> findFreeSlots(List<Long> blockIds);
    List<AvailabilitySlot> lockRequiredSlots(Long professionalId, Long locationId, LocalDateTime startAt, int count);
    void assignSlots(List<Long> slotIds, Long appointmentId);
    void releaseSlots(Long appointmentId);
}
