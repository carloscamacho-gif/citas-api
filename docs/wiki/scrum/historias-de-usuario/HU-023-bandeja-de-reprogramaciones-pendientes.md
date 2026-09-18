---
id: HU-023
tipo: historia-de-usuario
titulo: "Bandeja de reprogramaciones pendientes"
estado: Borrador
epica: "[[EP-009-administracion-y-auditoria]]"
esfuerzo: "Alto"
sprint_sugerido: "Sprint 4"
dependencias: ["[[HU-019-solicitar-reprogramacion]]"]
relacionadas: ["[[HU-024-auditoria-de-cambios-de-estado]]"]
---

# HU-023 — Bandeja de reprogramaciones pendientes

## Historia de usuario

**COMO** ADMIN
**QUIERO** ver y decidir sobre las solicitudes de reprogramación `PENDING`
**PARA** aprobar el nuevo horario o mantener la cita original

> Como ADMIN, quiero gestionar la bandeja de reprogramaciones pendientes para decidir sobre cada solicitud.

## Contexto y descripción

Complementa [[HU-022-bandeja-de-citas-especializadas-pendientes]] dentro de la misma bandeja administrativa exigida por RF-18, pero con una máquina de estados distinta: hay dos franjas horarias en juego (original y propuesta).

## Alcance

- Listado de reprogramaciones `PENDING`, filtrable por sede, profesional, especialidad y fecha.
- Aprobación: libera slots antiguos, asigna los nuevos y actualiza la cita.
- Rechazo: libera la nueva reserva provisional y mantiene la cita original, con motivo cuando corresponda.

## Fuera de alcance

- Bandeja de citas especializadas (ver [[HU-022-bandeja-de-citas-especializadas-pendientes]]).
- Creación de la solicitud (ver [[HU-019-solicitar-reprogramacion]]).

## Reglas de negocio

- RF-15: al aprobar, libera slots antiguos, asigna nuevos y actualiza la cita; al rechazar, libera la nueva reserva provisional y mantiene la cita original.
- RF-18: bandeja con filtros por sede, profesional, especialidad y fecha.
- RN-04: rechazo administrativo requiere motivo cuando corresponda.
- RN-09: rechazar libera reservas correspondientes.
- RN-10: la reprogramación no destruye la cita anterior hasta que sea aprobada.

## Dependencias y relaciones

- Épica: [[EP-009-administracion-y-auditoria]]
- Dependencias: [[HU-019-solicitar-reprogramacion]] (origen de las solicitudes).
- Relacionadas: [[HU-024-auditoria-de-cambios-de-estado]].

## Esfuerzo

**Nivel:** Alto

**Justificación de dificultad:** la aprobación debe liberar la franja antigua y confirmar la nueva de forma atómica sobre la misma cita, y el rechazo debe revertir exactamente la franja provisional sin afectar la original — más superficie de estados que una aprobación simple.

## Tareas de desarrollo

- [ ] **T-01 — Endpoint de bandeja filtrable de reprogramaciones**
  Dificultad: Bajo
  Descripción: Lista solicitudes `PENDING` con los filtros exigidos por RF-18.

- [ ] **T-02 — Endpoint de aprobación**
  Dificultad: Alto
  Descripción: Libera los slots de la franja original, confirma los de la nueva franja y actualiza la cita con el nuevo horario.

- [ ] **T-03 — Endpoint de rechazo**
  Dificultad: Medio
  Descripción: Libera la reserva provisional de la nueva franja, mantiene la cita original intacta y exige motivo.

## Criterios de aceptación

### CA-01 — Aprobación actualiza la cita y libera la franja antigua

**Dado** una reprogramación `PENDING`
**Cuando** ADMIN la aprueba
**Entonces** la cita queda con la nueva franja, la franja antigua queda liberada y la nueva confirmada

### CA-02 — Rechazo mantiene la cita original

**Dado** una reprogramación `PENDING`
**Cuando** ADMIN la rechaza con motivo
**Entonces** la nueva franja provisional se libera y la cita original permanece sin cambios en su franja

### CA-03 — Rechazo exige motivo

**Dado** una reprogramación `PENDING`
**Cuando** ADMIN intenta rechazarla sin motivo
**Entonces** el sistema rechaza la operación de rechazo

## Definition of Done

- [ ] Todos los criterios de aceptación obligatorios están validados con evidencia.
- [ ] El contrato REST de la bandeja de reprogramaciones está documentado — RF-20.
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
