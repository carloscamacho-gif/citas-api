---
id: EP-009
tipo: epica
titulo: "Administración y auditoría"
estado: Borrador
historias: ["[[HU-022-bandeja-de-citas-especializadas-pendientes]]", "[[HU-023-bandeja-de-reprogramaciones-pendientes]]", "[[HU-024-auditoria-de-cambios-de-estado]]"]
dependencias: ["[[EP-006-busqueda-y-agendamiento-de-citas]]", "[[EP-007-gestion-de-citas-del-usuario]]"]
---

# EP-009 — Administración y auditoría

## Objetivo

Dar a ADMIN una bandeja única para decidir sobre citas especializadas `REQUESTED` y reprogramaciones `PENDING`, y garantizar que todo cambio de estado de cita quede auditado.

## Valor esperado

Es el punto de control humano del sistema (aprobaciones especializadas) y la garantía de trazabilidad exigida por el laboratorio para evaluar el progreso (RF-19).

## Actores

- ADMIN

## Alcance

- Bandeja de citas especializadas `REQUESTED` con filtros por sede, profesional, especialidad y fecha; aprobar (`APPROVED`) o rechazar (`REJECTED`, con motivo, liberando slots).
- Bandeja de reprogramaciones `PENDING`; aprobar (libera slots antiguos, asigna nuevos, actualiza la cita) o rechazar (libera la reserva provisional, mantiene la cita original), con motivo cuando corresponda.
- Registro de auditoría (`appointment_status_history`) para todo cambio de estado de cita: cita, estado nuevo, actor cuando existe, fuente `SYSTEM`/`USER`/`ADMIN`, fecha/hora, motivo opcional.

## Fuera de alcance

- Creación de las solicitudes que llegan a la bandeja (ver [[EP-006-busqueda-y-agendamiento-de-citas]] y [[EP-007-gestion-de-citas-del-usuario]]).
- CRUD de catálogos (ver [[EP-003-catalogos-del-sistema]]).

## Reglas de negocio

- RF-12, RF-15, RF-18: reglas de aprobación/rechazo de citas especializadas y reprogramaciones.
- RF-19: todo cambio de estado de cita guarda cita, estado nuevo, actor, fuente, fecha/hora y motivo opcional.
- RN-04: rechazo administrativo requiere motivo.
- RN-09: cancelar/rechazar libera reservas correspondientes.
- RN-10: la reprogramación no destruye la cita anterior hasta que sea aprobada.
- RN-12: datos de auditoría no se modifican como CRUD normal.

## Dependencias

- [[EP-006-busqueda-y-agendamiento-de-citas]] (origen de citas especializadas `REQUESTED`).
- [[EP-007-gestion-de-citas-del-usuario]] (origen de reprogramaciones `PENDING`).

## Historias de usuario

- [[HU-022-bandeja-de-citas-especializadas-pendientes]]
- [[HU-023-bandeja-de-reprogramaciones-pendientes]]
- [[HU-024-auditoria-de-cambios-de-estado]]

## Criterio de completitud de la épica

- [ ] Todas las HU obligatorias de esta épica están `Completada`.
- [ ] No quedan dependencias bloqueantes dentro del alcance de la épica.

## Riesgos e incógnitas

- RN-12 ("datos de auditoría no se modifican como CRUD normal") implica que `HU-024` debe implementarse como registro append-only sin exponer edición/borrado; se deja como restricción explícita de diseño, no como incógnita abierta.
