-- HU-021: el cierre de atención lo realiza el PROFESSIONAL; se amplía el origen de cambio permitido
-- en el historial para registrar ese actor de forma explícita (RN-11, RF-19).
ALTER TABLE appointment_status_history
    DROP CHECK ck_status_history_source;

ALTER TABLE appointment_status_history
    ADD CONSTRAINT ck_status_history_source
        CHECK (change_source IN ('SYSTEM', 'USER', 'ADMIN', 'PROFESSIONAL'));
