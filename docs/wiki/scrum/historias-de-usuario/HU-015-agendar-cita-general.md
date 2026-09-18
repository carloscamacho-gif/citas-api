---
id: HU-015
tipo: historia-de-usuario
titulo: "Agendar cita general"
estado: Borrador
epica: "[[EP-006-busqueda-y-agendamiento-de-citas]]"
esfuerzo: "Medio"
sprint_sugerido: "Sprint 2"
dependencias: ["[[HU-014-consultar-disponibilidad]]"]
relacionadas: ["[[HU-016-solicitar-cita-especializada]]", "[[HU-024-auditoria-de-cambios-de-estado]]"]
---

# HU-015 — Agendar cita general

## Historia de usuario

**COMO** USER
**QUIERO** agendar una cita de Medicina General eligiendo un profesional disponible
**PARA** obtener una cita aprobada de inmediato, sin esperar aprobación administrativa

> Como USER, quiero agendar una cita general con aprobación automática para resolver mi necesidad sin trámites adicionales.

## Contexto y descripción

El usuario selecciona `Medicina General` y uno de los profesionales generales disponibles. Si el horario sigue disponible al confirmar, la cita se crea `APPROVED` automáticamente.

## Alcance

- Confirmación de una cita de Medicina General sobre un horario mostrado por [[HU-014-consultar-disponibilidad]].
- Verificación de disponibilidad real al momento de confirmar (no solo al momento de la búsqueda).
- Aprobación automática (`APPROVED`) y reserva definitiva del/los slot(s).

## Fuera de alcance

- Cita especializada (ver [[HU-016-solicitar-cita-especializada]]).
- Cancelación/reprogramación (ver [[EP-007-gestion-de-citas-del-usuario]]).

## Reglas de negocio

- RF-11: si el horario sigue disponible al confirmar, la cita se crea `APPROVED` automáticamente; no requiere ADMIN.
- RN-02: citas generales se aprueban automáticamente.
- RN-01: ninguna cita puede ocupar slots ya reservados/retenidos.
- RN-06: no se permiten citas en el pasado.

## Dependencias y relaciones

- Épica: [[EP-006-busqueda-y-agendamiento-de-citas]]
- Dependencias: [[HU-014-consultar-disponibilidad]] (el usuario parte de un horario mostrado).
- Relacionadas: [[HU-016-solicitar-cita-especializada]], [[HU-024-auditoria-de-cambios-de-estado]] (la creación debe auditarse con fuente `USER`).

## Esfuerzo

**Nivel:** Medio

**Justificación de dificultad:** requiere reverificar disponibilidad en el momento exacto de confirmar (condición de carrera entre búsqueda y confirmación) y coordinar la reserva atómica de slots.

## Tareas de desarrollo

- [ ] **T-01 — Endpoint de confirmación de cita general**
  Dificultad: Medio
  Descripción: Revalida disponibilidad del horario elegido al momento de confirmar y crea la cita en estado `APPROVED` si sigue disponible.

- [ ] **T-02 — Registro de auditoría de creación**
  Dificultad: Bajo
  Descripción: Registra en el historial de estados la creación con estado `APPROVED`, fuente `USER` (o `SYSTEM` si la aprobación se considera automática del sistema, a definir).

## Criterios de aceptación

### CA-01 — Confirmación exitosa con horario disponible

**Dado** un horario general disponible mostrado por la búsqueda
**Cuando** el USER lo confirma antes de que otro lo ocupe
**Entonces** la cita se crea en estado `APPROVED` sin intervención de ADMIN

### CA-02 — Rechazo por horario ya no disponible

**Dado** un horario que fue reservado por otro usuario entre la búsqueda y la confirmación
**Cuando** el USER intenta confirmarlo
**Entonces** el sistema rechaza la confirmación sin crear una cita duplicada sobre el mismo slot

### CA-03 — Auditoría de la creación

**Dado** una cita general creada exitosamente
**Cuando** se consulta su historial de estados
**Entonces** existe un registro de la transición a `APPROVED` con fecha/hora y fuente

## Definition of Done

- [ ] Todos los criterios de aceptación obligatorios están validados con evidencia.
- [ ] El contrato REST de agendamiento de cita general está documentado — RF-20.
- [ ] Existe al menos una prueba de doble reserva sobre este flujo (requisito de verificación S3).
- [ ] La trazabilidad de esta HU y su épica está actualizada en `docs/wiki/scrum/`.

## Evidencia de validación

| Elemento | Resultado | Evidencia | Observación |
|---|---|---|---|
| CA-01 | Pendiente | — | — |
| CA-02 | Pendiente | — | — |
| CA-03 | Pendiente | — | — |
| DoD-01 | Pendiente | — | — |

## Historial de validación

- 2026-09-18 — HU creada en estado `Borrador` durante la planificación inicial del backlog.

## Notas y decisiones

- Pendiente definir en implementación qué especialidad(es) del catálogo se consideran "generales" (ver nota en [[HU-009-crud-de-especialidades]]).
