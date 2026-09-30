package com.fcv.citas.scheduling.infrastructure.persistence;

import com.fcv.citas.scheduling.domain.model.RescheduleRequest;
import com.fcv.citas.scheduling.domain.model.RescheduleStatus;
import com.fcv.citas.scheduling.domain.port.in.RescheduleInboxFilter;
import com.fcv.citas.scheduling.domain.port.out.RescheduleRepositoryPort;
import com.fcv.citas.scheduling.infrastructure.persistence.entity.RescheduleRequestJpaEntity;
import com.fcv.citas.scheduling.infrastructure.persistence.entity.RescheduleStatusJpaEntity;
import com.fcv.citas.scheduling.infrastructure.persistence.repository.SpringDataRescheduleRepository;
import com.fcv.citas.scheduling.infrastructure.persistence.repository.SpringDataRescheduleStatusRepository;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
public class RescheduleRepositoryAdapter implements RescheduleRepositoryPort {

    private static final LocalDateTime MIN = LocalDateTime.of(1900, 1, 1, 0, 0);
    private static final LocalDateTime MAX = LocalDateTime.of(9999, 12, 31, 0, 0);

    private final SpringDataRescheduleRepository reschedules;
    private final SpringDataRescheduleStatusRepository statuses;

    private volatile Map<RescheduleStatus, Long> idByStatus;
    private volatile Map<Long, RescheduleStatus> statusById;

    public RescheduleRepositoryAdapter(SpringDataRescheduleRepository reschedules,
                                       SpringDataRescheduleStatusRepository statuses) {
        this.reschedules = reschedules;
        this.statuses = statuses;
    }

    @Override
    public RescheduleRequest save(RescheduleRequest r) {
        RescheduleRequestJpaEntity saved = reschedules.save(new RescheduleRequestJpaEntity(r.id(), r.appointmentId(),
                r.requestedByUserId(), r.requestedLocationId(), statusId(r.status()), r.previousStartAt(),
                r.previousEndAt(), r.requestedStartAt(), r.requestedEndAt(), r.decisionReason(), r.decidedByUserId(),
                r.decidedAt()));
        return toDomain(saved);
    }

    @Override
    public Optional<RescheduleRequest> findByIdForUpdate(Long id) {
        return reschedules.findByIdForUpdate(id).map(this::toDomain);
    }

    @Override
    public boolean existsPendingForAppointment(Long appointmentId) {
        return reschedules.existsByAppointmentIdAndStatusId(appointmentId, statusId(RescheduleStatus.PENDING));
    }

    @Override
    public List<RescheduleRequest> findPending(RescheduleInboxFilter filter) {
        LocalDateTime from = filter.date() == null ? MIN : filter.date().atStartOfDay();
        LocalDateTime to = filter.date() == null ? MAX : filter.date().plusDays(1).atStartOfDay();
        return reschedules.search(statusId(RescheduleStatus.PENDING), filter.locationId(), filter.professionalId(),
                        filter.specialtyId(), from, to)
                .stream().map(this::toDomain).toList();
    }

    private RescheduleRequest toDomain(RescheduleRequestJpaEntity e) {
        return new RescheduleRequest(e.getId(), e.getAppointmentId(), e.getRequestedByUserId(),
                e.getRequestedLocationId(), statusFor(e.getStatusId()), e.getPreviousStartAt(), e.getPreviousEndAt(),
                e.getRequestedStartAt(), e.getRequestedEndAt(), e.getDecisionReason(), e.getDecidedByUserId(),
                e.getDecidedAt());
    }

    private Long statusId(RescheduleStatus status) {
        return maps().idByStatus.get(status);
    }

    private RescheduleStatus statusFor(Long id) {
        return maps().statusById.get(id);
    }

    /** Los estados son un catálogo fijo: se cargan una vez y se reutilizan. */
    private StatusMaps maps() {
        if (idByStatus == null) {
            Map<RescheduleStatus, Long> ids = new EnumMap<>(RescheduleStatus.class);
            Map<Long, RescheduleStatus> byId = new HashMap<>();
            for (RescheduleStatusJpaEntity row : statuses.findAll()) {
                RescheduleStatus status = RescheduleStatus.valueOf(row.getCode());
                ids.put(status, row.getId());
                byId.put(row.getId(), status);
            }
            statusById = byId;
            idByStatus = ids;
        }
        return new StatusMaps(idByStatus, statusById);
    }

    private record StatusMaps(Map<RescheduleStatus, Long> idByStatus, Map<Long, RescheduleStatus> statusById) {
    }
}
