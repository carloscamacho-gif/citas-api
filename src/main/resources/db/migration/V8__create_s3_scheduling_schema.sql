CREATE TABLE professionals (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    professional_code VARCHAR(40) NOT NULL,
    license_number VARCHAR(80) NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT uq_professionals_user UNIQUE (user_id),
    CONSTRAINT uq_professionals_code UNIQUE (professional_code),
    CONSTRAINT uq_professionals_license UNIQUE (license_number),
    CONSTRAINT fk_professionals_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE RESTRICT
) ENGINE = InnoDB;

CREATE TABLE professional_specialties (
    professional_id BIGINT NOT NULL,
    specialty_id BIGINT NOT NULL,
    is_primary BOOLEAN NOT NULL DEFAULT FALSE,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    PRIMARY KEY (professional_id, specialty_id),
    CONSTRAINT fk_prof_specialty_professional FOREIGN KEY (professional_id) REFERENCES professionals(id) ON DELETE CASCADE,
    CONSTRAINT fk_prof_specialty_specialty FOREIGN KEY (specialty_id) REFERENCES specialties(id) ON DELETE RESTRICT
) ENGINE = InnoDB;

CREATE TABLE professional_locations (
    professional_id BIGINT NOT NULL,
    location_id BIGINT NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    PRIMARY KEY (professional_id, location_id),
    CONSTRAINT fk_prof_location_professional FOREIGN KEY (professional_id) REFERENCES professionals(id) ON DELETE CASCADE,
    CONSTRAINT fk_prof_location_location FOREIGN KEY (location_id) REFERENCES locations(id) ON DELETE RESTRICT
) ENGINE = InnoDB;

CREATE TABLE appointment_statuses (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    code VARCHAR(40) NOT NULL,
    name VARCHAR(80) NOT NULL,
    is_terminal BOOLEAN NOT NULL DEFAULT FALSE,
    CONSTRAINT uq_appointment_status_code UNIQUE (code)
) ENGINE = InnoDB;

INSERT INTO appointment_statuses (code, name, is_terminal) VALUES
    ('REQUESTED', 'Solicitada / pendiente de aprobación', FALSE),
    ('APPROVED', 'Aprobada', FALSE),
    ('REJECTED', 'Rechazada', TRUE),
    ('CANCELLED', 'Cancelada', TRUE),
    ('COMPLETED', 'Atendida / completada', TRUE),
    ('NO_SHOW', 'No asistió', TRUE);

CREATE TABLE availability_blocks (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    professional_id BIGINT NOT NULL,
    location_id BIGINT NOT NULL,
    start_at DATETIME NOT NULL,
    end_at DATETIME NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT ck_availability_block_time CHECK (end_at > start_at),
    CONSTRAINT fk_availability_professional FOREIGN KEY (professional_id) REFERENCES professionals(id) ON DELETE RESTRICT,
    CONSTRAINT fk_availability_location FOREIGN KEY (location_id) REFERENCES locations(id) ON DELETE RESTRICT,
    INDEX ix_availability_prof_date (professional_id, start_at),
    INDEX ix_availability_location_date (location_id, start_at)
) ENGINE = InnoDB;

CREATE TABLE appointments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    patient_user_id BIGINT NOT NULL,
    professional_id BIGINT NOT NULL,
    location_id BIGINT NOT NULL,
    specialty_id BIGINT NOT NULL,
    status_id BIGINT NOT NULL,
    reason VARCHAR(500) NULL,
    rejection_reason VARCHAR(500) NULL,
    scheduled_start_at DATETIME NOT NULL,
    scheduled_end_at DATETIME NOT NULL,
    approved_by_user_id BIGINT NULL,
    approved_at DATETIME NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT ck_appointment_time CHECK (scheduled_end_at > scheduled_start_at),
    CONSTRAINT fk_appointments_patient FOREIGN KEY (patient_user_id) REFERENCES users(id) ON DELETE RESTRICT,
    CONSTRAINT fk_appointments_professional FOREIGN KEY (professional_id) REFERENCES professionals(id) ON DELETE RESTRICT,
    CONSTRAINT fk_appointments_location FOREIGN KEY (location_id) REFERENCES locations(id) ON DELETE RESTRICT,
    CONSTRAINT fk_appointments_specialty FOREIGN KEY (specialty_id) REFERENCES specialties(id) ON DELETE RESTRICT,
    CONSTRAINT fk_appointments_status FOREIGN KEY (status_id) REFERENCES appointment_statuses(id) ON DELETE RESTRICT,
    CONSTRAINT fk_appointments_approved_by FOREIGN KEY (approved_by_user_id) REFERENCES users(id) ON DELETE RESTRICT,
    INDEX ix_appointments_patient (patient_user_id, scheduled_start_at),
    INDEX ix_appointments_professional (professional_id, scheduled_start_at),
    INDEX ix_appointments_status (status_id)
) ENGINE = InnoDB;

CREATE TABLE professional_slots (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    availability_block_id BIGINT NOT NULL,
    start_at DATETIME NOT NULL,
    end_at DATETIME NOT NULL,
    appointment_id BIGINT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT ck_professional_slot_time CHECK (end_at > start_at),
    CONSTRAINT uq_block_slot UNIQUE (availability_block_id, start_at),
    CONSTRAINT fk_slots_availability_block FOREIGN KEY (availability_block_id) REFERENCES availability_blocks(id) ON DELETE RESTRICT,
    CONSTRAINT fk_slots_appointment FOREIGN KEY (appointment_id) REFERENCES appointments(id) ON DELETE SET NULL,
    INDEX ix_slots_start (start_at),
    INDEX ix_slots_appointment (appointment_id)
) ENGINE = InnoDB;

CREATE TABLE appointment_status_history (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    appointment_id BIGINT NOT NULL,
    status_id BIGINT NOT NULL,
    changed_by_user_id BIGINT NULL,
    change_source VARCHAR(20) NOT NULL,
    reason VARCHAR(500) NULL,
    changed_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT ck_status_history_source CHECK (change_source IN ('SYSTEM', 'USER', 'ADMIN')),
    CONSTRAINT fk_status_history_appointment FOREIGN KEY (appointment_id) REFERENCES appointments(id) ON DELETE CASCADE,
    CONSTRAINT fk_status_history_status FOREIGN KEY (status_id) REFERENCES appointment_statuses(id) ON DELETE RESTRICT,
    CONSTRAINT fk_status_history_user FOREIGN KEY (changed_by_user_id) REFERENCES users(id) ON DELETE SET NULL,
    INDEX ix_status_history_appointment (appointment_id, changed_at)
) ENGINE = InnoDB;
