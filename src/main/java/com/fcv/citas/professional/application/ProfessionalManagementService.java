package com.fcv.citas.professional.application;

import com.fcv.citas.auth.domain.exception.DocumentAlreadyUsedException;
import com.fcv.citas.auth.domain.exception.EmailAlreadyUsedException;
import com.fcv.citas.auth.domain.model.User;
import com.fcv.citas.auth.domain.port.out.PasswordHasherPort;
import com.fcv.citas.auth.domain.port.out.UserRepositoryPort;
import com.fcv.citas.catalog.domain.model.Location;
import com.fcv.citas.catalog.domain.model.Specialty;
import com.fcv.citas.catalog.domain.port.out.LocationRepositoryPort;
import com.fcv.citas.catalog.domain.port.out.SpecialtyRepositoryPort;
import com.fcv.citas.professional.domain.exception.InvalidProfessionalException;
import com.fcv.citas.professional.domain.exception.ProfessionalNotFoundException;
import com.fcv.citas.professional.domain.model.Professional;
import com.fcv.citas.professional.domain.port.in.CreateProfessionalCommand;
import com.fcv.citas.professional.domain.port.in.ProfessionalManagementUseCase;
import com.fcv.citas.professional.domain.port.out.ProfessionalRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProfessionalManagementService implements ProfessionalManagementUseCase {
    private final ProfessionalRepositoryPort professionals;
    private final UserRepositoryPort users;
    private final PasswordHasherPort passwordHasher;
    private final SpecialtyRepositoryPort specialties;
    private final LocationRepositoryPort locations;

    public ProfessionalManagementService(ProfessionalRepositoryPort professionals, UserRepositoryPort users,
            PasswordHasherPort passwordHasher, SpecialtyRepositoryPort specialties, LocationRepositoryPort locations) {
        this.professionals = professionals;
        this.users = users;
        this.passwordHasher = passwordHasher;
        this.specialties = specialties;
        this.locations = locations;
    }

    @Override
    @Transactional
    public Professional create(CreateProfessionalCommand command) {
        validateAssignments(command.specialtyIds(), command.primarySpecialtyId(), command.locationIds());
        if (users.existsByEmail(command.email())) throw new EmailAlreadyUsedException(command.email());
        if (users.existsByDocument(command.documentType(), command.documentNumber())) {
            throw new DocumentAlreadyUsedException(command.documentType().name(), command.documentNumber());
        }
        if (professionals.existsByProfessionalCode(command.professionalCode())) {
            throw new InvalidProfessionalException("El código profesional ya está registrado");
        }
        if (professionals.existsByLicenseNumber(command.licenseNumber())) {
            throw new InvalidProfessionalException("La matrícula profesional ya está registrada");
        }
        User savedUser = users.save(User.professional(command.firstName(), command.lastName(), command.documentType(),
                command.documentNumber(), command.email(), command.phone(), passwordHasher.hash(command.rawPassword())));
        return professionals.save(new Professional(null, savedUser.getId(), command.firstName(), command.lastName(),
                command.email(), command.professionalCode(), command.licenseNumber(), true,
                List.copyOf(command.specialtyIds()), command.primarySpecialtyId(), List.copyOf(command.locationIds())));
    }

    @Override public List<Professional> list() { return professionals.findAll(); }

    @Override
    @Transactional
    public Professional setActive(Long id, boolean active) {
        return professionals.save(require(id).withActive(active));
    }

    @Override
    @Transactional
    public Professional assignSpecialties(Long id, List<Long> specialtyIds, Long primarySpecialtyId) {
        validateSpecialties(specialtyIds, primarySpecialtyId);
        return professionals.save(require(id).withSpecialties(specialtyIds, primarySpecialtyId));
    }

    @Override
    @Transactional
    public Professional assignLocations(Long id, List<Long> locationIds) {
        validateLocations(locationIds);
        return professionals.save(require(id).withLocations(locationIds));
    }

    private Professional require(Long id) {
        return professionals.findById(id).orElseThrow(() -> new ProfessionalNotFoundException(id));
    }

    private void validateAssignments(List<Long> specialtyIds, Long primarySpecialtyId, List<Long> locationIds) {
        validateSpecialties(specialtyIds, primarySpecialtyId);
        validateLocations(locationIds);
    }

    private void validateSpecialties(List<Long> specialtyIds, Long primarySpecialtyId) {
        if (specialtyIds == null || specialtyIds.isEmpty() || primarySpecialtyId == null
                || specialtyIds.stream().filter(primarySpecialtyId::equals).count() != 1) {
            throw new InvalidProfessionalException("Debe asignar especialidades y exactamente una primaria");
        }
        for (Long id : specialtyIds.stream().distinct().toList()) {
            Specialty specialty = specialties.findById(id)
                    .orElseThrow(() -> new InvalidProfessionalException("La especialidad " + id + " no existe"));
            if (!specialty.isActive()) throw new InvalidProfessionalException("La especialidad " + id + " está inactiva");
        }
        if (specialtyIds.stream().distinct().count() != specialtyIds.size()) {
            throw new InvalidProfessionalException("No se permiten especialidades duplicadas");
        }
    }

    private void validateLocations(List<Long> locationIds) {
        if (locationIds == null || locationIds.isEmpty()) {
            throw new InvalidProfessionalException("Debe asignar al menos una sede");
        }
        for (Long id : locationIds.stream().distinct().toList()) {
            Location location = locations.findById(id)
                    .orElseThrow(() -> new InvalidProfessionalException("La sede " + id + " no existe"));
            if (!location.active()) throw new InvalidProfessionalException("La sede " + id + " está inactiva");
        }
        if (locationIds.stream().distinct().count() != locationIds.size()) {
            throw new InvalidProfessionalException("No se permiten sedes duplicadas");
        }
    }
}
