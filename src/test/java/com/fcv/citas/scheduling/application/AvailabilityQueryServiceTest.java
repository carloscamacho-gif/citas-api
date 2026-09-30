package com.fcv.citas.scheduling.application;

import com.fcv.citas.catalog.domain.exception.SpecialtyNotFoundException;
import com.fcv.citas.catalog.domain.model.Location;
import com.fcv.citas.catalog.domain.model.Specialty;
import com.fcv.citas.catalog.domain.port.out.LocationRepositoryPort;
import com.fcv.citas.catalog.domain.port.out.SpecialtyRepositoryPort;
import com.fcv.citas.professional.domain.model.Professional;
import com.fcv.citas.professional.domain.port.out.ProfessionalRepositoryPort;
import com.fcv.citas.scheduling.domain.exception.InvalidSchedulingException;
import com.fcv.citas.scheduling.domain.model.AvailabilityBlock;
import com.fcv.citas.scheduling.domain.model.AvailabilitySlot;
import com.fcv.citas.scheduling.domain.model.AvailableProfessional;
import com.fcv.citas.scheduling.domain.port.out.SchedulingRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
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

/**
 * RN-05 y RF-09/RF-10: una especialidad de 30 min ocupa un slot; una de 60 min necesita dos slots
 * consecutivos y libres, dentro del mismo bloque. Solo se ofrecen horarios que completan la duración.
 */
class AvailabilityQueryServiceTest {

    private static final LocalDateTime T0800 = LocalDateTime.of(2026, 10, 6, 8, 0);

    private static AvailabilitySlot slot(long id, long blockId, LocalDateTime start) {
        return new AvailabilitySlot(id, blockId, start, start.plusMinutes(30), null);
    }

    private static List<String> starts(List<AvailableProfessional.Window> windows) {
        return windows.stream().map(w -> w.startAt().toLocalTime().toString()).toList();
    }

    // ---- reglas puras de ventanas (windows) ----------------------------------------------------------

    @Test
    void a30MinuteSpecialtyOffersEveryFreeSlot() {
        var slots = List.of(slot(1, 1, T0800), slot(2, 1, T0800.plusMinutes(30)), slot(3, 1, T0800.plusMinutes(60)));

        var windows = AvailabilityQueryService.windows(slots, 30);

        assertThat(starts(windows)).containsExactly("08:00", "08:30", "09:00");
        assertThat(windows).allSatisfy(w -> assertThat(java.time.Duration.between(w.startAt(), w.endAt()).toMinutes()).isEqualTo(30));
    }

    @Test
    void a60MinuteSpecialtyOnlyOffersPairsOfConsecutiveSlots() {
        var slots = List.of(slot(1, 1, T0800), slot(2, 1, T0800.plusMinutes(30)), slot(3, 1, T0800.plusMinutes(60)));

        var windows = AvailabilityQueryService.windows(slots, 60);

        // 08:00-09:00 y 08:30-09:30; el último slot solo no completa 60 minutos.
        assertThat(starts(windows)).containsExactly("08:00", "08:30");
        assertThat(windows).allSatisfy(w -> assertThat(java.time.Duration.between(w.startAt(), w.endAt()).toMinutes()).isEqualTo(60));
    }

    @Test
    void anOccupiedSlotBreaksAConsecutivePair() {
        // El slot de 08:30 está ocupado, por eso no aparece entre los libres.
        var slots = List.of(slot(1, 1, T0800), slot(3, 1, T0800.plusMinutes(60)));

        assertThat(AvailabilityQueryService.windows(slots, 60)).isEmpty();
        // Con 30 minutos sí hay dos opciones.
        assertThat(starts(AvailabilityQueryService.windows(slots, 30))).containsExactly("08:00", "09:00");
    }

    @Test
    void aSixtyMinuteWindowNeverSpansTwoBlocks() {
        // Dos bloques contiguos (08:00-09:00 y 09:00-10:00): 08:30 + 09:00 no se une entre bloques.
        var slots = List.of(
                slot(1, 1, T0800), slot(2, 1, T0800.plusMinutes(30)),
                slot(3, 2, T0800.plusMinutes(60)), slot(4, 2, T0800.plusMinutes(90)));

        var windows = AvailabilityQueryService.windows(slots, 60);

        assertThat(starts(windows)).containsExactly("08:00", "09:00");
    }

    @Test
    void windowsAreSortedByStartTimeAcrossBlocks() {
        var slots = List.of(slot(3, 2, T0800.plusMinutes(120)), slot(1, 1, T0800), slot(2, 1, T0800.plusMinutes(30)));

        assertThat(starts(AvailabilityQueryService.windows(slots, 30))).containsExactly("08:00", "08:30", "10:00");
    }

