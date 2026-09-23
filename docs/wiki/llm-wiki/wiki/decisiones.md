---
tipo: wiki
---

# Decisiones — Sistema de Agendamiento de Citas FCV

Decisiones explícitas tomadas por el estudiante o derivadas directamente de instrucciones aprobadas. No incluir aquí supuestos no confirmados (van en [[riesgos]]).

## 2026-09-18

- **Backlog Scrum**: se generaron 9 épicas y 24 HU cubriendo RF-01 a RF-19 del PRD. RF-20 (contrato REST) se trató como ítem transversal de Definition of Done en vez de HU propia, por ser un requisito puramente técnico sin valor observable independiente.
- **Alcance Sprint 1 (S2)**: aprobadas HU-001 (Registrar usuario), HU-002 (Iniciar sesión y sesión JWT) y HU-006 (Consultar catálogos fijos). El resto del backlog permanece en `Borrador` hasta nueva aprobación explícita.
- **AGENTS.md raíz y estructura LLM Wiki**: aprobados y creados tal como fueron propuestos por el agente orquestador (ver `AGENTS.md` en la raíz del workspace).
- **Workflows n8n (RF de la sección 10 del PRD)**: quedan fuera del backlog Scrum funcional; ya tienen especificación propia en `citas-api/automations/n8n/WF-00x-*.md` y corresponden a sesiones S5/S6.
- **Nombre de la base de datos de la aplicación**: `MYSQL_DATABASE`/`DB_NAME` pasan de `citas_fcv_training` a `citas_fcv_app` en `.env`, `.env.example` (raíz) y `citas-api/.env.example`. Motivo: `citas_fcv_training` ya tenía cargado el esquema de referencia (`database/reference/db.sql`) en el volumen Docker de este laboratorio, y Flyway no puede gestionar un esquema no vacío sin tabla de historial. `citas_fcv_app` es ahora la única base que las migraciones de `citas-api` deben tocar; `citas_fcv_training` se conserva sin modificar para quien quiera explorar el modelo de referencia/ERD por separado. Ver [[riesgos]] si en algún momento se quiere unificar ambas bases.

## 2026-09-23

- **Framework de frontend = React**: se importó a `citas-web` el frontend del prototipo aprobado (Stitch → Google AI Studio): **React 19 + TypeScript + Vite 8 + Tailwind CSS v4** (`lucide-react`, Vitest). Resuelve la incógnita abierta de framework (ver [[riesgos]]). Incluye pantallas de login, registro y dashboard, más clientes REST de auth y de agenda.
- **Reconciliación de contrato de auth (front adaptado al backend)**: el frontend importado venía construido contra un backend distinto (refresh por cookie `HttpOnly`, header `X-Requested-With`, errores con campo `detail`, registro con `role` singular + `phone`, `documentType=PA`). Se decidió **adaptar el frontend al contrato real de `citas-api`** (ya `Completado` y probado), no al revés. Cambios en `citas-web/src/auth/authApi.ts` y `RegisterScreen.tsx`: refresh token en el cuerpo (guardado en web storage), errores leídos de `message`, `roles` como arreglo, `documentType=PASSPORT`, y se quitó `X-Requested-With` (el CORS de `citas-api` no lo permite y bloqueaba el navegador). Verificado end-to-end en navegador: registro → auto-login → dashboard, logout y login. El contrato REST canónico sigue siendo `citas-api/docs/wiki/contratos/auth-api.md`.
- **Riesgo residual aceptado**: guardar el refresh token en `localStorage`/`sessionStorage` es más débil que una cookie `HttpOnly`. Aceptado para el laboratorio; documentado en [[riesgos]].

## Ver también

- [[riesgos]] para las decisiones aún pendientes de tomar.
- `citas-api/docs/wiki/scrum/README.md` para el detalle completo del backlog.
