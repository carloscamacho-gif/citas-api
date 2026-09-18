---
id: EP-002
tipo: epica
titulo: "Perfil y afiliación"
estado: Borrador
historias: ["[[HU-004-consultar-y-actualizar-perfil]]", "[[HU-005-gestionar-afiliacion-eps]]"]
dependencias: ["[[EP-001-autenticacion-y-sesion]]"]
---

# EP-002 — Perfil y afiliación

## Objetivo

Permitir que un USER autenticado consulte/actualice sus datos permitidos y asocie una afiliación EPS/plan/régimen sin duplicar información.

## Valor esperado

Los datos de afiliación son prerrequisito de negocio para razonar sobre cobertura, aunque el PRD no liga la afiliación a validaciones de agendamiento explícitas; de cualquier forma es un dato de perfil obligatorio del dominio.

## Actores

- USER

## Alcance

- Consulta y actualización de datos de perfil permitidos.
- Alta/actualización de una única afiliación activa por usuario (EPS + plan + régimen derivados de un plan).

## Fuera de alcance

- Gestión del catálogo de EPS/planes/regímenes (ver [[EP-003-catalogos-del-sistema]]), aquí solo se consume el catálogo.
- Validación clínica o de cobertura real (fuera de alcance del PRD, sección 9).

## Reglas de negocio

- RF-04: la aplicación debe evitar duplicar EPS, régimen y plan dentro del usuario — la afiliación referencia un plan, y desde allí se conoce EPS y régimen (ver `database/reference/README_DB.md`).

## Dependencias

- [[EP-001-autenticacion-y-sesion]] (requiere usuario autenticado).
- [[EP-003-catalogos-del-sistema]] (requiere catálogo de EPS/planes existente para poder afiliarse).

## Historias de usuario

- [[HU-004-consultar-y-actualizar-perfil]]
- [[HU-005-gestionar-afiliacion-eps]]

## Criterio de completitud de la épica

- [ ] Todas las HU obligatorias de esta épica están `Completada`.
- [ ] No quedan dependencias bloqueantes dentro del alcance de la épica.

## Riesgos e incógnitas

- El PRD no especifica qué campos de perfil son editables vs. de solo lectura (p. ej. documento). Se documenta como incógnita a resolver en la HU correspondiente, no bloqueante para especificar el comportamiento general.
