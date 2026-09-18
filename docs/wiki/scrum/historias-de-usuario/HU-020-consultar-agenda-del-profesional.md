---
id: HU-020
tipo: historia-de-usuario
titulo: "Consultar agenda del profesional"
estado: Borrador
epica: "[[EP-008-atencion-del-profesional]]"
esfuerzo: "Bajo"
sprint_sugerido: "Sprint 3"
dependencias: ["[[HU-015-agendar-cita-general]]", "[[HU-016-solicitar-cita-especializada]]"]
relacionadas: ["[[HU-021-cerrar-atencion]]"]
---

# HU-020 — Consultar agenda del profesional

## Historia de usuario

**COMO** PROFESSIONAL
**QUIERO** consultar mis citas aprobadas por día, semana y sede
**PARA** organizar mi atención sin ver datos de usuarios ajenos a mis citas

> Como PROFESSIONAL, quiero consultar mi agenda para organizar mi jornada.

## Contexto y descripción

Vista de solo lectura restringida a las propias citas `APPROVED` del profesional autenticado; no debe filtrar datos de usuarios fuera de sus propias citas.

## Alcance

- Listado de citas `APPROVED` propias, filtrable por día/semana y sede.

## Fuera de alcance

- Cierre de atención (ver [[HU-021-cerrar-atencion]]).
- Gestión de bloques de disponibilidad (ver [[HU-012-gestionar-bloques-de-disponibilidad]]).

## Reglas de negocio

- RF-16: PROFESSIONAL consulta sus citas `APPROVED` por día/semana y sede; no puede ver datos de usuarios fuera de sus propias citas.

## Dependencias y relaciones

- Épica: [[EP-008-atencion-del-profesional]]
- Dependencias: [[HU-015-agendar-cita-general]], [[HU-016-solicitar-cita-especializada]] (requiere citas `APPROVED` existentes).
- Relacionadas: [[HU-021-cerrar-atencion]].

## Esfuerzo

**Nivel:** Bajo

**Justificación de dificultad:** consulta de solo lectura con ownership por profesional y filtros simples.

## Tareas de desarrollo

- [ ] **T-01 — Endpoint de agenda del profesional**
  Dificultad: Bajo
  Descripción: Lista citas `APPROVED` del profesional autenticado, filtrable por rango de fecha y sede.

## Criterios de aceptación

### CA-01 — Listado restringido a citas propias aprobadas

**Dado** un PROFESSIONAL autenticado con citas `APPROVED`
**Cuando** consulta su agenda
**Entonces** solo ve sus propias citas `APPROVED`, no las de otros profesionales

### CA-02 — Filtro por sede y rango de fecha

**Dado** citas `APPROVED` en distintas sedes y fechas
**Cuando** el profesional filtra por sede y/o rango de fecha
**Entonces** solo se listan las citas que cumplen ese filtro

## Definition of Done

- [ ] Todos los criterios de aceptación obligatorios están validados con evidencia.
- [ ] El contrato REST de agenda del profesional está documentado — RF-20.
- [ ] Existe al menos una prueba de autorización que confirme que un profesional no accede a la agenda de otro (requisito de verificación S3).
- [ ] La trazabilidad de esta HU y su épica está actualizada en `docs/wiki/scrum/`.

## Evidencia de validación

| Elemento | Resultado | Evidencia | Observación |
|---|---|---|---|
| CA-01 | Pendiente | — | — |
| CA-02 | Pendiente | — | — |
| DoD-01 | Pendiente | — | — |

## Historial de validación

- 2026-09-18 — HU creada en estado `Borrador` durante la planificación inicial del backlog.

## Notas y decisiones

- Ninguna nota adicional.
