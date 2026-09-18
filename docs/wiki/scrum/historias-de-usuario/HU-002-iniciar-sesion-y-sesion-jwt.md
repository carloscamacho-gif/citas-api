---
id: HU-002
tipo: historia-de-usuario
titulo: "Iniciar sesión y gestión de sesión JWT"
estado: Completada
epica: "[[EP-001-autenticacion-y-sesion]]"
esfuerzo: "Medio"
sprint_sugerido: "Sprint 1"
dependencias: ["[[HU-001-registrar-usuario]]"]
relacionadas: ["[[HU-003-recuperar-contrasena]]"]
---

# HU-002 — Iniciar sesión y gestión de sesión JWT

## Historia de usuario

**COMO** usuario registrado (USER, PROFESSIONAL o ADMIN)
**QUIERO** iniciar sesión con email y contraseña y mantener mi sesión mediante tokens
**PARA** acceder de forma segura a las funcionalidades según mi rol

> Como usuario registrado, quiero iniciar sesión con email y contraseña para acceder de forma segura a la aplicación.

## Contexto y descripción

El login es el mecanismo único de autenticación para los tres roles del sistema (USER, PROFESSIONAL, ADMIN); el rol determina la autorización posterior en cada endpoint protegido. Incluye emisión, refresh y revocación/logout de tokens.

## Alcance

- Login por email + contraseña.
- Emisión de access token de corta duración y refresh token.
- Endpoint de refresh que emite un nuevo access token a partir de un refresh token válido.
- Revocación/logout que invalida el refresh token.
- El rol del usuario forma parte del contexto de autorización del token/sesión.

## Fuera de alcance

- Autorización fina por endpoint de recursos de negocio (se declara en cada HU que expone esos recursos).
- Recuperación de contraseña (ver [[HU-003-recuperar-contrasena]]).

## Reglas de negocio

- RF-02: access token de corta duración + refresh token; permitir refresh y revocación/logout; roles como parte del contexto de autorización.
- Sección 8 del PRD: JWT access/refresh con secretos separados (`JWT_ACCESS_SECRET`, `JWT_REFRESH_SECRET`); nunca loguear passwords/tokens.

## Dependencias y relaciones

- Épica: [[EP-001-autenticacion-y-sesion]]
- Dependencias: [[HU-001-registrar-usuario]] (requiere una cuenta existente; también aplica a PROFESSIONAL/ADMIN creados por otras HU).
- Relacionadas: [[HU-003-recuperar-contrasena]].

## Esfuerzo

**Nivel:** Medio

**Justificación de dificultad:** coordina autenticación, dos tipos de token con ciclos de vida distintos, revocación y el contexto de autorización por rol; más superficie que un CRUD simple, aunque acotado a un solo agregado (sesión/credenciales).

## Tareas de desarrollo

- [x] **T-01 — Endpoint de login**
  Dificultad: Bajo
  Descripción: Validar credenciales contra el hash almacenado y, si son válidas, emitir access + refresh token con el rol embebido en el contexto de autorización.

- [x] **T-02 — Endpoint de refresh**
  Dificultad: Medio
  Descripción: Validar el refresh token vigente y no revocado, emitir un nuevo access token.

- [x] **T-03 — Logout / revocación**
  Dificultad: Medio
  Descripción: Invalidar el refresh token asociado a la sesión (p. ej. tabla `refresh_tokens` con hash, según `database/reference/README_DB.md`).

- [x] **T-04 — Configuración Spring Security**
  Dificultad: Medio
  Descripción: Filtro/validador de JWT que puebla el contexto de autorización con el rol del usuario para el resto de endpoints protegidos.

## Criterios de aceptación

### CA-01 — Login exitoso

**Dado** un usuario registrado con credenciales correctas
**Cuando** inicia sesión
**Entonces** recibe un access token y un refresh token válidos, y el rol del usuario queda disponible en el contexto de autorización

### CA-02 — Rechazo de credenciales inválidas

