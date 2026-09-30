---
id: HU-022
tipo: historia-de-usuario
titulo: "Bandeja de citas especializadas pendientes"
estado: Aprobada
epica: "[[EP-009-administracion-y-auditoria]]"
esfuerzo: "Medio"
sprint_sugerido: "Sprint 2"
dependencias: ["[[HU-016-solicitar-cita-especializada]]"]
relacionadas: ["[[HU-024-auditoria-de-cambios-de-estado]]"]
---

# HU-022 — Bandeja de citas especializadas pendientes

## Historia de usuario

**COMO** ADMIN
**QUIERO** ver y decidir sobre las citas especializadas `REQUESTED`
**PARA** aprobarlas o rechazarlas con motivo

> Como ADMIN, quiero gestionar la bandeja de citas especializadas pendientes para decidir sobre cada solicitud.

## Contexto y descripción

Es el punto de control humano exigido por RF-12/RF-18 para las citas que requieren aprobación administrativa.

## Alcance

- Listado de citas especializadas `REQUESTED`, filtrable por sede, profesional, especialidad y fecha.
- Aprobación: transición a `APPROVED`.
- Rechazo: transición a `REJECTED` con motivo obligatorio, liberando los slots retenidos.

## Fuera de alcance

- Bandeja de reprogramaciones (ver [[HU-023-bandeja-de-reprogramaciones-pendientes]]).
- Creación de la solicitud (ver [[HU-016-solicitar-cita-especializada]]).

## Reglas de negocio

- RF-12: al aprobar, estado `APPROVED`; al rechazar, estado `REJECTED` y se liberan los slots.
- RF-18: bandeja con filtros por sede, profesional, especialidad y fecha.
- RN-03: citas especializadas requieren ADMIN.
- RN-04: rechazo administrativo requiere motivo.
- RN-09: rechazar libera reservas correspondientes.

## Dependencias y relaciones

- Épica: [[EP-009-administracion-y-auditoria]]
- Dependencias: [[HU-016-solicitar-cita-especializada]] (origen de las solicitudes).
- Relacionadas: [[HU-024-auditoria-de-cambios-de-estado]].

## Esfuerzo

**Nivel:** Medio

**Justificación de dificultad:** combina listado filtrado con dos transiciones de estado que además deben coordinar la liberación de slots (rechazo) o su confirmación (aprobación).

## Tareas de desarrollo

- [ ] **T-01 — Endpoint de bandeja filtrable**
  Dificultad: Bajo
  Descripción: Lista citas especializadas `REQUESTED` con filtros por sede, profesional, especialidad y fecha.

- [ ] **T-02 — Endpoint de aprobación/rechazo**
  Dificultad: Medio
  Descripción: Aprueba (mantiene slots retenidos como definitivos) o rechaza (exige motivo y libera slots) una solicitud `REQUESTED`.

- [ ] **T-03 — Registro de auditoría de la decisión**
  Dificultad: Bajo
  Descripción: Registra la transición con fuente `ADMIN` y motivo cuando aplique.

## Criterios de aceptación

### CA-01 — Listado filtrado de solicitudes pendientes

**Dado** citas especializadas `REQUESTED` en distintas sedes/especialidades
**Cuando** ADMIN filtra la bandeja
**Entonces** solo se listan las solicitudes que cumplen el filtro aplicado

### CA-02 — Aprobación exitosa

**Dado** una cita especializada `REQUESTED`
**Cuando** ADMIN la aprueba
**Entonces** la cita queda `APPROVED` y sus slots quedan confirmados

### CA-03 — Rechazo exige motivo y libera slots

**Dado** una cita especializada `REQUESTED`
**Cuando** ADMIN la rechaza sin proporcionar un motivo
**Entonces** el sistema rechaza la operación de rechazo por falta de motivo

**Dado** una cita especializada `REQUESTED`
**Cuando** ADMIN la rechaza con un motivo
**Entonces** la cita queda `REJECTED` con ese motivo y sus slots quedan liberados

## Definition of Done

- [ ] Todos los criterios de aceptación obligatorios están validados con evidencia.
- [ ] El contrato REST de la bandeja y sus decisiones está documentado — RF-20.
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
