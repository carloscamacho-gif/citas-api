-- Usuarios del sistema (USER, PROFESSIONAL, ADMIN comparten esta tabla; el rol se resuelve via user_roles).
-- RF-01: email y documento unicos; password nunca en texto plano (se guarda password_hash).
CREATE TABLE users (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    first_name      VARCHAR(100)  NOT NULL,
    last_name       VARCHAR(100)  NOT NULL,
    document_type   VARCHAR(20)   NOT NULL,
    document_number VARCHAR(30)   NOT NULL,
    email           VARCHAR(150)  NOT NULL,
    phone           VARCHAR(30)   NOT NULL,
    password_hash   VARCHAR(255)  NOT NULL,
    active          BOOLEAN       NOT NULL DEFAULT TRUE,
    created_at      TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT uq_users_email UNIQUE (email),
    CONSTRAINT uq_users_document UNIQUE (document_type, document_number)
) ENGINE = InnoDB;
