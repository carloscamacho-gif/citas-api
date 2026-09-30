---
id: EP-001
tipo: epica
titulo: "Autenticación y sesión"
estado: Aprobada
historias: ["[[HU-001-registrar-usuario]]", "[[HU-002-iniciar-sesion-y-sesion-jwt]]", "[[HU-003-recuperar-contrasena]]"]
dependencias: []
---

# EP-001 — Autenticación y sesión

## Objetivo

Permitir que un visitante se registre como USER, inicie sesión de forma segura y pueda recuperar el acceso a su cuenta si olvida su contraseña.

## Valor esperado

Es la base de identidad de toda la aplicación: sin registro/login no existe ningún flujo de citas, perfil ni administración posible.

## Actores

- USER
- (indirectamente) PROFESSIONAL y ADMIN, que también inician sesión con el mismo mecanismo aunque no se autorregistran.

## Alcance

- Registro de cuenta USER con datos mínimos únicos (email, documento).
- Login por email/contraseña con emisión de JWT access + refresh.
- Refresh y revocación/logout de sesión.
- Recuperación de contraseña con token temporal de un solo uso.

## Fuera de alcance

- Autorregistro de PROFESSIONAL o ADMIN (los crea ADMIN, ver [[EP-004-gestion-de-profesionales]]).
- Autorización fina por endpoint (se cubre transversalmente en cada HU que expone recursos protegidos).
- Envío real de correo (RF-03 permite exponer el token de forma segura en log/respuesta controlada en desarrollo).

## Reglas de negocio

- RF-01: email y documento deben ser únicos; contraseñas nunca en texto plano.
- RF-02: access token de corta duración + refresh token; roles forman parte del contexto de autorización.
- RF-03: token de recuperación temporal y de un solo uso; cambiar contraseña invalida/consume el token.
- Sección 8 del PRD: hash adaptativo (BCrypt/Argon2), secretos solo por `.env`, JWT access/refresh con secretos separados.

## Dependencias

- Ninguna (es la épica fundacional).

## Historias de usuario

- [[HU-001-registrar-usuario]]
- [[HU-002-iniciar-sesion-y-sesion-jwt]]
- [[HU-003-recuperar-contrasena]]

## Criterio de completitud de la épica

- [ ] Todas las HU obligatorias de esta épica están `Completada`.
- [ ] No quedan dependencias bloqueantes dentro del alcance de la épica.

## Riesgos e incógnitas

- No hay decisión de SMTP para RF-03; se documenta como supuesto que en desarrollo el token se expone de forma controlada (log/response), nunca como requisito bloqueante.