**Dado** un email inexistente o una contraseña incorrecta
**Cuando** se intenta iniciar sesión
**Entonces** el sistema rechaza el intento sin indicar cuál de los dos datos fue incorrecto

### CA-03 — Refresh de sesión

**Dado** un refresh token vigente y no revocado
**Cuando** se solicita un nuevo access token
**Entonces** el sistema emite un access token válido sin exigir credenciales nuevamente

### CA-04 — Logout revoca el acceso

**Dado** una sesión activa
**Cuando** el usuario cierra sesión
**Entonces** el refresh token queda revocado y no puede usarse para obtener nuevos access tokens

## Definition of Done

- [x] Todos los criterios de aceptación obligatorios están validados con evidencia.
- [x] Existe una migración Flyway coherente para el almacenamiento de refresh tokens (hash, no texto plano).
- [x] El contrato REST de login/refresh/logout está documentado (request/response, códigos de error) — RF-20.
- [x] No hay logging de contraseñas ni tokens en texto plano.
- [x] La trazabilidad de esta HU y su épica está actualizada en `docs/wiki/scrum/`.

## Evidencia de validación

| Elemento | Resultado | Evidencia | Observación |
|---|---|---|---|
| CA-01 | Cumple | `LoginServiceTest.issuesTokensOnValidCredentials`, `JwtTokenIssuerAdapterTest.issuedAccessTokenRoundTripsToOriginalClaims`; end-to-end `POST /auth/login` → `200` con tokens | — |
| CA-02 | Cumple | `LoginServiceTest.rejectsUnknownEmailWithGenericError`, `LoginServiceTest.rejectsWrongPasswordWithGenericError`; end-to-end contraseña incorrecta → `401` | Ambos casos lanzan la misma `InvalidCredentialsException`, sin distinguir la causa |
| CA-03 | Cumple | `RefreshAccessTokenServiceTest` (4 casos); end-to-end `POST /auth/refresh` → `200` con access token nuevo | — |
| CA-04 | Cumple | `LogoutServiceTest` (2 casos); end-to-end `POST /auth/logout` → `204` y refresh posterior con el mismo token → `401` | — |
| DoD-01 | Cumple | migración `V4__create_refresh_tokens_table.sql`; `token_hash` almacena SHA-256, nunca el valor crudo (`JwtTokenIssuerAdapter.hashRefreshToken`) | — |
| DoD-02 | Cumple | [[auth-api]] (`docs/wiki/contratos/auth-api.md`), secciones `POST /api/v1/auth/login`, `/refresh`, `/logout` | — |
| DoD-03 | Cumple | Revisión de código: ningún `log`/`System.out` imprime `password`, `passwordHash`, `accessToken` ni `refreshToken` crudos en `auth/` | Verificación estática, no hay prueba automatizada dedicada |

## Historial de validación

- 2026-09-18 — HU creada en estado `Borrador` durante la planificación inicial del backlog.
- 2026-09-18 — Aprobada para Sprint 1 (alcance de S2) por el estudiante.
- 2026-09-18 — Estado movido a `En desarrollo`: implementado login/refresh/logout en `citas-api` (bounded context `auth`); login cubierto por pruebas unitarias, refresh/logout implementados pero aún sin prueba dedicada ni verificación contra MySQL real.
- 2026-09-18 — Verificación end-to-end contra MySQL real (contenedor `fcv-citas-mysql`, base `citas_fcv_app`): login exitoso con tokens, rechazo de contraseña incorrecta (401), refresh exitoso, logout (204) y refresh posterior con el token ya revocado rechazado (401).
- 2026-09-18 — Agregadas pruebas unitarias dedicadas (`RefreshAccessTokenServiceTest`, `LogoutServiceTest`) y documentado el contrato REST en `docs/wiki/contratos/auth-api.md`. Todos los CA y toda la DoD están `Cumple` con evidencia: HU movida a `Completada`.

## Notas y decisiones

- Ninguna nota adicional.
