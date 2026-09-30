---
tipo: loop-builder-verifier
sprint: S4
loop: propio (HUs de esfuerzo bajo)
hu: ["[[HU-007-crud-de-eps]]", "[[HU-008-crud-de-planes-de-eps]]"]
fecha: 2026-09-30
---

# LOOP — CRUD de EPS y planes (HU-007/008)

Ciclo Builder/Verifier del vertical **HU-007 (CRUD de EPS) + HU-008 (CRUD de planes)**: administración ADMIN del catálogo de aseguramiento con desactivación en vez de borrado para catálogos referenciados (RF-06).

## Objetivo (goal)
ADMIN crea/lista/actualiza/activa-desactiva EPS y planes. Un plan requiere EPS y régimen existentes (CA-03). No se borra físicamente una EPS con planes ni un plan con afiliaciones (CA-02); se desactiva. Solo ADMIN administra el catálogo (CA-03 de HU-007).

## Reglas / stop condition
- Presupuesto: 2 iteraciones.
- PASS solo si: suite backend en verde, gate frontend (lint+test+build) en verde, e2e contra MySQL de todos los criterios obligatorios + autorización por rol.

## Iteraciones

### Iteración 1 — Backend + frontend
- **BUILDER**: modelos `Eps`, `EpsPlan`, `InsuranceRegime`; puertos in (`AdminEpsUseCase`, `AdminEpsPlanUseCase`, `ListInsuranceRegimesUseCase` + comandos) y out (`EpsRepositoryPort`, `EpsPlanRepositoryPort`, `InsuranceRegimeRepositoryPort`); servicios `AdminEpsService`, `AdminEpsPlanService`, `ListInsuranceRegimesService`; adaptadores JDBC (`EpsJdbcAdapter`, `EpsPlanJdbcAdapter`, `InsuranceRegimeJdbcAdapter`) sobre las tablas de `V7`; controladores `AdminEpsController` y `AdminEpsPlanController`; excepciones y mapeos HTTP (404/409/400). Pruebas `AdminEpsServiceTest` (6), `AdminEpsPlanServiceTest` (7) y authz en `SecurityAuthorizationTest`. Frontend: `epsApi`/`epsPlansApi` + sección "EPS y planes" en `AdminHome`.
- **VERIFIER** (diff + `./mvnw test` + e2e API + e2e navegador): **PASS** (con un ajuste menor de UX, ver iteración 2).
  - Backend: **135/135** en verde.
  - E2E API: crear EPS → 201; crear plan (EPS+régimen resueltos) → 201; plan con EPS inexistente → 404; borrar EPS con plan → 409; desactivar EPS → ok; borrar plan sin afiliaciones → 204; borrar EPS sin planes → 204; borrar plan semilla con 1 afiliación → 409; USER GET /admin/eps → 403.

### Iteración 2 — Ajuste de UX (mensaje de error)
- **VERIFIER**: en el navegador, el borrado protegido (409) mostraba el mensaje genérico de agenda ("El horario dejó de estar disponible") porque la sección reutilizaba `schedulingErrorMessage`.
- **BUILDER**: se añadió `catalogErrorMessage` (mensajes propios del catálogo: código en uso / referenciado / desactivar) y se usó en la sección de EPS y planes.
- **VERIFIER**: **PASS**. Frontend gate en verde; en el navegador se creó una EPS, apareció en la lista y en el selector de planes, y su borrado sin referencias devolvió 204 (desapareció); el borrado de la EPS semilla (con plan) quedó bloqueado con el mensaje correcto.

```json
{ "goal": "crud-eps-planes", "iteration": 2, "builder": "completed",
  "backendTests": "pass (135)", "frontendGate": "pass",
  "e2e": "pass (HU-007 CA-01/02/03, HU-008 CA-01/02/03, authz)", "verifier": "PASS", "result": "COMPLETED" }
```

## Resultado
**COMPLETED en 2 iteraciones** (dentro del presupuesto). HU-007 y HU-008 verificadas por pruebas unitarias, e2e de API, autorización por rol y e2e de navegador. No requirió migración nueva (tablas ya presentes en `V7`).
