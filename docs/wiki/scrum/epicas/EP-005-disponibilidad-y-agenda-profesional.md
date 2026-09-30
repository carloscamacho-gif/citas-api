---
id: EP-005
tipo: epica
titulo: "Disponibilidad y agenda del profesional"
estado: Borrador
historias: ["[[HU-012-gestionar-bloques-de-disponibilidad]]", "[[HU-013-discretizar-bloques-en-slots]]"]
dependencias: ["[[EP-004-gestion-de-profesionales]]"]
---

# EP-005 — Disponibilidad y agenda del profesional

## Objetivo

Permitir que PROFESSIONAL declare bloques de disponibilidad por día y sede, discretizados en slots atómicos según la duración de cada especialidad.

## Valor esperado

Es la fuente de verdad de horarios reservables; sin slots no hay disponibilidad que buscar ni citas que agendar (ver [[EP-006-busqueda-y-agendamiento-de-citas]]).

## Actores

- PROFESSIONAL

## Alcance

- Crear múltiples bloques por día, seleccionando sede por bloque.
- Editar/eliminar bloques futuros sin citas comprometidas.
- Consultar el propio calendario.
- Discretizar cada bloque en slots de 30 minutos.

## Fuera de alcance

- Búsqueda de disponibilidad desde la perspectiva de USER (ver [[EP-006-busqueda-y-agendamiento-de-citas]]).
- Reservar/retener slots por una cita (esa lógica pertenece a la épica de agendamiento, aunque consume este modelo).

## Reglas de negocio

- RF-08: no crear bloques en el pasado; no solapar bloques del mismo profesional; el profesional debe estar habilitado en la sede.
- RF-09: cada especialidad define 30 o 60 minutos; 60 min = 2 slots consecutivos disponibles; el profesional no sobrescribe la duración de la especialidad.
- RN-05: slots deben ser consecutivos cuando la duración es 60 min.
- RN-06: no se permiten citas ni bloques en el pasado.
- RN-07: un profesional solo publica agenda en sedes asignadas.

## Dependencias

- [[EP-004-gestion-de-profesionales]] (el profesional debe existir, estar activo y tener sedes asignadas).
- [[EP-003-catalogos-del-sistema]] (duración de especialidad).

## Historias de usuario

- [[HU-012-gestionar-bloques-de-disponibilidad]]
- [[HU-013-discretizar-bloques-en-slots]]

## Criterio de completitud de la épica

- [ ] Todas las HU obligatorias de esta épica están `Completada`.
- [ ] No quedan dependencias bloqueantes dentro del alcance de la épica.

## Riesgos e incógnitas

- El ejemplo del PRD (08:00–12:00 y 14:00–17:00) no aclara si un bloque puede abarcar varias especialidades a la vez o si el bloque es agnóstico de especialidad y esta se decide al reservar; se documenta como incógnita a resolver en HU-012 antes de implementar.
