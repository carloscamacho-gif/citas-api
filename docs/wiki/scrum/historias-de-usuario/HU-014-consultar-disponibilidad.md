---
id: HU-014
tipo: historia-de-usuario
titulo: "Consultar disponibilidad"
estado: Borrador
epica: "[[EP-006-busqueda-y-agendamiento-de-citas]]"
esfuerzo: "Medio"
sprint_sugerido: "Sprint 2"
dependencias: ["[[HU-013-discretizar-bloques-en-slots]]"]
relacionadas: ["[[HU-015-agendar-cita-general]]", "[[HU-016-solicitar-cita-especializada]]"]
---

# HU-014 — Consultar disponibilidad

## Historia de usuario

**COMO** USER
**QUIERO** filtrar la disponibilidad por sede, tipo de cita, especialidad, profesional y fecha
**PARA** encontrar un horario que pueda reservar

> Como USER, quiero consultar disponibilidad filtrada para encontrar un horario adecuado antes de agendar.

## Contexto y descripción

Solo deben mostrarse horarios que puedan completar toda la duración requerida por la especialidad (1 o 2 slots consecutivos).

## Alcance

- Endpoint de búsqueda con filtros combinables: sede, tipo general/especializada, especialidad, profesional, fecha.
- Cálculo de horarios candidatos que cubren completamente la duración de la especialidad solicitada.

## Fuera de alcance

- Creación de la cita en sí (ver [[HU-015-agendar-cita-general]] y [[HU-016-solicitar-cita-especializada]]).

## Reglas de negocio

- RF-10: filtros por sede, tipo general/especializada, especialidad, profesional, fecha; solo se muestran horarios que puedan completar toda la duración requerida.

## Dependencias y relaciones

- Épica: [[EP-006-busqueda-y-agendamiento-de-citas]]
- Dependencias: [[HU-013-discretizar-bloques-en-slots]] (requiere slots generados).
- Relacionadas: [[HU-015-agendar-cita-general]], [[HU-016-solicitar-cita-especializada]].

## Esfuerzo

**Nivel:** Medio

**Justificación de dificultad:** combina varios filtros y debe replicar la regla de consecutividad de slots (RN-05) solo para presentar resultados válidos, sin todavía reservar nada.

## Tareas de desarrollo

- [ ] **T-01 — Endpoint de búsqueda de disponibilidad**
  Dificultad: Medio
  Descripción: Combina los filtros solicitados y devuelve horarios candidatos que cubren completamente la duración de la especialidad.

- [ ] **T-02 — Exclusión de slots ocupados/retenidos y profesionales inactivos**
  Dificultad: Bajo
  Descripción: No mostrar horarios cuyos slots ya estén reservados/retenidos, ni de profesionales desactivados.

## Criterios de aceptación

### CA-01 — Filtro combinado devuelve horarios válidos

**Dado** filtros de sede, especialidad y fecha con disponibilidad real
**Cuando** un USER consulta disponibilidad
**Entonces** solo se muestran horarios que cubren completamente la duración de la especialidad filtrada

### CA-02 — No se muestran horarios incompletos

**Dado** un slot disponible seguido de uno ocupado para una especialidad de 60 minutos
**Cuando** se consulta disponibilidad para esa especialidad
**Entonces** ese horario no aparece como opción

### CA-03 — No se muestran profesionales inactivos

**Dado** un profesional desactivado con bloques publicados
**Cuando** se consulta disponibilidad
**Entonces** ese profesional no aparece en los resultados

## Definition of Done

- [ ] Todos los criterios de aceptación obligatorios están validados con evidencia.
- [ ] El contrato REST de búsqueda de disponibilidad está documentado (parámetros de filtro, formato de respuesta) — RF-20.
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
