# LLM Wiki — Log

Registro cronológico append-only. No editar entradas pasadas; solo agregar nuevas al final.

## 2026-09-18

- **INGEST**: `PRD.md`, `RESTRICCIONES_TECNICAS.md`, `database/reference/README_DB.md`. Se integró conocimiento de dominio en [[dominio]] y de stack/arquitectura en [[arquitectura]].
- **INGEST**: backlog Scrum inicial generado por la skill `scrum-spec-orchestrator` (9 épicas, 24 HU) en `citas-api/docs/wiki/scrum/`. Se registraron las incógnitas abiertas de cada HU en [[riesgos]].
- **DECISIÓN**: aprobado el `AGENTS.md` raíz y la estructura inicial de esta LLM Wiki, propuestos por el agente orquestador. Ver [[decisiones]].
- **DECISIÓN**: aprobadas para Sprint 1 (alcance de sesión S2) las HU-001 (Registrar usuario), HU-002 (Iniciar sesión y sesión JWT) y HU-006 (Consultar catálogos fijos). Resto del backlog queda en `Borrador`.
- **BUILD**: inicializado `citas-api` (Spring Boot 3.5.4, Maven Wrapper, arquitectura hexagonal). Implementado el bounded context `auth` con HU-001 y HU-002 (registro, login, refresh, logout). Migraciones Flyway V1–V4 (roles, users, user_roles, refresh_tokens). Verificado con `./mvnw test` (11/11 pruebas unitarias en verde) y `./mvnw clean package` exitoso; **no verificado aún** contra MySQL real ni contrato REST end-to-end (pendiente `docker compose up -d mysql`). Generado `citas-api/AGENTS.md` real con evidencia del repositorio; se eliminó `AGENTS.md.template` por quedar superseded.
- **HALLAZGO**: al levantar `docker compose up -d mysql`, el volumen ya tenía cargado el esquema completo de `database/reference/db.sql` dentro de la base `citas_fcv_training` (de una sesión anterior a esta, no generado por el agente). Esa base es justo la que `MYSQL_DATABASE`/`DB_NAME` usaban por defecto en `.env`/`.env.example`, lo que hacía fallar Flyway ("non-empty schema but no schema history table") para cualquiera que siguiera el flujo documentado del README.
- **DECISIÓN**: `MYSQL_DATABASE` y `DB_NAME` se cambiaron a `citas_fcv_app` en `.env`, `.env.example` (raíz) y `citas-api/.env.example`. `citas_fcv_app` es la base que gestiona Flyway/`citas-api` de aquí en adelante; `citas_fcv_training` queda intacta como copia de referencia/ERD, sin que la aplicación la toque. Ver [[decisiones]].
- **VERIFICACIÓN E2E**: con MySQL real (`docker compose up -d mysql`) y la app corriendo (`./mvnw spring-boot:run` contra `citas_fcv_app`), se probaron por HTTP: registro exitoso (201), email duplicado (409), documento duplicado (409), contraseña corta (400), login exitoso con tokens (200), contraseña incorrecta (401), refresh exitoso (200), logout (204) y refresh con token ya revocado (401). Los 9 casos se comportaron según la HU.
- **CIERRE**: agregadas pruebas unitarias dedicadas `RefreshAccessTokenServiceTest` y `LogoutServiceTest` (17 pruebas en total en `citas-api`, todas en verde). Documentado el contrato REST completo de `/api/v1/auth/**` en `citas-api/docs/wiki/contratos/auth-api.md`. Con toda la evidencia reunida, HU-001 y HU-002 se movieron a `Completada` en `docs/wiki/scrum/`.

## 2026-09-23

- **BUILD (frontend)**: se importó el frontend del prototipo aprobado (Stitch → Google AI Studio) a `citas-web`: **React 19 + TypeScript + Vite 8 + Tailwind v4**, con pantallas de login/registro/dashboard y clientes REST de auth y agenda. Resuelve la incógnita de framework (→ React). Ver [[decisiones]] y [[arquitectura]].
- **HALLAZGO**: el frontend importado venía cableado a otro contrato de auth (cookie `HttpOnly`, `X-Requested-With`, error `detail`, registro `role`+`phone`, `documentType=PA`), incompatible con nuestro `citas-api`. Sin adaptación, el navegador bloqueaba login/registro (CORS por `X-Requested-With`) y la sesión no persistía.
- **DECISIÓN + FIX**: se adaptó el frontend al contrato real de `citas-api` (refresh en el cuerpo → web storage, error `message`, `roles` arreglo, `documentType=PASSPORT`, sin `X-Requested-With`). Ajustados `citas-web/src/auth/authApi.ts`, `RegisterScreen.tsx` y sus pruebas.
- **VERIFICACIÓN**: `npm run lint` (tsc) limpio, `npm test` 9/9 en verde, `npm run build` exitoso. End-to-end en navegador contra `citas-api` real: registro (nuevo paciente id 3) → auto-login → dashboard, logout, y login; usuario persistido con hash BCrypt y refresh token guardado solo como hash; sin errores de CORS. Generado `citas-web/AGENTS.md` real; eliminado su `AGENTS.md.template`.
- **RIESGO RESIDUAL**: refresh token en almacenamiento web (no cookie `HttpOnly`). Aceptado para el laboratorio; ver [[riesgos]].
