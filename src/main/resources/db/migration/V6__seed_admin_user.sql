-- Usuario ADMIN de arranque del laboratorio (sintético). Necesario para gestionar catálogos,
-- profesionales y aprobaciones (RF-06 en adelante), ya que ADMIN no se autorregistra.
-- Credenciales de laboratorio: admin@fcv.local / Demo1234*  (password de referencia del trainer).
-- El hash es BCrypt (cost 10) de 'Demo1234*'; nunca se guarda la contraseña en claro.
INSERT INTO users (first_name, last_name, document_type, document_number, email, phone, password_hash, active)
VALUES ('Admin', 'FCV', 'CC', '900000001', 'admin@fcv.local', '3000000000',
        '$2b$10$bXWsWIXTq1sqzp9olx1WGeR5vO4vrUVYLyJBLZLJnbDwuFEAbcVKy', TRUE);

INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id
FROM users u
JOIN roles r ON r.name = 'ADMIN'
WHERE u.email = 'admin@fcv.local';
