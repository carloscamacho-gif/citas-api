---
id: HU-019
tipo: historia-de-usuario
titulo: "Solicitar reprogramación"
estado: Borrador
epica: "[[EP-007-gestion-de-citas-del-usuario]]"
esfuerzo: "Alto"
sprint_sugerido: "Sprint 4"
dependencias: ["[[HU-017-consultar-mis-citas]]", "[[HU-014-consultar-disponibilidad]]"]
relacionadas: ["[[HU-023-bandeja-de-reprogramaciones-pendientes]]", "[[HU-024-auditoria-de-cambios-de-estado]]"]
---

# HU-019 — Solicitar reprogramación

## Historia de usuario

**COMO** USER
**QUIERO** solicitar una nueva fecha/hora para una cita aprobada y futura
**PARA** ajustarla sin perder la cita original mientras se decide

> Como USER, quiero reprogramar una cita aprobada para cambiar su horario sin perder mi reserva actual.

## Contexto y descripción

Conserva profesional y especialidad; cambiar de profesional se trata como una cita nueva. La nueva franja se retiene como `PENDING` mientras la cita original permanece intacta hasta que ADMIN decida.

## Alcance

- Solicitud de reprogramación sobre una cita `APPROVED` y futura, propia del usuario.
- Selección de una nueva fecha/hora disponible, conservando profesional y especialidad.
- Retención de la nueva franja en estado `PENDING` sin liberar la franja original.
- Manejo post-rechazo: el USER puede conservar la cita original o cancelarla.

## Fuera de alcance

- Cambio de profesional (se trata como nueva cita, ver [[HU-015-agendar-cita-general]] / [[HU-016-solicitar-cita-especializada]]).
- Decisión de ADMIN sobre la solicitud (ver [[HU-023-bandeja-de-reprogramaciones-pendientes]]).

## Reglas de negocio

- RF-15: solo una cita aprobada y futura puede solicitar reprogramación; conserva profesional y especialidad; la nueva franja se retiene mientras `PENDING`; la cita original conserva su franja hasta que ADMIN decida; tras rechazo, USER puede conservar o cancelar.
- RN-10: la reprogramación no destruye la cita anterior hasta que sea aprobada.

## Dependencias y relaciones

- Épica: [[EP-007-gestion-de-citas-del-usuario]]
- Dependencias: [[HU-017-consultar-mis-citas]] (origen de la cita a reprogramar), [[HU-014-consultar-disponibilidad]] (elección de nueva franja).
- Relacionadas: [[HU-023-bandeja-de-reprogramaciones-pendientes]], [[HU-024-auditoria-de-cambios-de-estado]].

## Esfuerzo

**Nivel:** Alto

**Justificación de dificultad:** mantiene dos franjas horarias "vivas" simultáneamente (la original reservada y la nueva retenida) sin liberar ninguna hasta la decisión de ADMIN, lo que exige un modelo de datos y reglas de consistencia más complejos que un simple cambio de estado.

## Tareas de desarrollo

- [ ] **T-01 — Endpoint de solicitud de reprogramación**
  Dificultad: Alto
  Descripción: Valida que la cita sea `APPROVED` y futura, retiene la nueva franja (mismo profesional/especialidad) sin liberar la original, y crea la solicitud en `PENDING`.

- [ ] **T-02 — Manejo post-rechazo**
  Dificultad: Medio
  Descripción: Al rechazar, libera la nueva reserva provisional y ofrece al USER conservar o cancelar la cita original.

- [ ] **T-03 — Registro de auditoría**
  Dificultad: Bajo
  Descripción: Registra la creación de la solicitud de reprogramación con fuente `USER`.

## Criterios de aceptación

### CA-01 — Solicitud válida retiene la nueva franja sin tocar la original

**Dado** una cita `APPROVED` futura y una nueva franja disponible del mismo profesional/especialidad
**Cuando** el USER solicita la reprogramación
**Entonces** la solicitud queda `PENDING`, la nueva franja se retiene y la cita original conserva su franja intacta

### CA-02 — Rechazo de reprogramación sobre cita no aprobada o pasada

**Dado** una cita `REQUESTED`, pasada, o en cualquier estado distinto de `APPROVED` futura
**Cuando** el USER intenta solicitar su reprogramación
**Entonces** el sistema rechaza la solicitud

### CA-03 — Rechazo por cambio de profesional

**Dado** una solicitud de reprogramación que intenta cambiar el profesional
**Cuando** se envía
**Entonces** el sistema la rechaza (debe tratarse como una cita nueva)

## Definition of Done

- [ ] Todos los criterios de aceptación obligatorios están validados con evidencia.
- [ ] El contrato REST de solicitud de reprogramación está documentado — RF-20.
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

- Ninguna nota adicional.
