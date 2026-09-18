---
id: EP-008
tipo: epica
titulo: "Atención del profesional"
estado: Borrador
historias: ["[[HU-020-consultar-agenda-del-profesional]]", "[[HU-021-cerrar-atencion]]"]
dependencias: ["[[EP-006-busqueda-y-agendamiento-de-citas]]"]
---

# EP-008 — Atención del profesional

## Objetivo

Permitir que PROFESSIONAL consulte su agenda de citas `APPROVED` por día/semana/sede y marque el resultado de la atención (`COMPLETED` o `NO_SHOW`).

## Valor esperado

Cierra el ciclo operativo de la cita desde la perspectiva del profesional, con trazabilidad del resultado real de la atención.

## Actores

- PROFESSIONAL

## Alcance

- Consulta de citas `APPROVED` propias, filtrable por día/semana y sede.
- Marcado de una cita pasada/aplicable como `COMPLETED` o `NO_SHOW`, con historial de cambio.

## Fuera de alcance

- Visualización de datos de usuarios fuera de las propias citas del profesional (restricción explícita del PRD).
- Edición de bloques de disponibilidad (ver [[EP-005-disponibilidad-y-agenda-profesional]]).

## Reglas de negocio

- RF-16: PROFESSIONAL no puede ver datos de usuarios fuera de sus propias citas.
- RF-17: debe registrarse historial del cambio a `COMPLETED`/`NO_SHOW`.
- RN-11: transiciones de estado deben ser explícitas y verificables.

## Dependencias

- [[EP-006-busqueda-y-agendamiento-de-citas]] (requiere citas `APPROVED` existentes).
- [[EP-009-administracion-y-auditoria]] (toda transición se audita vía [[HU-024-auditoria-de-cambios-de-estado]]).

## Historias de usuario

- [[HU-020-consultar-agenda-del-profesional]]
- [[HU-021-cerrar-atencion]]

## Criterio de completitud de la épica

- [ ] Todas las HU obligatorias de esta épica están `Completada`.
- [ ] No quedan dependencias bloqueantes dentro del alcance de la épica.

## Riesgos e incógnitas

- El PRD no define un umbral exacto de "cita pasada/aplicable" (¿inmediatamente tras la hora de fin del slot?); se documenta como decisión a tomar en HU-021.
