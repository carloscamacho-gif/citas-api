---
tipo: wiki
---

# Riesgos e incógnitas — Sistema de Agendamiento de Citas FCV

Incógnitas reales identificadas durante la planificación, no resueltas por inferencia. Cada una debe resolverse explícitamente por el estudiante antes de implementar la HU asociada.

## Abiertas

| Incógnita | HU/épica afectada | Impacto si no se resuelve |
|---|---|---|
| ¿Desactivar un profesional cancela automáticamente sus citas futuras `APPROVED`? | [[HU-011-activar-desactivar-profesional]] (via `citas-api/docs/wiki/scrum/`) | Riesgo de citas huérfanas con un profesional inactivo |
| ¿Un bloque de disponibilidad es agnóstico de especialidad o se asocia a una especialidad concreta al crearlo? | HU-012, HU-013 | Afecta el diseño del modelo de slots y de la discretización 30/60 min |
| ¿Qué valor(es) exactos del catálogo de especialidades cuentan como "Medicina General"? | HU-009, HU-015 | Sin definirlo, no se puede implementar la aprobación automática de cita general |
| ¿Cuál es el umbral exacto de "cita pasada/aplicable" para permitir cerrar la atención? | HU-021 | Ambigüedad sobre cuándo PROFESSIONAL puede marcar `COMPLETED`/`NO_SHOW` |
| Framework de frontend (React o Angular) | Todo `citas-web` | No bloquea la redacción de HU (son agnósticas de framework), pero sí bloquea T-01 de cualquier HU de frontend hasta que el estudiante decida vía Stitch/AI Studio |
| Mecanismo concreto de exposición del token de recuperación en desarrollo (log vs. respuesta HTTP) | HU-003 | Debe decidirse y documentarse en el contrato REST antes de implementar |

## Resueltas

_Ninguna todavía — se moverán aquí desde la tabla anterior con fecha y referencia a la decisión en [[decisiones]] cuando el estudiante las resuelva._

## Ver también

- [[decisiones]] para el registro de resoluciones.
- [[dominio]] y [[arquitectura]] para el contexto de cada incógnita.
