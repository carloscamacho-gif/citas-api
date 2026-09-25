---
id: HU-016
tipo: historia-de-usuario
titulo: "Solicitar cita especializada"
estado: Aprobada
epica: "[[EP-006-busqueda-y-agendamiento-de-citas]]"
esfuerzo: "Medio"
sprint_sugerido: "Sprint 2"
dependencias: ["[[HU-014-consultar-disponibilidad]]"]
relacionadas: ["[[HU-022-bandeja-de-citas-especializadas-pendientes]]", "[[HU-024-auditoria-de-cambios-de-estado]]"]
---

# HU-016 — Solicitar cita especializada

## Historia de usuario

**COMO** USER
**QUIERO** solicitar una cita especializada eligiendo especialidad, sede, profesional y horario
**PARA** reservar mi turno mientras espero la aprobación de ADMIN

> Como USER, quiero solicitar una cita especializada para que quede en trámite hasta que ADMIN la apruebe.

## Contexto y descripción

A diferencia de la cita general, la especializada nace `REQUESTED` y retiene el horario para evitar doble reserva mientras se decide.

## Alcance

- Solicitud de cita especializada especificando especialidad, sede, profesional y horario.
- Retención del/los slot(s) elegidos en estado `REQUESTED` (no disponibles para otras solicitudes).

## Fuera de alcance

- Decisión de aprobar/rechazar (ver [[HU-022-bandeja-de-citas-especializadas-pendientes]]).
- Cita general (ver [[HU-015-agendar-cita-general]]).

## Reglas de negocio

- RF-12: la solicitud nace `REQUESTED`; el horario queda retenido para evitar doble reserva.
- RN-01: ninguna cita puede ocupar slots ya reservados/retenidos.
- RN-03: citas especializadas requieren ADMIN.
- RN-08: una especialidad debe estar activa y asociada al profesional para reservarse.

## Dependencias y relaciones

- Épica: [[EP-006-busqueda-y-agendamiento-de-citas]]
- Dependencias: [[HU-014-consultar-disponibilidad]].
- Relacionadas: [[HU-022-bandeja-de-citas-especializadas-pendientes]], [[HU-024-auditoria-de-cambios-de-estado]].

## Esfuerzo

**Nivel:** Medio

**Justificación de dificultad:** similar a la cita general pero con un estado intermedio de retención que debe sobrevivir hasta la decisión de ADMIN, sin liberar el slot mientras tanto.

## Tareas de desarrollo

- [ ] **T-01 — Endpoint de solicitud de cita especializada**
  Dificultad: Medio
  Descripción: Valida que la especialidad esté activa y asociada al profesional, retiene el/los slot(s) y crea la cita en `REQUESTED`.

- [ ] **T-02 — Registro de auditoría de la solicitud**
  Dificultad: Bajo
  Descripción: Registra en el historial de estados la creación con estado `REQUESTED`, fuente `USER`.

## Criterios de aceptación

### CA-01 — Solicitud exitosa retiene el horario

**Dado** un horario especializado disponible
**Cuando** el USER solicita la cita
**Entonces** la cita queda `REQUESTED` y el horario deja de estar disponible para otras solicitudes

### CA-02 — Rechazo de especialidad inactiva o no asociada

**Dado** una especialidad inactiva o no asociada al profesional elegido
**Cuando** el USER intenta solicitar la cita
**Entonces** el sistema rechaza la solicitud

### CA-03 — Auditoría de la solicitud

**Dado** una solicitud creada exitosamente
**Cuando** se consulta su historial de estados
**Entonces** existe un registro de la transición a `REQUESTED` con fecha/hora y fuente `USER`

## Definition of Done

- [ ] Todos los criterios de aceptación obligatorios están validados con evidencia.
- [ ] El contrato REST de solicitud de cita especializada está documentado — RF-20.
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

- Ninguna nota adicional.
