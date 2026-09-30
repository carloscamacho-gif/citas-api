# LLM Wiki — Índice

Catálogo de la memoria global del proyecto. Léelo antes de cualquier operación QUERY o INGEST. Actualízalo cada vez que cambie la estructura de páginas.

## Páginas

- [[dominio]] — actores, entidades y reglas de negocio del sistema de citas.
- [[arquitectura]] — stack, repos, arquitectura hexagonal, decisiones técnicas.
- [[decisiones]] — decisiones de producto/proceso tomadas explícitamente por el estudiante.
- [[riesgos]] — incógnitas y contradicciones abiertas, no resueltas por inferencia.
- [[log]] — registro cronológico append-only de operaciones INGEST/QUERY/LEARN/LINT.

## Fuentes RAW ingeridas

| Fuente | Ubicación | Estado |
|---|---|---|
| PRD | `PRD.md` (raíz) | Ingerida 2026-09-18 |
| Restricciones técnicas | `RESTRICCIONES_TECNICAS.md` (raíz) | Ingerida 2026-09-18 |
| Modelo de datos de referencia | `database/reference/README_DB.md` | Ingerida 2026-09-18 |
| Backlog Scrum inicial | `citas-api/docs/wiki/scrum/` | Ingerida 2026-09-18 |

## Convenciones

- Esta wiki documenta conocimiento durable (hechos del dominio, decisiones, arquitectura), no conversaciones completas.
- Toda página distingue explícitamente evidencia (verificada contra PRD/repositorio) de inferencia (supuesto documentado).
- Nunca se persisten passwords, tokens, secretos ni PII real de FCV.
