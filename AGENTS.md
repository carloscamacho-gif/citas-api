# AGENTS.md — citas-api

Generado siguiendo `../prompts/agents/PROMPT_AGENT_CITAS_API.md`, con evidencia real del repositorio (no instrucciones genéricas). Léelo antes de tocar código de este repo.

## Estado real del repositorio

- Java 21, Spring Boot 3.5.4, Maven (con Maven Wrapper: usa `./mvnw`, no asumas un `mvn` global).
- Persistencia: Spring Data JPA + MySQL (`com.mysql:mysql-connector-j`) + Flyway (`src/main/resources/db/migration/V1..V4`).
- Seguridad: Spring Security + JWT (`io.jsonwebtoken:jjwt`) con access/refresh separados; refresh token opaco, solo se persiste su hash SHA-256.
- Único bounded context implementado hasta ahora: `auth` (HU-001 Registrar usuario, HU-002 Iniciar sesión/sesión JWT). Todo lo demás del backlog (`docs/wiki/scrum/`) sigue sin implementar.
- Sin frontend acoplado: no hay ninguna dependencia ni referencia a `citas-web` en este repo.

## Estructura hexagonal (evidencia: `src/main/java/com/fcv/citas/auth/`)

```text
auth/
├── domain/
│   ├── model/       modelos puros (User, RefreshToken, RoleName...), sin anotaciones de Spring/JPA
│   ├── port/in/     casos de uso (RegisterUserUseCase, LoginUseCase, RefreshAccessTokenUseCase, LogoutUseCase)
│   ├── port/out/    dependencias hacia fuera (UserRepositoryPort, RefreshTokenRepositoryPort, PasswordHasherPort, TokenIssuerPort)
│   └── exception/   excepciones de negocio (EmailAlreadyUsedException, InvalidCredentialsException, ...)
├── application/     implementa los port/in (RegisterUserService, LoginService, RefreshAccessTokenService, LogoutService)
└── infrastructure/
    ├── persistence/ entidades JPA + Spring Data repositories + adapters que implementan los port/out de persistencia
    ├── security/    JwtTokenIssuerAdapter, BCryptPasswordHasherAdapter, JwtAuthenticationFilter, SecurityConfig
    └── web/         AuthController + DTOs (contrato REST bajo /api/v1/auth)
```

`com.fcv.citas.shared.web` contiene `ApiError` y `GlobalExceptionHandler`, compartidos entre bounded contexts.

Cuando agregues un nuevo bounded context (p. ej. `catalog`, `professional`, `scheduling`), replica esta misma separación domain/application/infrastructure. No pongas anotaciones de Spring o JPA dentro de `domain/`.

## Reglas arquitectónicas (verificadas contra el código actual, no aspiracionales)

- El dominio (`auth.domain`) no importa Spring, JPA ni Servlet — compílalo mentalmente sin esas dependencias antes de tocarlo.
- Los casos de uso viven en `application/` e implementan una interfaz de `domain/port/in`.
- `infrastructure/persistence` y `infrastructure/security` son adaptadores que implementan `domain/port/out`; nunca al revés.
- Los controladores (`infrastructure/web`) solo traducen HTTP ↔ casos de uso; no contienen reglas de negocio.
- Cambios de esquema requieren una nueva migración Flyway (`V{n}__descripcion.sql`) coherente con las existentes; nunca edites una migración ya aplicada.
- Secretos (`JWT_ACCESS_SECRET`, `JWT_REFRESH_SECRET`, credenciales DB) solo llegan por variables de entorno (`application.yml` los referencia con `${...}`); nunca hardcodear ni loguear.
- El refresh token nunca se persiste en claro (ver `JwtTokenIssuerAdapter.hashRefreshToken`); solo su hash SHA-256 vive en `refresh_tokens.token_hash`.
- `InvalidCredentialsException` es deliberadamente genérica (no distingue email inexistente de contraseña incorrecta) para evitar enumeración de usuarios — no lo "mejores" agregando detalle.
- No acoples este backend a decisiones de `citas-web` (React/Angular); el contrato es JSON puro sobre `/api/v1/**`.

## Cómo compilar y probar

```bash
./mvnw test              # pruebas unitarias (no requieren MySQL: usan Mockito sobre los puertos)
./mvnw clean package      # compila y empaqueta (requiere MySQL accesible solo para levantar la app, no para compilar/testear)
./mvnw spring-boot:run    # requiere MySQL corriendo (docker compose up -d mysql) y variables de entorno del .env
```

Pruebas actuales (`src/test/java/com/fcv/citas/auth/`): `RegisterUserServiceTest`, `LoginServiceTest`, `RefreshAccessTokenServiceTest`, `LogoutServiceTest`, `JwtTokenIssuerAdapterTest` (17 pruebas) — cubren todos los criterios de aceptación de HU-001 y HU-002, ambas ya `Completada` en `docs/wiki/scrum/`. Verificado además end-to-end contra MySQL real (registro, login, refresh, logout, y los rechazos 400/401/409). Aún falta: prueba de integración automatizada contra MySQL real (Flyway + repositorios) — hoy esa verificación fue manual con `curl`; y prueba de contrato a nivel de `AuthController` (`@WebMvcTest`/`MockMvc`) — hoy el contrato solo está probado end-to-end manualmente y documentado en `docs/wiki/contratos/auth-api.md`.

## Modo de trabajo para la próxima HU

1. Localiza la HU aprobada en `docs/wiki/scrum/historias-de-usuario/` y su Definition of Done.
2. Identifica qué bounded context le corresponde (¿existe ya o es nuevo?) y qué puertos/adaptadores necesita.
3. Propón el plan (qué archivos/paquetes tocarás) antes de escribir código, especialmente si cruza bounded contexts.
4. Implementa el mínimo coherente con la arquitectura hexagonal ya establecida.
5. Ejecuta `./mvnw test` y agrega pruebas para los criterios de aceptación de la HU.
6. Verifica contra la DoD de la HU y contra las reglas de esta página.
7. Actualiza la tabla de evidencia de la HU en `docs/wiki/scrum/` con lo verificado (y deja explícito lo que quede `No verificable`).

No mantengas una LLM Wiki propia en este repo: la wiki global vive en `docs/wiki/llm-wiki/` y la mantiene el agente orquestador desde la raíz del workspace.
