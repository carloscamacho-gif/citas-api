---
tipo: wiki
---

# Arquitectura — Sistema de Agendamiento de Citas FCV

Fuente: `RESTRICCIONES_TECNICAS.md`, `README.md`, `docker-compose.yml`, y el código real de `citas-api` (actualizado 2026-09-18).

## Repos

- `citas-api` (Git independiente): Java 21 + Spring Boot 3.5.4 + Maven (con Maven Wrapper) + arquitectura hexagonal + Spring Data JPA + Flyway + MySQL 8.4 + Spring Security con JWT access/refresh. **Ya inicializado**: bounded context `auth` implementa HU-001 (registro) y HU-002 (login/sesión JWT). Ver `citas-api/AGENTS.md` para el detalle verificado de la estructura y las reglas arquitectónicas.
- `citas-web` (Git independiente): **React 19 + TypeScript + Vite 8 + Tailwind CSS v4** (`lucide-react`, Vitest), Node.js 24. Sin Express/BFF; REST directo contra `citas-api`. **Inicializado** al importar el frontend del prototipo Stitch/AI Studio; login y registro funcionan end-to-end contra `citas-api`. Ver `citas-web/AGENTS.md`. El contrato de auth se reconció al de `citas-api` (ver [[decisiones]] 2026-09-23).
- La raíz `citas/` no es un repositorio Git; solo orquesta.

## Infraestructura local (docker-compose.yml)

- `mysql` (MySQL 8.4) en host `3307` → contenedor `3306`.
- `citas-api-dev` (Maven/Java 21) monta `./citas-api`, expone `8080`; no contiene la app, solo el toolchain.
- `citas-web-dev` (Node 24) monta `./citas-web`, expone `5173` (React/Vite) y `4200` (Angular); tampoco contiene la app.

## Seguridad mínima obligatoria

- Passwords con hash adaptativo (BCrypt/Argon2 compatible con Spring Security).
- Secretos solo por `.env`/variables de entorno (`JWT_ACCESS_SECRET`, `JWT_REFRESH_SECRET` con secretos separados).
- Autorización por rol y ownership; CORS explícito; validación server-side.
- Nunca loguear passwords/tokens.

## Documentación versionada

- `citas-api/docs/wiki/scrum/`: backlog Scrum, escrito únicamente por la skill `scrum-spec-orchestrator`.
- `citas-api/docs/wiki/llm-wiki/`: esta wiki (RAW/WIKI/SCHEMA).
- `citas-api/docs/wiki/contratos/`: contratos REST enlazados desde las HU y desde esta wiki (p. ej. [[auth-api]] para `/api/v1/auth/**`).
- `citas-api/automations/n8n/`: JSON de workflows n8n versionados (S5/S6); ya existen specs Markdown de WF-001 a WF-003.

## Ver también

- [[dominio]] para el modelo funcional que esta arquitectura debe soportar.
- [[decisiones]] para el estado de inicialización de cada repo.
