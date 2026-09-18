---
id: HU-009
tipo: historia-de-usuario
titulo: "CRUD de especialidades"
estado: Borrador
epica: "[[EP-003-catalogos-del-sistema]]"
esfuerzo: "Bajo"
sprint_sugerido: "Sprint 2"
dependencias: ["[[HU-002-iniciar-sesion-y-sesion-jwt]]"]
relacionadas: ["[[HU-010-crear-profesional-con-especialidades-y-sedes]]", "[[HU-013-discretizar-bloques-en-slots]]"]
---

# HU-009 — CRUD de especialidades

## Historia de usuario

**COMO** ADMIN
**QUIERO** administrar el catálogo de especialidades y su duración
**PARA** que los profesionales puedan asignarse especialidades válidas y las citas respeten la duración correcta

> Como ADMIN, quiero administrar las especialidades para que el resto del sistema pueda apoyarse en un catálogo confiable.

## Contexto y descripción

Cada especialidad define su duración (30 o 60 minutos), valor que el profesional no puede sobrescribir (RF-09). Es la base de asignación de profesionales (RF-07) y de discretización de slots (RF-09).

## Alcance

- Crear, listar, actualizar y activar/desactivar una especialidad.
- Definir la duración de la especialidad (30 o 60 minutos) al crearla/editarla.
- Impedir borrado físico de una especialidad referenciada por profesionales o citas.

## Fuera de alcance

- Asignación de especialidades a un profesional concreto (ver [[HU-010-crear-profesional-con-especialidades-y-sedes]]).
- Discretización de bloques en slots (ver [[HU-013-discretizar-bloques-en-slots]]), que consume esta duración pero no la define.

## Reglas de negocio

- RF-06: ADMIN gestiona especialidades mediante CRUD.
- RF-09: cada especialidad define 30 o 60 minutos.
- RN-08: una especialidad debe estar activa y asociada al profesional para reservarse.
- Regla explícita del PRD (RF-06): no se permite borrado físico de un catálogo referenciado; usar activación/desactivación.

## Dependencias y relaciones

- Épica: [[EP-003-catalogos-del-sistema]]
- Dependencias: [[HU-002-iniciar-sesion-y-sesion-jwt]] (ADMIN autenticado).
- Relacionadas: [[HU-010-crear-profesional-con-especialidades-y-sedes]], [[HU-013-discretizar-bloques-en-slots]].

## Esfuerzo

**Nivel:** Bajo

**Justificación de dificultad:** CRUD estándar con un campo de dominio adicional (duración) restringido a dos valores válidos.

## Tareas de desarrollo

- [ ] **T-01 — CRUD de especialidades**
  Dificultad: Bajo
  Descripción: Endpoints de creación, listado, actualización y activación/desactivación, incluyendo el campo de duración (30 o 60 minutos).

- [ ] **T-02 — Validación de duración**
  Dificultad: Bajo
  Descripción: Restringir el valor de duración exclusivamente a 30 o 60 minutos.

## Criterios de aceptación

### CA-01 — Alta de especialidad con duración válida

**Dado** un ADMIN autenticado
**Cuando** crea una especialidad con duración de 30 o 60 minutos
**Entonces** la especialidad queda disponible en el catálogo con esa duración

### CA-02 — Rechazo de duración inválida

**Dado** un ADMIN autenticado
**Cuando** intenta crear una especialidad con una duración distinta de 30 o 60 minutos
**Entonces** el sistema rechaza la operación

### CA-03 — Desactivación en vez de borrado

**Dado** una especialidad referenciada por al menos un profesional
**Cuando** un ADMIN intenta eliminarla físicamente
**Entonces** el sistema rechaza el borrado y solo permite desactivarla

## Definition of Done

- [ ] Todos los criterios de aceptación obligatorios están validados con evidencia.
- [ ] Existe una migración Flyway coherente para la tabla de especialidades.
- [ ] El contrato REST del CRUD de especialidades está documentado — RF-20.
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

- Pendiente decidir si "Medicina General" es un dato semilla obligatorio de esta especialidad (necesario para [[HU-015-agendar-cita-general]]).
