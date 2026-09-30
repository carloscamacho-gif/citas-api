---
id: HU-021
tipo: historia-de-usuario
titulo: "Cerrar atención (COMPLETED/NO_SHOW)"
estado: Completada
epica: "[[EP-008-atencion-del-profesional]]"
esfuerzo: "Bajo"
sprint_sugerido: "Sprint 4"
dependencias: ["[[HU-020-consultar-agenda-del-profesional]]"]
relacionadas: ["[[HU-024-auditoria-de-cambios-de-estado]]"]
---

# HU-021 — Cerrar atención (COMPLETED/NO_SHOW)

## Historia de usuario

**COMO** PROFESSIONAL
**QUIERO** marcar una cita pasada como completada o como inasistencia
**PARA** dejar registro del resultado real de la atención

> Como PROFESSIONAL, quiero cerrar la atención de una cita para reflejar si el paciente asistió.

## Contexto y descripción

Cierra el ciclo operativo de una cita ya atendida (o que debió atenderse). Debe registrarse historial del cambio.

## Alcance

- Marcar una cita pasada/aplicable, propia del profesional, como `COMPLETED` o `NO_SHOW`.
- Registro de historial del cambio de estado.

## Fuera de alcance

- Cierre de citas de otros profesionales (restricción de ownership, ver RF-16).
- Reversión de un cierre ya realizado (no contemplado por el PRD).

## Reglas de negocio

- RF-17: PROFESSIONAL puede marcar una cita pasada/aplicable como `COMPLETED` o `NO_SHOW`; debe registrarse historial.
- RN-11: transiciones de estado deben ser explícitas y verificables.

## Dependencias y relaciones

- Épica: [[EP-008-atencion-del-profesional]]
- Dependencias: [[HU-020-consultar-agenda-del-profesional]] (el profesional identifica la cita a cerrar desde su agenda).
- Relacionadas: [[HU-024-auditoria-de-cambios-de-estado]].

## Esfuerzo

**Nivel:** Bajo

**Justificación de dificultad:** transición de estado simple sobre una cita existente, con validación de ownership y de que la cita sea "pasada/aplicable".

## Tareas de desarrollo

- [ ] **T-01 — Endpoint de cierre de atención**
  Dificultad: Bajo
  Descripción: Cambia el estado a `COMPLETED` o `NO_SHOW`, validando ownership y que la cita sea pasada/aplicable; registra el cambio en el historial.

## Criterios de aceptación

### CA-01 — Marcar como completada

**Dado** una cita `APPROVED` propia del profesional, ya pasada
**Cuando** el profesional la marca como `COMPLETED`
**Entonces** la cita queda en ese estado y se registra el cambio en el historial

### CA-02 — Marcar como inasistencia

**Dado** una cita `APPROVED` propia del profesional, ya pasada
**Cuando** el profesional la marca como `NO_SHOW`
**Entonces** la cita queda en ese estado y se registra el cambio en el historial

### CA-03 — Rechazo sobre cita de otro profesional

**Dado** una cita `APPROVED` de otro profesional
**Cuando** un profesional intenta cerrarla
**Entonces** el sistema rechaza la operación

## Definition of Done

- [ ] Todos los criterios de aceptación obligatorios están validados con evidencia.
- [ ] El contrato REST de cierre de atención está documentado — RF-20.
- [ ] La trazabilidad de esta HU y su épica está actualizada en `docs/wiki/scrum/`.

## Evidencia de validación

| Elemento | Resultado | Evidencia | Observación |
|---|---|---|---|
| CA-01 | Cumple | `CloseAttentionServiceTest.marksAPastApprovedAppointmentAsCompletedAndAudits`; e2e (cita 5 → COMPLETED, historial `SYSTEM APPROVED → PROFESSIONAL COMPLETED`) + e2e navegador | — |
| CA-02 | Cumple | `CloseAttentionServiceTest.marksAPastApprovedAppointmentAsNoShow` | — |
| CA-03 | Cumple | `CloseAttentionServiceTest.cannotCloseAnotherProfessionalsAppointment` (403); guardas extra: futura → 400, no aprobada → 400 | — |
| DoD-01 | Cumple | Suite backend 121/121 + e2e API (COMPLETED, re-cierre terminal → 400, futura → 400, ADMIN → 403) + e2e navegador ([[LOOP-HU-021-cerrar-atencion]]) | — |

## Historial de validación

- 2026-09-18 — HU creada en estado `Borrador` durante la planificación inicial del backlog; sugerida para Sprint 4 (alcance de S4) según `GUIA_SESIONES_S2_S6.md`.
- 2026-09-30 — Implementada y verificada (backend + frontend) en S4; ver [[LOOP-HU-021-cerrar-atencion]]. Estado → `Completada`.

## Notas y decisiones

- Umbral de "cita pasada/aplicable" (incógnita heredada, ahora resuelta): la cita se puede cerrar cuando **su inicio ya ocurrió** (`startAt <= ahora`) y está en estado `APPROVED`. No se permite cerrar una cita futura ni una ya terminal.
- Endpoint: `POST /api/v1/professional/appointments/{id}/close` con cuerpo `{ "outcome": "COMPLETED" | "NO_SHOW" }` (rol PROFESSIONAL). El id autenticado se resuelve a `professionals.id` y se exige ownership (RF-16).
- El historial registra el cambio con fuente `PROFESSIONAL` (nuevo origen añadido en la migración `V10`, RN-11).
