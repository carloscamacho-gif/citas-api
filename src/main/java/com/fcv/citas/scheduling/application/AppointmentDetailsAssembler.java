package com.fcv.citas.scheduling.application;

import com.fcv.citas.catalog.domain.port.out.LocationRepositoryPort;
import com.fcv.citas.catalog.domain.port.out.SpecialtyRepositoryPort;
import com.fcv.citas.professional.domain.port.out.ProfessionalRepositoryPort;
import com.fcv.citas.scheduling.domain.model.Appointment;
import com.fcv.citas.scheduling.domain.model.AppointmentDetails;
import org.springframework.stereotype.Component;

/** Resuelve los nombres (profesional, especialidad, sede) que la interfaz muestra junto a una cita. */
@Component
public class AppointmentDetailsAssembler {

    private final ProfessionalRepositoryPort professionals;
    private final SpecialtyRepositoryPort specialties;
    private final LocationRepositoryPort locations;

    public AppointmentDetailsAssembler(ProfessionalRepositoryPort professionals, SpecialtyRepositoryPort specialties,
                                       LocationRepositoryPort locations) {
        this.professionals = professionals;
        this.specialties = specialties;
        this.locations = locations;
    }

    public AppointmentDetails assemble(Appointment appointment) {
        String professionalName = professionals.findById(appointment.professionalId())
                .map(p -> p.fullName()).orElse("");
        String specialtyName = specialties.findById(appointment.specialtyId())
                .map(s -> s.getName()).orElse("");
        String locationName = locations.findById(appointment.locationId())
                .map(l -> l.name()).orElse("");
        return new AppointmentDetails(appointment, professionalName, specialtyName, locationName);
    }
}
