package com.fcv.citas.scheduling.infrastructure.persistence;

import com.fcv.citas.scheduling.domain.model.Appointment;
import com.fcv.citas.scheduling.domain.model.AppointmentStatus;
import com.fcv.citas.scheduling.domain.model.ChangeSource;
import com.fcv.citas.scheduling.domain.model.StatusHistoryEntry;
import com.fcv.citas.scheduling.domain.port.in.PendingAppointmentFilter;
import com.fcv.citas.scheduling.domain.port.out.AppointmentRepositoryPort;
import com.fcv.citas.scheduling.infrastructure.persistence.entity.AppointmentJpaEntity;
import com.fcv.citas.scheduling.infrastructure.persistence.entity.AppointmentStatusJpaEntity;
import com.fcv.citas.scheduling.infrastructure.persistence.entity.StatusHistoryJpaEntity;
import com.fcv.citas.scheduling.infrastructure.persistence.repository.SpringDataAppointmentRepository;
import com.fcv.citas.scheduling.infrastructure.persistence.repository.SpringDataAppointmentStatusRepository;
import com.fcv.citas.scheduling.infrastructure.persistence.repository.SpringDataStatusHistoryRepository;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
public class AppointmentRepositoryAdapter implements AppointmentRepositoryPort {

    private static final LocalDateTime MIN = LocalDateTime.of(1900, 1, 1, 0, 0);
    private static final LocalDateTime MAX = LocalDateTime.of(9999, 12, 31, 0, 0);

    private final SpringDataAppointmentRepository appointments;
    private final SpringDataAppointmentStatusRepository statuses;
    private final SpringDataStatusHistoryRepository history;

    private volatile Map<AppointmentStatus, Long> idByStatus;
    private volatile Map<Long, AppointmentStatus> statusById;

    public AppointmentRepositoryAdapter(SpringDataAppointmentRepository appointments,
                                        SpringDataAppointmentStatusRepository statuses,
                                        SpringDataStatusHistoryRepository history) {
        this.appointments = appointments;
        this.statuses = statuses;
        this.history = history;
    }

    @Override
    public Appointment save(Appointment a) {
        AppointmentJpaEntity saved = appointments.save(new AppointmentJpaEntity(a.id(), a.patientUserId(),
                a.professionalId(), a.locationId(), a.specialtyId(), statusId(a.status()), a.reason(),
                a.rejectionReason(), a.startAt(), a.endAt(), a.approvedByUserId(), a.approvedAt()));
        return toDomain(saved);
    }

    @Override
    public Optional<Appointment> findById(Long id) {
        return appointments.findById(id).map(this::toDomain);
    }

    @Override
    public Optional<Appointment> findByIdForUpdate(Long id) {
        return appointments.findByIdForUpdate(id).map(this::toDomain);
    }

    @Override
    public List<Appointment> findByStatus(AppointmentStatus status, PendingAppointmentFilter f) {
        LocalDateTime from = f.date() == null ? MIN : f.date().atStartOfDay();
        LocalDateTime to = f.date() == null ? MAX : f.date().plusDays(1).atStartOfDay();
        return appointments.search(statusId(status), f.locationId(), f.professionalId(), f.specialtyId(), from, to)
                .stream().map(this::toDomain).toList();
    }

    @Override
    public void addHistory(StatusHistoryEntry e) {
        history.save(new StatusHistoryJpaEntity(e.appointmentId(), statusId(e.status()), e.changedByUserId(),
                e.source().name(), e.reason(), e.changedAt()));
    }

    @Override
    public List<StatusHistoryEntry> history(Long appointmentId) {
        return history.findByAppointmentIdOrderByChangedAtAscIdAsc(appointmentId).stream()
                .map(h -> new StatusHistoryEntry(h.getAppointmentId(), statusFor(h.getStatusId()),
                        h.getChangedByUserId(), ChangeSource.valueOf(h.getChangeSource()), h.getReason(),
                        h.getChangedAt()))
                .toList();
    }

    private Appointment toDomain(AppointmentJpaEntity e) {
        return new Appointment(e.getId(), e.getPatientUserId(), e.getProfessionalId(), e.getLocationId(),
                e.getSpecialtyId(), statusFor(e.getStatusId()), e.getReason(), e.getRejectionReason(),
                e.getStartAt(), e.getEndAt(), e.getApprovedByUserId(), e.getApprovedAt());
    }

    private Long statusId(AppointmentStatus status) {
        return maps().idByStatus.get(status);
    }

    private AppointmentStatus statusFor(Long id) {
        return maps().statusById.get(id);
    }

    /** Los estados son un catálogo fijo: se cargan una vez y se reutilizan. */
    private StatusMaps maps() {
        if (idByStatus == null) {
            Map<AppointmentStatus, Long> ids = new EnumMap<>(AppointmentStatus.class);
            Map<Long, AppointmentStatus> byId = new HashMap<>();
            for (AppointmentStatusJpaEntity row : statuses.findAll()) {
                AppointmentStatus status = AppointmentStatus.valueOf(row.getCode());
                ids.put(status, row.getId());
                byId.put(row.getId(), status);
            }
            statusById = byId;
            idByStatus = ids;
        }
        return new StatusMaps(idByStatus, statusById);
    }

    private record StatusMaps(Map<AppointmentStatus, Long> idByStatus, Map<Long, AppointmentStatus> statusById) {
    }
}