    @Test
    void noFreeSlotsMeansNoWindows() {
        assertThat(AvailabilityQueryService.windows(List.of(), 30)).isEmpty();
        assertThat(AvailabilityQueryService.windows(List.of(), 60)).isEmpty();
    }

    // ---- búsqueda (search) --------------------------------------------------------------------------

    private ProfessionalRepositoryPort professionals;
    private SpecialtyRepositoryPort specialties;
    private LocationRepositoryPort locations;
    private SchedulingRepositoryPort scheduling;
    private AvailabilityQueryService service;

    private final Professional laura = new Professional(7L, 70L, "Laura", "Demo", "l@x.test", "P1", "L1", true,
            List.of(1L, 2L), 1L, List.of(1L));
    private final Professional otraSede = new Professional(8L, 80L, "Otro", "Sede", "o@x.test", "P2", "L2", true,
            List.of(1L), 1L, List.of(2L));

    @BeforeEach
    void setUp() {
        professionals = mock(ProfessionalRepositoryPort.class);
        specialties = mock(SpecialtyRepositoryPort.class);
        locations = mock(LocationRepositoryPort.class);
        scheduling = mock(SchedulingRepositoryPort.class);
        service = new AvailabilityQueryService(professionals, specialties, locations, scheduling);
        when(specialties.findById(1L)).thenReturn(Optional.of(new Specialty(1L, "MG", "Medicina General", 30, true, true)));
        when(specialties.findById(2L)).thenReturn(Optional.of(new Specialty(2L, "CAR", "Cardiología", 60, false, true)));
        when(locations.findById(1L)).thenReturn(Optional.of(new Location(1L, "HIC", "Hospital", true)));
    }

    private void givenLauraHasFreeSlots() {
        var block = new AvailabilityBlock(5L, 7L, 1L, "Hospital", T0800, T0800.plusHours(1), true);
        when(professionals.findAll()).thenReturn(List.of(laura, otraSede));
        when(scheduling.findBlocks(anyLong(), any(LocalDate.class), anyLong())).thenReturn(List.of(block));
        when(scheduling.findFreeSlots(List.of(5L))).thenReturn(List.of(slot(1, 5, T0800), slot(2, 5, T0800.plusMinutes(30))));
    }

    @Test
    void onlyProfessionalsAssignedToTheSpecialtyAndLocationAreOffered() {
        givenLauraHasFreeSlots();

        var result = service.search(1L, 1L, null, LocalDate.of(2026, 10, 6));

        assertThat(result).extracting(AvailableProfessional::name).containsExactly("Laura Demo");
        verify(scheduling, never()).findBlocks(org.mockito.ArgumentMatchers.eq(8L), any(), anyLong());
    }

    @Test
    void professionalFilterNarrowsTheSearch() {
        givenLauraHasFreeSlots();

        assertThat(service.search(1L, 1L, 8L, LocalDate.of(2026, 10, 6))).isEmpty();
    }

    @Test
    void professionalsWithoutACompleteWindowAreOmitted() {
        var block = new AvailabilityBlock(5L, 7L, 1L, "Hospital", T0800, T0800.plusHours(1), true);
        when(professionals.findAll()).thenReturn(List.of(laura));
        when(scheduling.findBlocks(anyLong(), any(LocalDate.class), anyLong())).thenReturn(List.of(block));
        // Un solo slot libre no completa los 60 minutos de Cardiología.
        when(scheduling.findFreeSlots(List.of(5L))).thenReturn(List.of(slot(1, 5, T0800)));

        assertThat(service.search(1L, 2L, null, LocalDate.of(2026, 10, 6))).isEmpty();
    }

    @Test
    void inactiveSpecialtyCannotBeSearched() {
        when(specialties.findById(1L)).thenReturn(Optional.of(new Specialty(1L, "MG", "Medicina General", 30, true, false)));

        assertThatThrownBy(() -> service.search(1L, 1L, null, LocalDate.of(2026, 10, 6)))
                .isInstanceOf(InvalidSchedulingException.class);
    }

    @Test
    void unknownSpecialtyIsNotFound() {
        when(specialties.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.search(1L, 99L, null, LocalDate.of(2026, 10, 6)))
                .isInstanceOf(SpecialtyNotFoundException.class);
    }

    @Test
    void inactiveLocationCannotBeSearched() {
        when(locations.findById(1L)).thenReturn(Optional.of(new Location(1L, "HIC", "Hospital", false)));

        assertThatThrownBy(() -> service.search(1L, 1L, null, LocalDate.of(2026, 10, 6)))
                .isInstanceOf(InvalidSchedulingException.class);
    }
}
