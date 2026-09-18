---
tipo: contrato-rest
epica: "[[EP-001-autenticacion-y-sesion]]"
historias: ["[[HU-001-registrar-usuario]]", "[[HU-002-iniciar-sesion-y-sesion-jwt]]"]
---

# Contrato REST — Autenticación (`/api/v1/auth`)

Cumple el ítem de Definition of Done "contrato REST documentado" (RF-20) de [[HU-001-registrar-usuario]] y [[HU-002-iniciar-sesion-y-sesion-jwt]]. Verificado contra la implementación real en `com.fcv.citas.auth.infrastructure.web.AuthController` y probado end-to-end contra MySQL (ver `docs/wiki/llm-wiki/wiki/log.md`, entrada "VERIFICACIÓN E2E").

Todos los endpoints son públicos (`permitAll` en `SecurityConfig`): no requieren `Authorization`. Content-Type de request y response: `application/json`.

## Formato común de error

Toda respuesta de error (4xx) usa esta forma (`com.fcv.citas.shared.web.ApiError`):

```json
{
  "timestamp": "2026-09-18T19:51:41.126317Z",
  "status": 409,
  "error": "Conflict",
  "message": "El email 'ana9@example.com' ya está registrado",
  "details": []
}
```

`details` solo se llena en errores de validación (400), con un string por campo inválido: `"campo: mensaje"`.

---

## `POST /api/v1/auth/register`

Implementa HU-001. Crea una cuenta `USER`.

**Request body**

| Campo | Tipo | Reglas |
|---|---|---|
| `firstName` | string | requerido, no vacío |
| `lastName` | string | requerido, no vacío |
| `documentType` | string (enum) | requerido — uno de `CC`, `CE`, `TI`, `PASSPORT` |
| `documentNumber` | string | requerido, no vacío |
| `email` | string | requerido, formato email válido |
| `phone` | string | requerido, no vacío |
| `password` | string | requerido, mínimo 8 caracteres |

```json
{
  "firstName": "Ana",
  "lastName": "Perez",
  "documentType": "CC",
  "documentNumber": "1000000001",
  "email": "ana.perez@example.com",
  "phone": "3000000000",
  "password": "S3cret123!"
}
```

**Respuesta exitosa — `201 Created`**

```json
{
  "id": 1,
  "firstName": "Ana",
  "lastName": "Perez",
  "email": "ana.perez@example.com",
  "roles": ["USER"]
}
```

Nunca incluye la contraseña ni su hash (RF-01, CA-01/CA-04 de HU-001).

**Errores**

| Status | Causa | Ejemplo de `message` |
|---|---|---|
| `400 Bad Request` | Validación de campos (p. ej. password < 8 caracteres, email inválido, campo faltante) | "La solicitud contiene datos inválidos" (ver `details`) |
| `409 Conflict` | Email ya registrado | "El email '...' ya está registrado" |
| `409 Conflict` | Documento ya registrado | "El documento '...' ya está registrado" |

---

## `POST /api/v1/auth/login`

Implementa la parte de login de HU-002.

**Request body**

| Campo | Tipo | Reglas |
|---|---|---|
| `email` | string | requerido, formato email válido |
| `password` | string | requerido |

```json
{ "email": "ana.perez@example.com", "password": "S3cret123!" }
```

**Respuesta exitosa — `200 OK`**

```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiJ9...",
  "refreshToken": "ViJXtHPyyjojj_xmDm1FEtirUKlhwXJIJ9egIsDGdYo",
  "tokenType": "Bearer"
}
```

- `accessToken`: JWT firmado (HS256), incluye `sub` (id de usuario), `email` y `roles`; expira en `JWT_ACCESS_MINUTES` minutos (por defecto 15).
- `refreshToken`: valor opaco aleatorio (no es un JWT); expira en `JWT_REFRESH_DAYS` días (por defecto 7). Solo se persiste su hash SHA-256 en el servidor — el cliente debe guardar el valor recibido aquí, no se puede recuperar después.

**Errores**

| Status | Causa | Ejemplo de `message` |
|---|---|---|
| `400 Bad Request` | Validación de campos (email/password faltante o email con formato inválido) | "La solicitud contiene datos inválidos" |
| `401 Unauthorized` | Email inexistente, contraseña incorrecta o usuario inactivo | "Credenciales inválidas" (deliberadamente genérico; nunca distingue la causa, para evitar enumeración de usuarios) |

---

## `POST /api/v1/auth/refresh`

Implementa la parte de refresh de HU-002 (CA-03). Emite un nuevo access token sin exigir credenciales de nuevo.

**Request body**

| Campo | Tipo | Reglas |
|---|---|---|
| `refreshToken` | string | requerido |

```json
{ "refreshToken": "ViJXtHPyyjojj_xmDm1FEtirUKlhwXJIJ9egIsDGdYo" }
```

**Respuesta exitosa — `200 OK`**

```json
{ "accessToken": "eyJhbGciOiJIUzI1NiJ9...", "refreshToken": null, "tokenType": "Bearer" }
```

`refreshToken` siempre viene `null` en esta respuesta: el mismo refresh token sigue vigente y no se reemite (no hay rotación de refresh token en esta versión).

**Errores**

| Status | Causa | Ejemplo de `message` |
|---|---|---|
| `400 Bad Request` | `refreshToken` faltante o vacío | "La solicitud contiene datos inválidos" |
| `401 Unauthorized` | Token inexistente, expirado, revocado, o el usuario asociado ya no existe/está inactivo | "El refresh token es inválido, expiró o fue revocado" |

---

## `POST /api/v1/auth/logout`

Implementa la parte de logout de HU-002 (CA-04). Revoca el refresh token; idempotente (no falla si el token ya no existe o ya estaba revocado).

**Request body**

| Campo | Tipo | Reglas |
|---|---|---|
| `refreshToken` | string | requerido |

```json
{ "refreshToken": "ViJXtHPyyjojj_xmDm1FEtirUKlhwXJIJ9egIsDGdYo" }
```

**Respuesta exitosa — `204 No Content`** (sin body).

**Errores**

| Status | Causa |
|---|---|
| `400 Bad Request` | `refreshToken` faltante o vacío |

Un `refreshToken` que no existe o ya estaba revocado también devuelve `204` (no revela si el token era válido).

---

## Notas de seguridad relevantes al contrato

- CORS explícito: solo los orígenes en `FRONTEND_ORIGIN` (por defecto `http://localhost:5173,http://localhost:4200`) pueden invocar estos endpoints desde un navegador.
- El `accessToken` se usa en el resto de la API como `Authorization: Bearer <token>`; los endpoints de `/api/v1/auth/**` en sí no lo requieren.
- No hay límite de intentos de login en esta versión (fuera del alcance de HU-002); considerarlo como hardening futuro si se necesita.
