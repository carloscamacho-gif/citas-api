---
id: HU-003
tipo: historia-de-usuario
titulo: "Recuperar contraseña"
estado: Borrador
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
| CA-01 | Pendiente | — | — |
| CA-02 | Pendiente | — | — |
| CA-03 | Pendiente | — | — |
| DoD-01 | Pendiente | — | — |

## Historial de validación

- 2026-09-18 — HU creada en estado `Borrador` durante la planificación inicial del backlog; sugerida para Sprint 4 (alcance de S4) según `GUIA_SESIONES_S2_S6.md`.

## Notas y decisiones

- Decisión pendiente del estudiante: mecanismo concreto de exposición del token en desarrollo (log vs. campo en la respuesta HTTP). Ambas opciones son válidas según el PRD; se debe documentar la elegida en el contrato REST.
