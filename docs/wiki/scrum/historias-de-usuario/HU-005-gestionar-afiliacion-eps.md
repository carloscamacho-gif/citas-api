---
id: HU-005
tipo: historia-de-usuario
titulo: "Gestionar afiliación EPS/plan/régimen"
estado: Borrador
epica: "[[EP-002-perfil-y-afiliacion]]"
esfuerzo: "Medio"
sprint_sugerido: "Sprint 2"
dependencias: ["[[HU-002-iniciar-sesion-y-sesion-jwt]]", "[[HU-007-crud-de-eps]]", "[[HU-008-crud-de-planes-de-eps]]"]
relacionadas: ["[[HU-004-consultar-y-actualizar-perfil]]"]
---

# HU-005 — Gestionar afiliación EPS/plan/régimen

## Historia de usuario

**COMO** USER autenticado
**QUIERO** asociar mi EPS y plan a mi perfil
**PARA** que mi afiliación quede registrada sin duplicar datos de EPS, régimen y plan

> Como USER autenticado, quiero asociar mi afiliación EPS/plan para tener mi cobertura registrada correctamente.

## Contexto y descripción

La afiliación referencia un plan de EPS existente; el régimen y la EPS se derivan de ese plan (no se guardan como texto libre en el usuario), evitando duplicación según `database/reference/README_DB.md`.

## Alcance

- Alta de afiliación seleccionando un plan de EPS activo del catálogo.
- Consulta de la afiliación actual del usuario (EPS, régimen y plan derivados).
- Reemplazo de la afiliación existente (una única afiliación activa por usuario).

## Fuera de alcance

- Gestión del catálogo de EPS/planes/regímenes (ver [[HU-007-crud-de-eps]], [[HU-008-crud-de-planes-de-eps]]).
- Validación de cobertura real o facturación (fuera de alcance del PRD, sección 9).

## Reglas de negocio

- RF-04: la aplicación debe evitar duplicar EPS, régimen y plan dentro del usuario.

## Dependencias y relaciones

- Épica: [[EP-002-perfil-y-afiliacion]]
- Dependencias: [[HU-002-iniciar-sesion-y-sesion-jwt]], [[HU-007-crud-de-eps]], [[HU-008-crud-de-planes-de-eps]] (requiere catálogo de EPS/planes activo).
- Relacionadas: [[HU-004-consultar-y-actualizar-perfil]].

## Esfuerzo

**Nivel:** Medio

**Justificación de dificultad:** involucra relaciones entre tres entidades de catálogo (régimen, EPS, plan) y la regla de no duplicación, aunque el flujo de usuario es simple.

## Tareas de desarrollo

- [ ] **T-01 — Endpoint de alta/reemplazo de afiliación**
  Dificultad: Medio
  Descripción: Recibe el plan seleccionado, valida que esté activo y crea/reemplaza la afiliación única del usuario.

- [ ] **T-02 — Endpoint de consulta de afiliación**
  Dificultad: Bajo
  Descripción: Retorna la afiliación actual del usuario con EPS y régimen derivados del plan (sin duplicar esos datos en la tabla de afiliación).

## Criterios de aceptación

### CA-01 — Alta de afiliación válida

**Dado** un USER autenticado y un plan de EPS activo
**Cuando** registra su afiliación con ese plan
**Entonces** la afiliación queda asociada al usuario y la consulta posterior muestra EPS y régimen derivados correctamente

### CA-02 — Rechazo de plan inactivo

**Dado** un plan de EPS desactivado
**Cuando** un USER intenta afiliarse a ese plan
**Entonces** el sistema rechaza la operación

### CA-03 — Reemplazo de afiliación existente

**Dado** un USER con una afiliación previa
**Cuando** registra una nueva afiliación con un plan distinto
**Entonces** la afiliación anterior se reemplaza sin quedar duplicada

## Definition of Done

- [ ] Todos los criterios de aceptación obligatorios están validados con evidencia.
- [ ] El contrato REST de afiliación está documentado — RF-20.
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
