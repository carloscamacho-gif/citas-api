---
tipo: loop-builder-verifier
sprint: S4
loop: guiado (LOOP_02 — avanzado)
hu: ["[[HU-019-solicitar-reprogramacion]]", "[[HU-023-bandeja-de-reprogramaciones-pendientes]]"]
fecha: 2026-09-30
---

# LOOP_02 (guiado) — Reprogramación completa

Ciclo Builder/Verifier del vertical **HU-019 (solicitar reprogramación) + HU-023 (bandeja ADMIN)**, según `prompts/goal-loop/LOOP_02_GUIADO_AVANZADO.md`.

## Objetivo (goal)
El USER solicita reprogramar una cita **APPROVED y futura** propia conservando profesional y especialidad; la nueva franja se **retiene** (sus slots se asignan a la misma cita) sin liberar la original, y la solicitud queda `PENDING`. ADMIN aprueba (libera la franja antigua, confirma la nueva, mueve la cita) o rechaza con motivo (libera solo la franja provisional; la cita original queda intacta). RN-10: la cita anterior no se destruye hasta aprobar.

## Reglas / stop condition
- Presupuesto: máximo 4 iteraciones.
- PASS solo si: suite backend completa en verde, gate frontend (lint+test+build) en verde, y verificación e2e contra MySQL de todos los criterios obligatorios (HU-019 CA-01/02/03, HU-023 CA-01/02/03) más autorización por rol y ownership.
- Escalar a humano si aparece dependencia o migración no prevista. (Se preveía la migración `V9`; no hubo escalamiento.)

## Iteraciones

### Iteración 1 — Backend (dominio, aplicación, persistencia, web)
- **BUILDER**: migración `V9__create_reschedule_requests.sql` (+ catálogo de estados PENDING/APPROVED/REJECTED); modelo `RescheduleRequest`/`RescheduleStatus`/`RescheduleDetails`; puertos in (`RequestRescheduleUseCase`, `RescheduleInboxUseCase`, `RescheduleDecisionUseCase`) y out (`RescheduleRepositoryPort`, `SchedulingRepositoryPort.releaseSlotsInRange`); servicios `RequestRescheduleService` y `RescheduleDecisionService`; adaptadores JPA; endpoints `POST /appointments/{id}/reschedule` (USER) y `GET|POST /admin/reschedules/**` (ADMIN); reglas de seguridad. Se escribieron primero las pruebas (`RequestRescheduleServiceTest` 6, `RescheduleDecisionServiceTest` 5) y se amplió `SecurityAuthorizationTest`.
- **VERIFIER** (diff + `./mvnw test` + e2e curl contra MySQL): **PASS**.
  - Backend: **110/110** en verde.
  - E2E (base `citas_fcv_app`): CA-01 la solicitud retiene ambas franjas sobre la misma cita (held=2) y queda PENDING; CA-02 reprogramar una cita `REQUESTED`/pasada → 400; CA-03 cambio de profesional → 400; bandeja ADMIN lista la solicitud; aprobar mueve la cita a la nueva franja, libera la antigua y confirma la nueva; rechazar sin motivo → 400; rechazar con motivo libera solo la franja provisional y conserva la cita original; historial `SYSTEM → USER(solicitada) → ADMIN(aprobada)`.

```json
{ "goal": "reprogramacion", "iteration": 1, "scope": "backend", "builder": "completed",
  "backendTests": "pass (110)", "e2e": "pass (HU-019 CA-01/02/03, HU-023 CA-01/02/03)", "verifier": "PASS" }
```

### Iteración 2 — Frontend (solicitud USER + bandeja ADMIN)
- **BUILDER**: `appointmentsApi.reschedule` y `reschedulesApi` (pending/decide); panel inline de reprogramación en el panel del paciente (elige día → slots disponibles del mismo profesional/especialidad/sede, excluyendo la franja actual → envía solicitud) y sección "Reprogramaciones pendientes" en ADMIN (aprobar / rechazar con motivo).
- **VERIFIER**: al integrar detectó que `AppointmentResponse` no exponía `professionalId`/`specialtyId`/`locationId`, necesarios para consultar disponibilidad al reprogramar → **corrección mínima**: se añadieron esos ids al DTO y al tipo `Appointment` del frontend. Re-verificación: **PASS**.
  - Frontend: `tsc --noEmit` limpio, `npm test` 10/10, `npm run build` OK.
  - E2E navegador contra MySQL: paciente abre "Reprogramar", elige 10:30, recibe el aviso "tu cita actual sigue vigente" (la cita sigue en 8:30 mientras PENDING); ADMIN ve la solicitud en la bandeja y la **aprueba**; en base: la cita pasa a 10:30, el slot 08:30 queda LIBRE, el slot 10:30 queda con la cita, `reschedule_requests.status=APPROVED` decidida por el ADMIN.

```json
{ "goal": "reprogramacion", "iteration": 2, "scope": "frontend", "builder": "completed",
  "cause_fixed": "AppointmentResponse sin ids -> se añaden professionalId/specialtyId/locationId",
  "frontendGate": "pass", "e2eBrowser": "pass", "verifier": "PASS", "result": "COMPLETED" }
```

## Resultado
**COMPLETED en 2 iteraciones** (dentro del presupuesto de 4). HU-019 y HU-023 verificadas por pruebas unitarias, e2e de API y e2e de navegador.
