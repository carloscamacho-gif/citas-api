---
id: HU-011
tipo: historia-de-usuario
titulo: "Activar/desactivar profesional"
estado: Borrador
epica: "[[EP-004-gestion-de-profesionales]]"
esfuerzo: "Bajo"
sprint_sugerido: "Sprint 2"
dependencias: ["[[HU-010-crear-profesional-con-especialidades-y-sedes]]"]
relacionadas: []
---

# HU-011 — Activar/desactivar profesional

## Historia de usuario

**COMO** ADMIN
**QUIERO** activar o desactivar un profesional
**PARA** controlar si puede publicar disponibilidad y recibir nuevas citas

> Como ADMIN, quiero activar o desactivar profesionales para controlar quién puede operar en el sistema.

## Contexto y descripción

RF-07 exige explícitamente esta capacidad. Un profesional desactivado no debería poder publicar nuevos bloques ni ser mostrado en la búsqueda de disponibilidad.

## Alcance

- Cambiar el estado activo/inactivo de un profesional.
- Excluir profesionales inactivos de la búsqueda de disponibilidad ([[HU-014-consultar-disponibilidad]]).

## Fuera de alcance

- Qué ocurre con citas futuras ya aprobadas de un profesional que se desactiva (queda como incógnita a resolver, ver más abajo).

## Reglas de negocio

- RF-07: ADMIN puede activar/desactivar al profesional.

## Dependencias y relaciones

- Épica: [[EP-004-gestion-de-profesionales]]
- Dependencias: [[HU-010-crear-profesional-con-especialidades-y-sedes]] (el profesional debe existir).
- Relacionadas: ninguna adicional.

## Esfuerzo

**Nivel:** Bajo

**Justificación de dificultad:** cambio de estado simple sobre una entidad existente.

## Tareas de desarrollo

- [ ] **T-01 — Endpoint de activación/desactivación**
  Dificultad: Bajo
  Descripción: Cambia el flag de estado del profesional, restringido a ADMIN.

## Criterios de aceptación

### CA-01 — Desactivación de profesional

**Dado** un profesional activo
**Cuando** un ADMIN lo desactiva
**Entonces** el profesional deja de aparecer como opción en la búsqueda de disponibilidad

### CA-02 — Reactivación de profesional

**Dado** un profesional inactivo
**Cuando** un ADMIN lo reactiva
**Entonces** vuelve a estar disponible para nuevas búsquedas, respetando su disponibilidad publicada

## Definition of Done

- [ ] Todos los criterios de aceptación obligatorios están validados con evidencia.
- [ ] El contrato REST de activación/desactivación está documentado — RF-20.
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

- Incógnita no bloqueante: el PRD no dice si desactivar cancela citas futuras ya `APPROVED`. Se recomienda decidirlo explícitamente antes de implementar y documentar la decisión aquí.
