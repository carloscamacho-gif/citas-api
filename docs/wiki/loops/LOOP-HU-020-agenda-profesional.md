---
tipo: loop-builder-verifier
sprint: S4
loop: propio (HU de esfuerzo bajo)
hu: ["[[HU-020-consultar-agenda-del-profesional]]"]
fecha: 2026-09-30
---

# LOOP — Agenda del profesional (HU-020)

Ciclo Builder/Verifier del vertical **HU-020**: el PROFESSIONAL consulta sus citas `APPROVED` por día/semana y sede, sin ver datos de otros profesionales (RF-16).

## Objetivo (goal)
Endpoint de solo lectura acotado al profesional autenticado: resuelve `user → professional` y lista sus citas `APPROVED` con filtros de rango de fecha y sede. Ningún rol distinto de PROFESSIONAL accede; un profesional nunca ve la agenda de otro.

## Reglas / stop condition
- Presupuesto: 2 iteraciones.
- PASS solo si: suite backend en verde, gate frontend (lint+test+build) en verde, e2e contra MySQL (CA-01, CA-02) y prueba de autorización cruzada (DoD S3).

## Iteraciones

### Iteración 1 — Backend + frontend
- **BUILDER**: puerto `ProfessionalAgendaUseCase`; `AppointmentRepositoryPort.findByProfessional` (+ query JPA `agenda` y adapter); `ProfessionalAgendaService` (resuelve `professionals.findByUserId`); `ProfessionalAgendaController` (`GET /professional/agenda`). Pruebas `ProfessionalAgendaServiceTest` (3) y authz en `SecurityAuthorizationTest`. Frontend: `professionalApi.agenda`; sección "Mi agenda" en `ProfessionalHome` con filtro por día y sede.
- **VERIFIER** (diff + `./mvnw test` + e2e API + e2e navegador): **PASS**.
  - Backend: **114/114** en verde.
  - E2E API (login del profesional, hash conocido en laboratorio): agenda devuelve 8 citas, todas `APPROVED`; filtro por rango `2026-10-03` → 3; sede inexistente → 0; ADMIN → 403; sin token → 401.
  - E2E navegador: el profesional ve "Mi agenda" con sus citas aprobadas; al filtrar por el día 2026-10-03 quedan exactamente las 3 citas de ese día.

```json
{ "goal": "agenda-profesional", "iteration": 1, "builder": "completed",
  "backendTests": "pass (114)", "frontendGate": "pass",
  "e2e": "pass (CA-01 scoped APPROVED, CA-02 filtros, authz 403/401)", "verifier": "PASS", "result": "COMPLETED" }
```

## Resultado
**COMPLETED en 1 iteración** (dentro del presupuesto). HU-020 verificada por pruebas unitarias, e2e de API, autorización cruzada y e2e de navegador.
