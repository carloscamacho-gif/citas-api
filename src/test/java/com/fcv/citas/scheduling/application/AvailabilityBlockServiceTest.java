package com.fcv.citas.scheduling.application;

import com.fcv.citas.catalog.domain.model.Location;
import com.fcv.citas.catalog.domain.port.out.LocationRepositoryPort;
import com.fcv.citas.professional.domain.exception.ProfessionalNotFoundException;
import com.fcv.citas.professional.domain.model.Professional;
import com.fcv.citas.professional.domain.port.out.ProfessionalRepositoryPort;
import com.fcv.citas.scheduling.domain.exception.AvailabilityBlockNotFoundException;
import com.fcv.citas.scheduling.domain.exception.InvalidSchedulingException;
import com.fcv.citas.scheduling.domain.exception.SchedulingConflictException;
import com.fcv.citas.scheduling.domain.model.AvailabilityBlock;
import com.fcv.citas.scheduling.domain.port.in.AvailabilityBlockCommand;
import com.fcv.citas.scheduling.domain.port.out.SchedulingRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/** RF-08/RF-09, RN-06, RN-07 — bloques de disponibilidad del profesional. */
class AvailabilityBlockServiceTest {

    private static final Long USER_ID = 70L;
    private static final LocalDateTime START = LocalDateTime.of(2026, 10, 6, 8, 0);
    private static final LocalDateTime END = LocalDateTime.of(2026, 10, 6, 12, 0);

    private final Professional laura = new Professional(7L, USER_ID, "Laura", "Demo", "l@x.test", "P1", "L1", true,
            List.of(1L), 1L, List.of(1L));

    private ProfessionalRepositoryPort professionals;
    private LocationRepositoryPort locations;
    private SchedulingRepositoryPort scheduling;
    private AvailabilityBlockService service;

    @BeforeEach
    void setUp() {
        professionals = mock(ProfessionalRepositoryPort.class);
        locations = mock(LocationRepositoryPort.class);
        scheduling = mock(SchedulingRepositoryPort.class);
        // "Ahora" fijo: 2026-09-25 10:00 (Bogotá).
        Clock clock = Clock.fixed(Instant.parse("2026-09-25T15:00:00Z"), ZoneId.of("America/Bogota"));
        service = new AvailabilityBlockService(professionals, locations, scheduling, clock);

        when(professionals.findByUserId(USER_ID)).thenReturn(Optional.of(laura));
        when(locations.findById(1L)).thenReturn(Optional.of(new Location(1L, "HIC", "Hospital", true)));
        when(scheduling.overlaps(anyLong(), any(), any(), any())).thenReturn(false);
        when(scheduling.saveBlock(any(AvailabilityBlock.class))).thenAnswer(inv -> {
            AvailabilityBlock b = inv.getArgument(0);
            return new AvailabilityBlock(b.id() == null ? 100L : b.id(), b.professionalId(), b.locationId(), "Hospital",
                    b.startAt(), b.endAt(), b.active());
        });
    }

    private static AvailabilityBlockCommand cmd(LocalDateTime start, LocalDateTime end) {
        return new AvailabilityBlockCommand(1L, start, end);
    }

    private static AvailabilityBlock existing(long id, long professionalId, LocalDateTime start) {
        return new AvailabilityBlock(id, professionalId, 1L, "Hospital", start, start.plusHours(2), true);
    }

    @Test
    void createsABlockAndItsThirtyMinuteSlots() {
        AvailabilityBlock created = service.create(USER_ID, cmd(START, END));

        assertThat(created.id()).isEqualTo(100L);
        verify(scheduling).replaceSlots(100L, START, END);
    }

    @Test
    void rejectsBlocksInThePast() {
        assertThatThrownBy(() -> service.create(USER_ID, cmd(LocalDateTime.of(2026, 9, 25, 8, 0), LocalDateTime.of(2026, 9, 25, 9, 0))))
                .isInstanceOf(InvalidSchedulingException.class).hasMessageContaining("pasado");
        verify(scheduling, never()).saveBlock(any());
    }

