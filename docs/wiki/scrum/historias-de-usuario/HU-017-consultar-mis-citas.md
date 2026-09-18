---
id: HU-017
tipo: historia-de-usuario
titulo: "Consultar mis citas"
estado: Borrador
epica: "[[EP-007-gestion-de-citas-del-usuario]]"
esfuerzo: "Bajo"
sprint_sugerido: "Sprint 3"
dependencias: ["[[HU-015-agendar-cita-general]]", "[[HU-016-solicitar-cita-especializada]]"]
relacionadas: ["[[HU-018-cancelar-cita]]", "[[HU-019-solicitar-reprogramacion]]"]
---

# HU-017 — Consultar mis citas

## Historia de usuario

**COMO** USER
**QUIERO** consultar mis citas y filtrarlas por estado y fecha
**PARA** hacer seguimiento de mis solicitudes y citas aprobadas

> Como USER, quiero consultar mis citas para saber su estado y planificarme.

## Contexto y descripción

Vista central del historial de citas del usuario, con visibilidad mínima obligatoria definida por el PRD.

## Alcance

- Listado de citas propias filtrable por estado y fecha.
- Visualización mínima: sede, profesional, especialidad, fecha/hora, duración, estado, motivo de rechazo cuando exista.

## Fuera de alcance

- Cancelación (ver [[HU-018-cancelar-cita]]).
- Solicitud de reprogramación (ver [[HU-019-solicitar-reprogramacion]]).

## Reglas de negocio

- RF-13: filtro por estado/fecha; visualización mínima obligatoria (sede, profesional, especialidad, fecha/hora, duración, estado, motivo de rechazo).

## Dependencias y relaciones

- Épica: [[EP-007-gestion-de-citas-del-usuario]]
- Dependencias: [[HU-015-agendar-cita-general]], [[HU-016-solicitar-cita-especializada]] (requiere citas existentes).
- Relacionadas: [[HU-018-cancelar-cita]], [[HU-019-solicitar-reprogramacion]].

## Esfuerzo

**Nivel:** Bajo

**Justificación de dificultad:** consulta de solo lectura con filtros simples sobre datos ya existentes; no involucra transiciones de estado.

## Tareas de desarrollo

- [ ] **T-01 — Endpoint de listado de citas propias**
  Dificultad: Bajo
  Descripción: Filtra por estado/fecha y devuelve los campos mínimos exigidos, incluyendo motivo de rechazo cuando exista.

## Criterios de aceptación

### CA-01 — Listado con datos mínimos

**Dado** un USER con al menos una cita
**Cuando** consulta "mis citas"
**Entonces** cada cita muestra sede, profesional, especialidad, fecha/hora, duración y estado

### CA-02 — Filtro por estado y fecha

**Dado** un USER con citas en distintos estados y fechas
**Cuando** filtra por un estado o rango de fecha específico
**Entonces** solo se listan las citas que cumplen ese filtro

### CA-03 — Motivo de rechazo visible

**Dado** una cita especializada `REJECTED` con motivo registrado
**Cuando** el USER la consulta
**Entonces** el motivo de rechazo es visible en la respuesta

## Definition of Done

- [ ] Todos los criterios de aceptación obligatorios están validados con evidencia.
- [ ] El contrato REST de "mis citas" está documentado — RF-20.
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
