---
id: HU-008
tipo: historia-de-usuario
titulo: "CRUD de planes de EPS"
estado: Borrador
epica: "[[EP-003-catalogos-del-sistema]]"
esfuerzo: "Bajo"
sprint_sugerido: "Sprint 2"
dependencias: ["[[HU-007-crud-de-eps]]"]
relacionadas: ["[[HU-005-gestionar-afiliacion-eps]]"]
---

# HU-008 — CRUD de planes de EPS

## Historia de usuario

**COMO** ADMIN
**QUIERO** administrar los planes de cada EPS
**PARA** que los usuarios puedan afiliarse a un plan concreto y vigente

> Como ADMIN, quiero administrar los planes de EPS para mantener el catálogo de afiliación completo.

## Contexto y descripción

Cada plan pertenece a una EPS y, según `database/reference/README_DB.md`, es el punto desde el cual se deriva tanto la EPS como el régimen al momento de afiliar un usuario.

## Alcance

- Crear, listar, actualizar y activar/desactivar un plan asociado a una EPS y a un régimen.
- Impedir borrado físico de un plan referenciado por afiliaciones existentes.

## Fuera de alcance

- CRUD de EPS (ver [[HU-007-crud-de-eps]]).
- Afiliación de usuarios (ver [[HU-005-gestionar-afiliacion-eps]]).

## Reglas de negocio

- RF-06: ADMIN gestiona planes de EPS mediante CRUD.
- Regla explícita del PRD (RF-06): no se permite borrar físicamente un catálogo referenciado por transacciones; usar activación/desactivación.
- `database/reference/README_DB.md`: el plan es el punto de referencia único desde el que se conoce EPS y régimen (evita duplicación, RF-04).

## Dependencias y relaciones

- Épica: [[EP-003-catalogos-del-sistema]]
- Dependencias: [[HU-007-crud-de-eps]] (un plan requiere una EPS existente).
- Relacionadas: [[HU-005-gestionar-afiliacion-eps]].

## Esfuerzo

**Nivel:** Bajo

**Justificación de dificultad:** CRUD estándar con una relación simple hacia EPS y régimen.

## Tareas de desarrollo

- [ ] **T-01 — CRUD de planes**
  Dificultad: Bajo
  Descripción: Endpoints de creación, listado, actualización y activación/desactivación de planes, asociados a una EPS y un régimen fijo.

- [ ] **T-02 — Regla de borrado protegido**
  Dificultad: Bajo
  Descripción: Impedir eliminación física cuando existan afiliaciones asociadas; forzar desactivación.

## Criterios de aceptación

### CA-01 — Alta de plan asociado a EPS

**Dado** un ADMIN autenticado y una EPS existente
**Cuando** crea un plan para esa EPS con un régimen válido
**Entonces** el plan queda disponible en el catálogo, vinculado a esa EPS y régimen

### CA-02 — Desactivación en vez de borrado

**Dado** un plan referenciado por al menos una afiliación
**Cuando** un ADMIN intenta eliminarlo físicamente
**Entonces** el sistema rechaza el borrado y solo permite desactivarlo

### CA-03 — Rechazo de plan sin EPS válida

**Dado** un identificador de EPS inexistente
**Cuando** un ADMIN intenta crear un plan para esa EPS
**Entonces** el sistema rechaza la operación

## Definition of Done

- [ ] Todos los criterios de aceptación obligatorios están validados con evidencia.
- [ ] Existe una migración Flyway coherente para la tabla de planes de EPS.
- [ ] El contrato REST del CRUD de planes está documentado — RF-20.
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