    @Test
    void rejectsRangesThatAreNotWholeThirtyMinuteSlots() {
        assertThatThrownBy(() -> service.create(USER_ID, cmd(START, START.plusMinutes(45))))
                .isInstanceOf(InvalidSchedulingException.class).hasMessageContaining("30 minutos");
    }

    @Test
    void rejectsAnEndBeforeTheStart() {
        assertThatThrownBy(() -> service.create(USER_ID, cmd(END, START))).isInstanceOf(InvalidSchedulingException.class);
    }

    @Test
    void rejectsOverlapWithAnotherBlockOfTheSameProfessional() {
        when(scheduling.overlaps(7L, START, END, null)).thenReturn(true);

        assertThatThrownBy(() -> service.create(USER_ID, cmd(START, END))).isInstanceOf(SchedulingConflictException.class);
        verify(scheduling, never()).saveBlock(any());
    }

    @Test
    void rejectsALocationTheProfessionalIsNotAssignedTo() {
        var other = new AvailabilityBlockCommand(2L, START, END);

        assertThatThrownBy(() -> service.create(USER_ID, other))
                .isInstanceOf(InvalidSchedulingException.class).hasMessageContaining("habilitado");
    }

    @Test
    void rejectsAnInactiveProfessional() {
        when(professionals.findByUserId(USER_ID)).thenReturn(Optional.of(laura.withActive(false)));

        assertThatThrownBy(() -> service.create(USER_ID, cmd(START, END))).isInstanceOf(InvalidSchedulingException.class);
    }

    @Test
    void aUserThatIsNotAProfessionalCannotManageBlocks() {
        when(professionals.findByUserId(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.create(99L, cmd(START, END))).isInstanceOf(ProfessionalNotFoundException.class);
    }

    @Test
    void cannotEditABlockWithCommittedAppointments() {
        when(scheduling.findBlock(100L)).thenReturn(Optional.of(existing(100L, 7L, START)));
        when(scheduling.hasCommittedSlots(100L)).thenReturn(true);

        assertThatThrownBy(() -> service.update(USER_ID, 100L, cmd(START, END))).isInstanceOf(SchedulingConflictException.class);
        verify(scheduling, never()).saveBlock(any());
    }

    @Test
    void cannotTouchAnotherProfessionalsBlock() {
        when(scheduling.findBlock(100L)).thenReturn(Optional.of(existing(100L, 8L, START)));

        assertThatThrownBy(() -> service.update(USER_ID, 100L, cmd(START, END))).isInstanceOf(InvalidSchedulingException.class);
        assertThatThrownBy(() -> service.delete(USER_ID, 100L)).isInstanceOf(InvalidSchedulingException.class);
    }

    @Test
    void unknownBlockIsNotFound() {
        when(scheduling.findBlock(404L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.delete(USER_ID, 404L)).isInstanceOf(AvailabilityBlockNotFoundException.class);
    }

    @Test
    void deletesAFutureBlockWithoutAppointments() {
        when(scheduling.findBlock(100L)).thenReturn(Optional.of(existing(100L, 7L, START)));
        when(scheduling.hasCommittedSlots(100L)).thenReturn(false);

        service.delete(USER_ID, 100L);

        verify(scheduling).deleteBlock(100L);
    }

    @Test
    void cannotDeleteABlockThatAlreadyStarted() {
        when(scheduling.findBlock(100L)).thenReturn(Optional.of(existing(100L, 7L, LocalDateTime.of(2026, 9, 25, 8, 0))));

        assertThatThrownBy(() -> service.delete(USER_ID, 100L)).isInstanceOf(InvalidSchedulingException.class);
        verify(scheduling, never()).deleteBlock(anyLong());
    }

    @Test
    void cannotDeleteABlockWithCommittedAppointments() {
        when(scheduling.findBlock(100L)).thenReturn(Optional.of(existing(100L, 7L, START)));
        when(scheduling.hasCommittedSlots(100L)).thenReturn(true);

        assertThatThrownBy(() -> service.delete(USER_ID, 100L)).isInstanceOf(SchedulingConflictException.class);
        verify(scheduling, never()).deleteBlock(anyLong());
    }
}
