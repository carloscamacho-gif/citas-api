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
| Mecanismo concreto de exposición del token de recuperación en desarrollo (log vs. respuesta HTTP) | HU-003 | Debe decidirse y documentarse en el contrato REST antes de implementar |

## Riesgos residuales aceptados

| Riesgo | Alcance | Estado |
|---|---|---|
| El refresh token se guarda en `localStorage`/`sessionStorage` del navegador (expuesto a XSS), en vez de una cookie `HttpOnly` | `citas-web` (`auth/authApi.ts`) + contrato `/api/v1/auth` | Aceptado para el laboratorio: `citas-api` entrega el refresh en el cuerpo del login. Mitigar migrando el contrato a cookie `HttpOnly` si se endurece la seguridad. Ver [[decisiones]] 2026-09-23 |

## Resueltas

| Incógnita | Resolución | Fecha |
|---|---|---|
| Framework de frontend (React o Angular) | **React** (React 19 + TypeScript + Vite + Tailwind v4), al importar el frontend del prototipo Stitch/AI Studio. Ver [[decisiones]] | 2026-09-23 |

## Ver también

- [[decisiones]] para el registro de resoluciones.
- [[dominio]] y [[arquitectura]] para el contexto de cada incógnita.
