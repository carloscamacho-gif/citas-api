package com.fcv.citas.scheduling.application;

import com.fcv.citas.auth.domain.port.out.UserRepositoryPort;
import com.fcv.citas.catalog.domain.port.out.LocationRepositoryPort;
import com.fcv.citas.catalog.domain.port.out.SpecialtyRepositoryPort;
import com.fcv.citas.professional.domain.port.out.ProfessionalRepositoryPort;
import com.fcv.citas.scheduling.domain.model.Appointment;
import com.fcv.citas.scheduling.domain.model.RescheduleDetails;
import com.fcv.citas.scheduling.domain.model.RescheduleRequest;
import org.springframework.stereotype.Component;

/** Resuelve los nombres (paciente, profesional, especialidad, sede) que la interfaz muestra junto a una reprogramación. */
@Component
public class RescheduleDetailsAssembler {

    private final ProfessionalRepositoryPort professionals;
    private final SpecialtyRepositoryPort specialties;
    private final LocationRepositoryPort locations;
    private final UserRepositoryPort users;

    public RescheduleDetailsAssembler(ProfessionalRepositoryPort professionals, SpecialtyRepositoryPort specialties,
                                      LocationRepositoryPort locations, UserRepositoryPort users) {
        this.professionals = professionals;
        this.specialties = specialties;
        this.locations = locations;
        this.users = users;
    }

    /** El profesional y la especialidad se toman de la cita (que la reprogramación conserva). */
    public RescheduleDetails assemble(RescheduleRequest request, Appointment appointment) {
        String professionalName = professionals.findById(appointment.professionalId())
                .map(p -> p.fullName()).orElse("");
        String specialtyName = specialties.findById(appointment.specialtyId())
                .map(s -> s.getName()).orElse("");
        String locationName = locations.findById(request.requestedLocationId())
                .map(l -> l.name()).orElse("");
        String patientName = users.findById(request.requestedByUserId())
                .map(u -> (u.getFirstName() + " " + u.getLastName()).trim()).orElse("");
        return new RescheduleDetails(request, patientName, professionalName, specialtyName, locationName);
    }
}
