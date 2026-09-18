---
id: HU-007
tipo: historia-de-usuario
titulo: "CRUD de EPS"
estado: Borrador
epica: "[[EP-003-catalogos-del-sistema]]"
esfuerzo: "Bajo"
sprint_sugerido: "Sprint 2"
dependencias: ["[[HU-002-iniciar-sesion-y-sesion-jwt]]"]
relacionadas: ["[[HU-008-crud-de-planes-de-eps]]", "[[HU-005-gestionar-afiliacion-eps]]"]
---

# HU-007 — CRUD de EPS

## Historia de usuario

**COMO** ADMIN
**QUIERO** administrar el catálogo de EPS
**PARA** que los usuarios puedan afiliarse a entidades vigentes en el laboratorio

> Como ADMIN, quiero administrar el catálogo de EPS para mantenerlo vigente y coherente.

## Contexto y descripción

Catálogo configurable base de la cadena EPS → planes → afiliación. No se permite borrado físico si la EPS está referenciada por planes/afiliaciones existentes.

## Alcance

- Crear, listar, actualizar y activar/desactivar una EPS.
- Impedir borrado físico de una EPS referenciada por planes o afiliaciones.

## Fuera de alcance

- CRUD de planes (ver [[HU-008-crud-de-planes-de-eps]]).
- Afiliación de usuarios (ver [[HU-005-gestionar-afiliacion-eps]]).

## Reglas de negocio

- RF-06: ADMIN gestiona EPS mediante CRUD.
- Regla explícita del PRD (RF-06): no se permite borrar físicamente un catálogo referenciado por transacciones; usar activación/desactivación.

## Dependencias y relaciones

- Épica: [[EP-003-catalogos-del-sistema]]
- Dependencias: [[HU-002-iniciar-sesion-y-sesion-jwt]] (ADMIN autenticado).
- Relacionadas: [[HU-008-crud-de-planes-de-eps]], [[HU-005-gestionar-afiliacion-eps]].

## Esfuerzo

**Nivel:** Bajo

**Justificación de dificultad:** CRUD estándar de una sola entidad con regla de desactivación en vez de borrado.

## Tareas de desarrollo

- [ ] **T-01 — CRUD de EPS**
  Dificultad: Bajo
  Descripción: Endpoints de creación, listado, actualización y activación/desactivación, restringidos a ADMIN.

- [ ] **T-02 — Regla de borrado protegido**
  Dificultad: Bajo
  Descripción: Impedir eliminación física cuando existan planes o afiliaciones asociadas; forzar desactivación en su lugar.

## Criterios de aceptación

### CA-01 — Alta de EPS

**Dado** un ADMIN autenticado
**Cuando** crea una EPS con datos válidos
**Entonces** la EPS queda disponible en el catálogo

### CA-02 — Desactivación en vez de borrado

**Dado** una EPS referenciada por al menos un plan
**Cuando** un ADMIN intenta eliminarla físicamente
**Entonces** el sistema rechaza el borrado y solo permite desactivarla

### CA-03 — Solo ADMIN administra el catálogo

**Dado** un usuario sin rol ADMIN
**Cuando** intenta crear, actualizar o desactivar una EPS
**Entonces** el sistema rechaza la operación por autorización

## Definition of Done

- [ ] Todos los criterios de aceptación obligatorios están validados con evidencia.
- [ ] Existe una migración Flyway coherente para la tabla de EPS.
- [ ] El contrato REST del CRUD de EPS está documentado — RF-20.
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
