-- Catálogo fijo de roles (RF-05). Solo lectura desde la aplicación; se administra por migración.
CREATE TABLE roles (
    id   BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(30) NOT NULL,
    CONSTRAINT uq_roles_name UNIQUE (name)
) ENGINE = InnoDB;

INSERT INTO roles (name) VALUES ('USER'), ('PROFESSIONAL'), ('ADMIN');
