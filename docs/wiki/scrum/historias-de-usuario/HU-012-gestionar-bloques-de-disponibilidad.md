---
id: HU-012
tipo: historia-de-usuario
titulo: "Gestionar bloques de disponibilidad"
estado: Aprobada
epica: "[[EP-005-disponibilidad-y-agenda-profesional]]"
esfuerzo: "Alto"
sprint_sugerido: "Sprint 2"
dependencias: ["[[HU-010-crear-profesional-con-especialidades-y-sedes]]", "[[HU-011-activar-desactivar-profesional]]"]
relacionadas: ["[[HU-013-discretizar-bloques-en-slots]]"]
---

# HU-012 — Gestionar bloques de disponibilidad

## Historia de usuario

**COMO** PROFESSIONAL
**QUIERO** crear, editar y eliminar mis bloques de disponibilidad por día y sede
**PARA** publicar los horarios en los que puedo atender

> Como PROFESSIONAL, quiero gestionar mis bloques de disponibilidad para que los usuarios puedan agendar citas conmigo.

## Contexto y descripción

Ejemplo válido del PRD: 08:00–12:00 HIC y 14:00–17:00 HIC en un mismo día. Los bloques no pueden solaparse ni crearse en el pasado, y la sede del bloque debe ser una donde el profesional esté habilitado.

## Alcance

- Crear múltiples bloques por día, seleccionando sede por bloque.
- Editar/eliminar bloques futuros que no tengan citas comprometidas.
- Consultar el propio calendario de bloques.

## Fuera de alcance

- Discretización de cada bloque en slots atómicos (ver [[HU-013-discretizar-bloques-en-slots]], que consume el bloque creado aquí).
- Reserva/retención de slots por una cita (pertenece a [[EP-006-busqueda-y-agendamiento-de-citas]]).

## Reglas de negocio

- RF-08: no crear bloques en el pasado; no solapar bloques del mismo profesional; el profesional debe estar habilitado en la sede; editar/eliminar solo bloques futuros sin citas comprometidas.
- RN-06: no se permiten citas ni bloques en el pasado.
- RN-07: un profesional solo publica agenda en sedes asignadas.

## Dependencias y relaciones

- Épica: [[EP-005-disponibilidad-y-agenda-profesional]]
- Dependencias: [[HU-010-crear-profesional-con-especialidades-y-sedes]] (sedes asignadas), [[HU-011-activar-desactivar-profesional]] (profesional debe estar activo).
- Relacionadas: [[HU-013-discretizar-bloques-en-slots]].

## Esfuerzo

**Nivel:** Alto

**Justificación de dificultad:** requiere validar solapamiento contra todos los bloques existentes del profesional, restricción temporal (no pasado), restricción de sede habilitada, y proteger la edición/eliminación cuando ya hay citas comprometidas — varias reglas de negocio coordinadas sobre el mismo agregado.

## Tareas de desarrollo

- [ ] **T-01 — Endpoint de creación de bloque**
  Dificultad: Alto
  Descripción: Valida sede habilitada, ausencia de solapamiento y que el bloque no sea en el pasado antes de persistir.

- [ ] **T-02 — Endpoint de edición/eliminación de bloque futuro**
  Dificultad: Medio
  Descripción: Permite editar/eliminar solo si el bloque es futuro y no tiene citas comprometidas.

- [ ] **T-03 — Consulta de calendario propio**
  Dificultad: Bajo
  Descripción: Lista los bloques del profesional autenticado, filtrable por rango de fechas.

## Criterios de aceptación

### CA-01 — Creación de bloque válido

**Dado** un profesional activo habilitado en una sede, y un horario futuro sin solapamiento
**Cuando** crea un bloque en esa sede
**Entonces** el bloque se registra correctamente

### CA-02 — Rechazo de solapamiento

**Dado** un profesional con un bloque existente 08:00–12:00 en una fecha
**Cuando** intenta crear otro bloque que se solapa con ese horario en la misma fecha
**Entonces** el sistema rechaza la creación

### CA-03 — Rechazo de bloque en el pasado

**Dado** una fecha/hora ya transcurrida
**Cuando** un profesional intenta crear un bloque en ese horario
**Entonces** el sistema rechaza la creación

### CA-04 — Rechazo de sede no habilitada

**Dado** un profesional no habilitado en una sede
**Cuando** intenta crear un bloque en esa sede
**Entonces** el sistema rechaza la creación

### CA-05 — Protección de bloque con citas comprometidas

**Dado** un bloque futuro con al menos una cita comprometida sobre alguno de sus slots
**Cuando** el profesional intenta editarlo o eliminarlo
**Entonces** el sistema rechaza la operación

## Definition of Done

- [ ] Todos los criterios de aceptación obligatorios están validados con evidencia.
- [ ] Existe una migración Flyway coherente para `availability_blocks`.
- [ ] El contrato REST de gestión de bloques está documentado — RF-20.
- [ ] La trazabilidad de esta HU y su épica está actualizada en `docs/wiki/scrum/`.

## Evidencia de validación

| Elemento | Resultado | Evidencia | Observación |
|---|---|---|---|
| CA-01 | Pendiente | — | — |
| CA-02 | Pendiente | — | — |
| CA-03 | Pendiente | — | — |
| CA-04 | Pendiente | — | — |
| CA-05 | Pendiente | — | — |
| DoD-01 | Pendiente | — | — |

## Historial de validación

- 2026-09-18 — HU creada en estado `Borrador` durante la planificación inicial del backlog.

## Notas y decisiones

- Incógnita documentada en la épica: si un bloque es agnóstico de especialidad o si se asocia a una especialidad concreta al crearlo. Se recomienda resolverlo antes de implementar T-01, ya que afecta el diseño del modelo de slots en [[HU-013-discretizar-bloques-en-slots]].
