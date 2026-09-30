CREATE TABLE reschedule_request_statuses (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    code VARCHAR(40) NOT NULL,
    name VARCHAR(80) NOT NULL,
    is_terminal BOOLEAN NOT NULL DEFAULT FALSE,
    CONSTRAINT uq_reschedule_status_code UNIQUE (code)
) ENGINE = InnoDB;

INSERT INTO reschedule_request_statuses (code, name, is_terminal) VALUES
    ('PENDING', 'Pendiente de decisión', FALSE),
    ('APPROVED', 'Aprobada', TRUE),
    ('REJECTED', 'Rechazada', TRUE);

CREATE TABLE reschedule_requests (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    appointment_id BIGINT NOT NULL,
    requested_by_user_id BIGINT NOT NULL,
    requested_location_id BIGINT NOT NULL,
    status_id BIGINT NOT NULL,
    previous_start_at DATETIME NOT NULL,
    previous_end_at DATETIME NOT NULL,
    requested_start_at DATETIME NOT NULL,
    requested_end_at DATETIME NOT NULL,
    decision_reason VARCHAR(500) NULL,
    decided_by_user_id BIGINT NULL,
    decided_at DATETIME NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT ck_reschedule_time CHECK (requested_end_at > requested_start_at),
    CONSTRAINT fk_reschedule_appointment FOREIGN KEY (appointment_id) REFERENCES appointments(id) ON DELETE CASCADE,
    CONSTRAINT fk_reschedule_requested_by FOREIGN KEY (requested_by_user_id) REFERENCES users(id) ON DELETE RESTRICT,
    CONSTRAINT fk_reschedule_location FOREIGN KEY (requested_location_id) REFERENCES locations(id) ON DELETE RESTRICT,
    CONSTRAINT fk_reschedule_status FOREIGN KEY (status_id) REFERENCES reschedule_request_statuses(id) ON DELETE RESTRICT,
    CONSTRAINT fk_reschedule_decided_by FOREIGN KEY (decided_by_user_id) REFERENCES users(id) ON DELETE RESTRICT,
    INDEX ix_reschedule_appointment (appointment_id),
    INDEX ix_reschedule_status (status_id)
) ENGINE = InnoDB;
