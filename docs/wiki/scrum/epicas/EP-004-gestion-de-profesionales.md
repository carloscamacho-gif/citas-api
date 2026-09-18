---
id: EP-004
tipo: epica
titulo: "Gestión de profesionales"
estado: Borrador
historias: ["[[HU-010-crear-profesional-con-especialidades-y-sedes]]", "[[HU-011-activar-desactivar-profesional]]"]
dependencias: ["[[EP-001-autenticacion-y-sesion]]", "[[EP-003-catalogos-del-sistema]]"]
---

# EP-004 — Gestión de profesionales

## Objetivo

Permitir que ADMIN cree y configure profesionales ficticios: usuario base, código/matrícula, especialidades (con una primaria), sedes habilitadas y estado de actividad.

## Valor esperado

Sin profesionales configurados no existen bloques de disponibilidad ni citas posibles; es prerrequisito directo de [[EP-005-disponibilidad-y-agenda-profesional]] y [[EP-006-busqueda-y-agendamiento-de-citas]].

## Actores

- ADMIN
- PROFESSIONAL (creado por ADMIN, no se autogestiona en esta épica)

## Alcance

- Alta de usuario PROFESSIONAL con código profesional y matrícula ficticia.
- Asignación de una o varias especialidades, marcando una primaria.
- Asignación de una o ambas sedes.
- Activación/desactivación del profesional.

## Fuera de alcance

- Gestión de disponibilidad/agenda propia del profesional (ver [[EP-005-disponibilidad-y-agenda-profesional]]).
- Autorregistro del profesional (PRD: "no aprueba citas... lo crea ADMIN").

## Reglas de negocio

- RF-07: el profesional puede pertenecer a una o varias especialidades y trabajar en una o ambas sedes; ADMIN marca una especialidad primaria.
- RN-07: un profesional solo publica agenda en sedes asignadas (validado en [[EP-005-disponibilidad-y-agenda-profesional]], pero la asignación nace aquí).
- RN-08: una especialidad debe estar activa y asociada al profesional para reservarse.

## Dependencias

- [[EP-001-autenticacion-y-sesion]] (ADMIN autenticado; el alta de PROFESSIONAL crea una cuenta de usuario).
- [[EP-003-catalogos-del-sistema]] (especialidades y sedes deben existir y estar activas).

## Historias de usuario

- [[HU-010-crear-profesional-con-especialidades-y-sedes]]
- [[HU-011-activar-desactivar-profesional]]

## Criterio de completitud de la épica

- [ ] Todas las HU obligatorias de esta épica están `Completada`.
- [ ] No quedan dependencias bloqueantes dentro del alcance de la épica.

## Riesgos e incógnitas

- El PRD no indica si desactivar un profesional debe cancelar automáticamente sus citas futuras; se documenta como decisión pendiente de negocio a resolver antes de implementar HU-011.
