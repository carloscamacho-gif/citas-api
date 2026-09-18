---
id: HU-004
tipo: historia-de-usuario
titulo: "Consultar y actualizar perfil"
estado: Borrador
epica: "[[EP-002-perfil-y-afiliacion]]"
esfuerzo: "Bajo"
sprint_sugerido: "Sprint 3"
dependencias: ["[[HU-002-iniciar-sesion-y-sesion-jwt]]"]
relacionadas: ["[[HU-005-gestionar-afiliacion-eps]]"]
---

# HU-004 — Consultar y actualizar perfil

## Historia de usuario

**COMO** USER autenticado
**QUIERO** consultar y actualizar los datos permitidos de mi perfil
**PARA** mantener mi información de contacto correcta

> Como USER autenticado, quiero consultar y actualizar mi perfil para mantener mi información correcta.

## Contexto y descripción

Todo usuario autenticado debe poder ver sus propios datos y corregir los que sean editables (p. ej. teléfono), sin poder modificar identificadores que rompen unicidad (email/documento) sin un flujo controlado.

## Alcance

- Consulta de los datos propios de perfil.
- Actualización de los campos permitidos (a definir en implementación: al menos nombres, apellidos, teléfono).

## Fuera de alcance

- Cambio de email o documento (no contemplado explícitamente por el PRD; se trata como fuera de alcance salvo decisión posterior).
- Gestión de afiliación EPS/plan/régimen (ver [[HU-005-gestionar-afiliacion-eps]]).

## Reglas de negocio

- RF-04: USER consulta/actualiza datos permitidos de su perfil.
- Autorización por ownership: un USER solo puede ver/editar su propio perfil (Sección 8 del PRD).

## Dependencias y relaciones

- Épica: [[EP-002-perfil-y-afiliacion]]
- Dependencias: [[HU-002-iniciar-sesion-y-sesion-jwt]] (requiere sesión autenticada).
- Relacionadas: [[HU-005-gestionar-afiliacion-eps]].

## Esfuerzo

**Nivel:** Bajo

**Justificación de dificultad:** CRUD acotado sobre una sola entidad ya existente (usuario), con reglas de ownership simples.

## Tareas de desarrollo

- [ ] **T-01 — Endpoint de consulta de perfil propio**
  Dificultad: Bajo
  Descripción: Retorna los datos del usuario autenticado según su identidad en el token.

- [ ] **T-02 — Endpoint de actualización de perfil**
  Dificultad: Bajo
  Descripción: Actualiza solo los campos permitidos, validando que el usuario autenticado sea el dueño del recurso.

## Criterios de aceptación

### CA-01 — Consulta de perfil propio

**Dado** un USER autenticado
**Cuando** consulta su perfil
**Entonces** recibe únicamente sus propios datos permitidos

### CA-02 — Actualización de campos permitidos

**Dado** un USER autenticado
**Cuando** actualiza un campo permitido (p. ej. teléfono) con un valor válido
**Entonces** el cambio se persiste y se refleja en consultas posteriores

### CA-03 — No se puede editar el perfil de otro usuario

**Dado** un USER autenticado
**Cuando** intenta actualizar el perfil de otro usuario
**Entonces** el sistema rechaza la operación por falta de ownership

## Definition of Done

- [ ] Todos los criterios de aceptación obligatorios están validados con evidencia.
- [ ] El contrato REST de consulta/actualización de perfil está documentado — RF-20.
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

- Pendiente definir la lista exacta de campos editables vs. de solo lectura al implementar T-02.
