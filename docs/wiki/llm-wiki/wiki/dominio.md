---
tipo: wiki
---

# Dominio — Sistema de Agendamiento de Citas FCV

Fuente: `PRD.md`, `database/reference/README_DB.md`. Dominio académico/ficticio; ver `riesgos.md` para incógnitas.

## Actores

- **USER**: paciente ficticio, se autorregistra.
- **PROFESSIONAL**: creado por ADMIN; una o varias especialidades (una primaria); una o ambas sedes; no aprueba citas, gestiona su disponibilidad.
- **ADMIN**: gestiona profesionales, catálogos configurables, aprobaciones y reprogramaciones.

## Sedes fijas (catálogo, datos públicos)

1. Hospital Internacional de Colombia (HIC).
2. Fundación Cardiovascular de Colombia / Instituto Cardiovascular (ICV).

## Ciclo de vida de una cita

- **General** (`Medicina General`): nace `APPROVED` automáticamente si el horario sigue disponible al confirmar.
- **Especializada**: nace `REQUESTED`, retiene el horario, requiere decisión ADMIN → `APPROVED` o `REJECTED` (con motivo, libera slots).
- Desde `APPROVED` y futura: puede `CANCELLED` (libera slots, no reactivable) o solicitar reprogramación (`PENDING` sobre una nueva franja, sin tocar la franja original hasta la decisión ADMIN).
- Tras atendida: PROFESSIONAL marca `COMPLETED` o `NO_SHOW`.
- Todo cambio de estado se audita: cita, estado nuevo, actor, fuente (`SYSTEM`/`USER`/`ADMIN`), fecha/hora, motivo opcional (RF-19, append-only).

## Disponibilidad y slots

- PROFESSIONAL declara `availability_blocks` por día y sede (ej. 08:00–12:00 y 14:00–17:00 en HIC).
- Cada bloque se discretiza en slots atómicos de 30 minutos.
- Especialidad de 30 min = 1 slot; de 60 min = 2 slots consecutivos disponibles.
- Reglas duras: no bloques/citas en el pasado; no solapamiento de bloques del mismo profesional; profesional solo publica en sedes asignadas; especialidad debe estar activa y asociada al profesional.

## Catálogos

- **Fijos** (solo lectura, seed): roles, estados de cita, estados de reprogramación, regímenes, sedes.
- **Configurables** (CRUD por ADMIN, sin borrado físico si están referenciados — solo activar/desactivar): EPS, planes de EPS, especialidades.

## Afiliación

Cadena `insurance_regimes` → `eps` → `eps_plans` → `user_insurance_affiliations`. El usuario referencia un plan; EPS y régimen se derivan de ahí, nunca se duplican en el usuario.

## Ver también

- [[arquitectura]] para cómo se traduce esto en el stack técnico.
- [[riesgos]] para las incógnitas de dominio aún no resueltas (p. ej. si un bloque es agnóstico de especialidad, o si desactivar un profesional cancela sus citas futuras).
- Backlog Scrum completo con criterios de aceptación: `citas-api/docs/wiki/scrum/README.md`.
