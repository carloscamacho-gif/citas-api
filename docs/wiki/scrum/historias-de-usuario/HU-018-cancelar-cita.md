---
id: HU-018
tipo: historia-de-usuario
titulo: "Cancelar cita"
estado: Borrador
epica: "[[EP-007-gestion-de-citas-del-usuario]]"
esfuerzo: "Medio"
sprint_sugerido: "Sprint 3"
dependencias: ["[[HU-017-consultar-mis-citas]]"]
relacionadas: ["[[HU-013-discretizar-bloques-en-slots]]", "[[HU-024-auditoria-de-cambios-de-estado]]"]
---

# HU-018 — Cancelar cita

## Historia de usuario

**COMO** USER
**QUIERO** cancelar una cita futura que ya no necesito
**PARA** liberar el horario y evitar una cita fantasma

> Como USER, quiero cancelar una cita futura para liberar el horario reservado.

## Contexto y descripción

Solo aplica a citas futuras no terminales (no `COMPLETED`, `NO_SHOW`, `CANCELLED` o `REJECTED` previamente). La cancelación libera los slots ocupados/retenidos.

## Alcance

- Cancelación de una cita futura no terminal propia del USER.
- Liberación de los slots asociados al cancelar.
- Registro de historial del cambio de estado.

## Fuera de alcance

- Reactivación de una cita cancelada (explícitamente no permitida por el PRD).
- Cancelación de citas de otros usuarios (restricción de ownership).

## Reglas de negocio

- RF-14: `CANCELLED` libera los slots; una cita cancelada no se reactiva directamente; debe registrarse historial.
- RN-09: cancelar/rechazar libera reservas correspondientes.

## Dependencias y relaciones

- Épica: [[EP-007-gestion-de-citas-del-usuario]]
- Dependencias: [[HU-017-consultar-mis-citas]] (el usuario identifica la cita a cancelar desde su listado).
- Relacionadas: [[HU-013-discretizar-bloques-en-slots]] (liberación de slots), [[HU-024-auditoria-de-cambios-de-estado]].

## Esfuerzo

**Nivel:** Medio

**Justificación de dificultad:** exige validar que la cita sea futura y no terminal, liberar slots de forma consistente y registrar auditoría, coordinando dos agregados (cita y slots).

## Tareas de desarrollo

- [ ] **T-01 — Endpoint de cancelación**
  Dificultad: Medio
  Descripción: Valida ownership, que la cita sea futura y no terminal; cambia el estado a `CANCELLED` y libera los slots asociados.

- [ ] **T-02 — Registro de auditoría de la cancelación**
  Dificultad: Bajo
  Descripción: Registra la transición a `CANCELLED` con fuente `USER`.

## Criterios de aceptación

### CA-01 — Cancelación exitosa libera slots

**Dado** una cita futura `APPROVED` propia del USER
**Cuando** la cancela
**Entonces** la cita queda `CANCELLED` y sus slots vuelven a estar disponibles

### CA-02 — Rechazo de cancelación de cita pasada o terminal

**Dado** una cita pasada, `COMPLETED`, `NO_SHOW`, `CANCELLED` o `REJECTED`
**Cuando** el USER intenta cancelarla
**Entonces** el sistema rechaza la operación

### CA-03 — No reactivación directa

**Dado** una cita `CANCELLED`
**Cuando** se intenta revertirla directamente a un estado activo
**Entonces** el sistema no ofrece esa operación (debe agendarse una cita nueva)

## Definition of Done

- [ ] Todos los criterios de aceptación obligatorios están validados con evidencia.
- [ ] El contrato REST de cancelación está documentado — RF-20.
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
