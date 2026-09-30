---
id: HU-013
tipo: historia-de-usuario
titulo: "Discretizar bloques en slots según duración de especialidad"
estado: Aprobada
epica: "[[EP-005-disponibilidad-y-agenda-profesional]]"
esfuerzo: "Alto"
sprint_sugerido: "Sprint 2"
dependencias: ["[[HU-012-gestionar-bloques-de-disponibilidad]]", "[[HU-009-crud-de-especialidades]]"]
relacionadas: ["[[HU-014-consultar-disponibilidad]]", "[[HU-015-agendar-cita-general]]", "[[HU-016-solicitar-cita-especializada]]"]
---

# HU-013 — Discretizar bloques en slots según duración de especialidad

## Historia de usuario

**COMO** sistema (proceso interno disparado al crear un bloque)
**QUIERO** discretizar cada bloque de disponibilidad en slots atómicos de 30 minutos
**PARA** que las citas de 30 o 60 minutos puedan reservarse de forma consistente y sin doble reserva

> Como sistema, quiero discretizar los bloques en slots de 30 minutos para soportar citas de 30 y 60 minutos sin conflictos.

## Contexto y descripción

Es la pieza técnica que traduce un bloque (p. ej. 08:00–12:00) en unidades reservables. Una especialidad de 30 minutos ocupa 1 slot; una de 60 minutos requiere 2 slots consecutivos disponibles. Es la base de RN-01 y RN-05.

## Alcance

- Generación de slots atómicos de 30 minutos al crear un bloque de disponibilidad.
- Verificación de que una especialidad de 60 minutos solo pueda reservar dos slots consecutivos y ambos disponibles.
- Liberación de slots cuando la cita que los ocupaba se cancela/rechaza (consumido por otras HU, ver relacionadas).

## Fuera de alcance

- Creación/edición del bloque en sí (ver [[HU-012-gestionar-bloques-de-disponibilidad]]).
- Lógica de búsqueda de disponibilidad orientada a USER (ver [[HU-014-consultar-disponibilidad]]).

## Reglas de negocio

- RF-09: 30 min = 1 slot; 60 min = 2 slots consecutivos disponibles; el profesional no sobrescribe la duración de la especialidad.
- RN-01: ninguna cita puede ocupar slots ya reservados/retenidos.
- RN-05: slots deben ser consecutivos cuando la duración es 60 min.

## Dependencias y relaciones

- Épica: [[EP-005-disponibilidad-y-agenda-profesional]]
- Dependencias: [[HU-012-gestionar-bloques-de-disponibilidad]] (requiere un bloque creado), [[HU-009-crud-de-especialidades]] (duración de la especialidad).
- Relacionadas: [[HU-014-consultar-disponibilidad]], [[HU-015-agendar-cita-general]], [[HU-016-solicitar-cita-especializada]] (todas consumen slots generados aquí).

## Esfuerzo

**Nivel:** Alto

**Justificación de dificultad:** es la lógica de dominio más sensible del sistema: debe garantizar atomicidad/consistencia contra condiciones de carrera (doble reserva) y consecutividad para citas de 60 minutos; errores aquí afectan a toda la cadena de agendamiento.

## Tareas de desarrollo

- [ ] **T-01 — Generación de slots al crear un bloque**
  Dificultad: Alto
  Descripción: Al persistir un bloque, generar sus slots atómicos de 30 minutos con estado inicial disponible.

- [ ] **T-02 — Reserva atómica de 1 o 2 slots consecutivos**
  Dificultad: Alto
  Descripción: Mecanismo (transacción/bloqueo a nivel de BD) que reserva 1 slot (30 min) o 2 slots consecutivos disponibles (60 min) sin permitir doble reserva concurrente.

- [ ] **T-03 — Liberación de slots**
  Dificultad: Medio
  Descripción: Punto de extensión reutilizable para liberar slots cuando una cita se cancela/rechaza (usado por otras HU).

## Criterios de aceptación

### CA-01 — Generación correcta de slots de 30 minutos

**Dado** un bloque 08:00–12:00 recién creado
**Cuando** el sistema lo procesa
**Entonces** se generan slots atómicos de 30 minutos cubriendo exactamente ese rango

### CA-02 — Reserva de cita de 60 minutos usa 2 slots consecutivos

**Dado** una especialidad de 60 minutos y dos slots consecutivos disponibles
**Cuando** se reserva una cita de esa especialidad
**Entonces** ambos slots quedan marcados como ocupados/retenidos de forma atómica

### CA-03 — Prevención de doble reserva

**Dado** un slot ya reservado o retenido
**Cuando** dos solicitudes intentan reservarlo simultáneamente
**Entonces** solo una de las solicitudes tiene éxito y la otra es rechazada

### CA-04 — Rechazo de cita de 60 minutos sin dos slots consecutivos

**Dado** una especialidad de 60 minutos donde solo hay un slot disponible seguido de uno ocupado
**Cuando** se intenta reservar esa especialidad en ese horario
**Entonces** el sistema rechaza la reserva

## Definition of Done

- [ ] Todos los criterios de aceptación obligatorios están validados con evidencia.
- [ ] Existe una migración Flyway coherente para la tabla de slots atómicos.
- [ ] Existe al menos una prueba de concurrencia/doble reserva para CA-03 (requisito explícito de verificación en `GUIA_SESIONES_S2_S6.md`, sesión S3).
- [ ] La trazabilidad de esta HU y su épica está actualizada en `docs/wiki/scrum/`.

## Evidencia de validación

| Elemento | Resultado | Evidencia | Observación |
|---|---|---|---|
| CA-01 | Pendiente | — | — |
| CA-02 | Pendiente | — | — |
| CA-03 | Pendiente | — | — |
| CA-04 | Pendiente | — | — |
| DoD-01 | Pendiente | — | — |

## Historial de validación

- 2026-09-18 — HU creada en estado `Borrador` durante la planificación inicial del backlog.

## Notas y decisiones

- Esta HU concentra el riesgo técnico más alto del backlog (doble reserva concurrente); se recomienda escribirla con TDD explícito en S3 según la guía de sesiones.
