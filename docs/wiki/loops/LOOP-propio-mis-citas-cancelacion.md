---
tipo: loop-builder-verifier
sprint: S4
loop: propio (elegido por el estudiante)
hu: ["[[HU-017-consultar-mis-citas]]", "[[HU-018-cancelar-cita]]"]
fecha: 2026-09-30
---

# LOOP (propio) — Mis citas + cancelación

Ciclo Builder/Verifier del vertical **HU-017 (mis citas) + HU-018 (cancelación)**. Es el loop "propio" de S4 (el guiado es la reprogramación, `LOOP_02`).

## Objetivo (goal)
El USER lista sus propias citas (filtrables por estado/fecha) y cancela una futura no terminal; cancelar libera los slots y registra auditoría (fuente USER). Solo el dueño puede ver/cancelar.

## Reglas / stop condition
- Presupuesto: máximo 3 iteraciones.
- PASS solo si: suite backend completa en verde, gate de frontend (lint+test+build) en verde, y verificación e2e contra MySQL de los criterios de aceptación (listar, filtrar, cancelar, liberar slot, ownership, autorización, auditoría).
- Escalar a humano si aparece migración o dependencia no prevista (no ocurrió).

## Iteraciones

### Iteración 1
- **BUILDER**: se escribió primero `CancelAppointmentServiceTest` (TDD) y un stub → Red (6/6 fallan). Se implementó `CancelAppointmentService` y `MyAppointmentsService`, se agregó el puerto `findByPatient` (+ query JPA + adapter), los endpoints `GET /appointments`, `POST /appointments/{id}/cancel` y las reglas de seguridad (USER).
- **VERIFIER** (revisión separada: diff + `./mvnw test`): **FAIL**. La suite quedó en 96 verde + **1 error**: `SecurityAuthorizationTest.onlyUsersCanListTheirAppointmentsAndCancel` lanzaba NPE porque el mock de `CancelAppointmentUseCase` devolvía `null` y el controlador lo mapeaba a DTO.

```json
{ "goal": "mis citas + cancelacion", "iteration": 1, "builder": "completed",
  "backendTests": "fail (96 pass, 1 error)", "frontendBuild": "n/a", "verifier": "FAIL",
  "cause": "SecurityAuthorizationTest: mock de cancel devuelve null -> NPE en AppointmentResponse.from",
  "result": "ITERATE" }
```

### Iteración 2
- **BUILDER**: usando solo ese feedback, se estubo el mock de `cancelAppointment.cancel(...)` en la prueba de autorización para devolver un `AppointmentDetails` válido (la prueba valida el status HTTP, no el cuerpo).
- **VERIFIER**: **PASS**.
  - Backend: **97/97** en verde.
  - Frontend: `npm run lint` limpio, `npm test` 10/10, `npm run build` OK.
  - E2E contra MySQL (base `citas_fcv_app`): listar mis citas y filtrar por `status`; agendar general→`APPROVED` y especializada→`REQUESTED`; cancelar→`CANCELLED` (200); re-cancelar terminal→400; **re-agendar en el mismo slot tras cancelar→201 (slot liberado, RN-09)**; cancelar cita ajena→403 (ownership); `GET /appointments` ADMIN/PRO→403 y sin token→401; historial `APPROVED (SYSTEM) → CANCELLED (USER)`.

```json
{ "goal": "mis citas + cancelacion", "iteration": 2, "builder": "completed",
  "backendTests": "pass (97)", "frontendBuild": "pass", "verifier": "PASS", "result": "COMPLETED" }
```

## Resultado
**COMPLETED en 2 iteraciones** (dentro del presupuesto). HU-017 y HU-018 verificadas por pruebas y e2e.
