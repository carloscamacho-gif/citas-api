---
id: HU-003
tipo: historia-de-usuario
titulo: "Recuperar contraseña"
estado: Completada
epica: "[[EP-001-autenticacion-y-sesion]]"
esfuerzo: "Medio"
sprint_sugerido: "Sprint 4"
dependencias: ["[[HU-001-registrar-usuario]]"]
relacionadas: ["[[HU-002-iniciar-sesion-y-sesion-jwt]]"]
---

# HU-003 — Recuperar contraseña

## Historia de usuario

**COMO** usuario registrado que olvidó su contraseña
**QUIERO** solicitar su recuperación y definir una nueva
**PARA** volver a acceder a mi cuenta sin depender de un administrador

> Como usuario registrado, quiero recuperar mi contraseña para volver a acceder a mi cuenta.

## Contexto y descripción

El envío real de correo es opcional en este laboratorio: en desarrollo el token puede exponerse de forma segura en log o respuesta controlada para poder completar el ejercicio sin SMTP obligatorio.

## Alcance

- Solicitud de recuperación por email.
- Generación de un token temporal de un solo uso.
- Cambio de contraseña usando el token, que invalida/consume el token al usarse.
- Exposición controlada del token en entorno de desarrollo (log/respuesta), nunca en producción-simulada sin control.

## Fuera de alcance

- Envío real de correo vía SMTP (fuera de alcance del PRD, sección 9).
- Notificaciones por SMS/WhatsApp (fuera de alcance del PRD, sección 9).

## Reglas de negocio

- RF-03: token temporal y de un solo uso; envío real de correo opcional; cambiar contraseña invalida/consume el token.
- Sección 8 del PRD: nunca loguear la contraseña en texto plano (el token de recuperación sí puede exponerse de forma controlada, pero no la contraseña nueva).

## Dependencias y relaciones

- Épica: [[EP-001-autenticacion-y-sesion]]
- Dependencias: [[HU-001-registrar-usuario]] (requiere una cuenta existente asociada al email).
- Relacionadas: [[HU-002-iniciar-sesion-y-sesion-jwt]].

## Esfuerzo

**Nivel:** Medio

**Justificación de dificultad:** requiere generación/validación de un token de un solo uso con expiración y su ciclo de consumo, aunque no depende de otras épicas del dominio de citas.

## Tareas de desarrollo

- [ ] **T-01 — Solicitud de recuperación**
  Dificultad: Bajo
  Descripción: Endpoint que recibe un email y, si existe una cuenta asociada, genera un token temporal de un solo uso (sin revelar si el email existe o no, por seguridad).

- [ ] **T-02 — Exposición controlada del token en desarrollo**
  Dificultad: Bajo
  Descripción: Mecanismo seguro (log estructurado o respuesta explícita en entorno dev) para obtener el token sin SMTP, documentado como decisión de laboratorio.

- [ ] **T-03 — Cambio de contraseña con token**
  Dificultad: Medio
  Descripción: Endpoint que valida el token (vigente, no usado) y establece la nueva contraseña con hash adaptativo, consumiendo el token.

## Criterios de aceptación

### CA-01 — Solicitud genera token de un solo uso

**Dado** un email de una cuenta existente
**Cuando** se solicita recuperación de contraseña
**Entonces** se genera un token temporal de un solo uso asociado a esa cuenta

### CA-02 — Cambio de contraseña exitoso consume el token

**Dado** un token de recuperación vigente y no usado
**Cuando** se envía una nueva contraseña válida junto al token
**Entonces** la contraseña se actualiza (hasheada) y el token queda consumido/invalidado

### CA-03 — Token usado o expirado es rechazado

**Dado** un token ya usado o expirado
**Cuando** se intenta cambiar la contraseña con ese token
**Entonces** el sistema rechaza la operación sin modificar la contraseña

## Definition of Done

- [ ] Todos los criterios de aceptación obligatorios están validados con evidencia.
- [ ] Existe una migración Flyway coherente para el almacenamiento de tokens de recuperación (`password_reset_tokens`).
- [ ] El contrato REST de solicitud/cambio de contraseña está documentado (request/response, códigos de error) — RF-20.
- [ ] No se registra la nueva contraseña en texto plano en ningún log.
- [ ] La trazabilidad de esta HU y su épica está actualizada en `docs/wiki/scrum/`.

## Evidencia de validación

| Elemento | Resultado | Evidencia | Observación |
|---|---|---|---|
| CA-01 | Cumple | `RequestPasswordResetServiceTest` (token de un solo uso, invalida anteriores, sin enumeración); e2e (POST request → devToken; email inexistente → mismo mensaje, devToken null) | — |
| CA-02 | Cumple | `ResetPasswordServiceTest.validTokenUpdatesThePasswordAndConsumesTheToken`; e2e (confirm → 204; login clave nueva → 200; clave vieja → 401) + e2e navegador | — |
| CA-03 | Cumple | `ResetPasswordServiceTest` (expirado/usado/inexistente → rechazo); e2e (reusar token consumido → 400; token basura → 400) | — |
| DoD (migración) | Cumple | `V11__create_password_reset_tokens.sql` | — |
| DoD (no loguear contraseña) | Cumple | Verificado en el log de ejecución: 0 apariciones de la contraseña; el token se persiste como hash SHA-256 (64 hex), nunca en crudo | — |
| DoD-01 | Cumple | Suite backend 143/143 + e2e API + e2e navegador ([[LOOP-HU-003-recuperar-contrasena]]) | — |

## Historial de validación

- 2026-09-18 — HU creada en estado `Borrador` durante la planificación inicial del backlog; sugerida para Sprint 4 (alcance de S4) según `GUIA_SESIONES_S2_S6.md`.
- 2026-09-30 — Implementada y verificada (backend + frontend) en S4; ver [[LOOP-HU-003-recuperar-contrasena]]. Estado → `Completada`.

## Notas y decisiones

- **Mecanismo de exposición del token elegido (laboratorio):** ambos canales, controlados por la propiedad `app.password-reset.expose-token` (default `true` en laboratorio, debe ser `false` en producción). Cuando está activa: (a) el token se registra en log estructurado y (b) viaja en el campo `devToken` de la respuesta de solicitud. La contraseña nueva nunca se registra.
- **Contrato REST:**
  - `POST /api/v1/auth/password-reset/request` — cuerpo `{ "email": "..." }`. Responde **200** siempre (sin enumeración): `{ "message": "...", "devToken": "<token|null>" }`.
  - `POST /api/v1/auth/password-reset/confirm` — cuerpo `{ "token": "...", "newPassword": "..." (≥8) }`. **204** al éxito; **400** si el token es inválido/usado/expirado o la contraseña es muy corta.
- El token se genera como valor aleatorio de 256 bits (URL-safe) y solo se persiste su hash SHA-256; expira en `app.password-reset.minutes` (default 30) y una nueva solicitud invalida las anteriores del mismo usuario.
