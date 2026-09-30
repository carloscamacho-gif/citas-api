-- Catálogo fijo de sedes (RF-05). Precargadas; la app solo las consulta.
CREATE TABLE locations (
    id     BIGINT AUTO_INCREMENT PRIMARY KEY,
    code   VARCHAR(20)  NOT NULL,
    name   VARCHAR(150) NOT NULL,
    active BOOLEAN      NOT NULL DEFAULT TRUE,
    CONSTRAINT uq_locations_code UNIQUE (code)
) ENGINE = InnoDB;

-- Sedes fijas del laboratorio (información pública de FCV, PRD sección 3).
INSERT INTO locations (code, name) VALUES
    ('HIC', 'Hospital Internacional de Colombia'),
    ('ICV', 'Instituto Cardiovascular / Fundación Cardiovascular de Colombia');

-- Catálogo configurable de especialidades (RF-06, RF-09). ADMIN las gestiona por CRUD.
-- duration_minutes solo admite 30 o 60. general = TRUE marca la especialidad de cita general
-- auto-aprobada (Medicina General); FALSE = especializada (requiere aprobación ADMIN).
CREATE TABLE specialties (
    id               BIGINT AUTO_INCREMENT PRIMARY KEY,
    code             VARCHAR(20)  NOT NULL,
    name             VARCHAR(150) NOT NULL,
    duration_minutes INT          NOT NULL,
    is_general       BOOLEAN      NOT NULL DEFAULT FALSE,
    active           BOOLEAN      NOT NULL DEFAULT TRUE,
    CONSTRAINT uq_specialties_code UNIQUE (code),
    CONSTRAINT ck_specialties_duration CHECK (duration_minutes IN (30, 60))
) ENGINE = InnoDB;

-- Datos sintéticos de laboratorio: una general (auto-aprobada) y una especializada.
INSERT INTO specialties (code, name, duration_minutes, is_general) VALUES
    ('MG',  'Medicina General', 30, TRUE),
    ('CAR', 'Cardiología',      60, FALSE);
