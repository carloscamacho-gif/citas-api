---
tipo: loop-builder-verifier
sprint: S4
loop: propio (HU de esfuerzo bajo)
hu: ["[[HU-021-cerrar-atencion]]"]
fecha: 2026-09-30
---

# LOOP — Cerrar atención (HU-021)

Ciclo Builder/Verifier del vertical **HU-021**: el PROFESSIONAL marca una cita propia y ya iniciada como `COMPLETED` o `NO_SHOW`, dejando registro en el historial (RF-17, RN-11).

## Objetivo (goal)
Transición explícita `APPROVED → COMPLETED | NO_SHOW` sobre una cita del profesional autenticado cuyo inicio ya pasó; se audita con fuente `PROFESSIONAL`. Solo el profesional dueño puede cerrarla (RF-16).

## Reglas / stop condition
- Presupuesto: 2 iteraciones.
- PASS solo si: suite backend en verde, gate frontend (lint+test+build) en verde, e2e contra MySQL (CA-01, CA-02, CA-03 y guardas) y prueba de autorización por rol.

## Iteraciones

### Iteración 1 — Backend + frontend
- **BUILDER**: migración `V10` (amplía el `CHECK` de `change_source` para admitir `PROFESSIONAL`); `ChangeSource.PROFESSIONAL`; `Appointment.closedAs`; puerto `CloseAttentionUseCase` + enum `AttentionOutcome`; `CloseAttentionService` (resuelve profesional, valida ownership + estado `APPROVED` + inicio pasado, transiciona y audita); `ProfessionalAttentionController` (`POST /professional/appointments/{id}/close`). Pruebas `CloseAttentionServiceTest` (6) y authz en `SecurityAuthorizationTest`. Frontend: `professionalApi.close` y botones "Completada"/"No asistió" en la agenda para citas pasadas.
- **VERIFIER** (diff + `./mvnw test` + e2e API + e2e navegador): **PASS**.
  - Backend: **121/121** en verde.
  - E2E API: cita 5 (2026-09-25, pasada) → `COMPLETED`, historial `SYSTEM APPROVED → PROFESSIONAL COMPLETED`; re-cierre de una cita terminal → 400; cerrar una cita futura → 400; ADMIN → 403.
  - E2E navegador: en la agenda del profesional, solo la cita pasada muestra "Completada/No asistió"; al marcar "Completada" aparece el aviso y la cita sale de la agenda (que solo lista `APPROVED`).

```json
{ "goal": "cerrar-atencion", "iteration": 1, "builder": "completed",
  "backendTests": "pass (121)", "frontendGate": "pass",
  "e2e": "pass (CA-01/02/03 + guardas + authz)", "verifier": "PASS", "result": "COMPLETED" }
```

## Resultado
**COMPLETED en 1 iteración** (dentro del presupuesto). HU-021 verificada por pruebas unitarias, e2e de API, autorización por rol y e2e de navegador.
