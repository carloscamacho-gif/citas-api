---
tipo: loop-builder-verifier
sprint: S4
loop: propio (HU de esfuerzo medio)
hu: ["[[HU-003-recuperar-contrasena]]"]
fecha: 2026-09-30
---

# LOOP — Recuperación de contraseña (HU-003)

Ciclo Builder/Verifier del vertical **HU-003**: solicitud de recuperación con token de un solo uso y cambio de contraseña, sin SMTP (token expuesto de forma controlada en laboratorio), RF-03.

## Objetivo (goal)
Un usuario registrado solicita recuperar su contraseña (sin revelar si el email existe), recibe un token temporal de un solo uso (expuesto en laboratorio), y lo usa para definir una nueva contraseña; el token se consume al usarse y los tokens usados/expirados se rechazan. La contraseña nueva nunca se registra en texto plano.

## Reglas / stop condition
- Presupuesto: 2 iteraciones.
- PASS solo si: suite backend en verde, gate frontend (lint+test+build) en verde, e2e contra MySQL de CA-01/02/03, más verificación de seguridad (token hasheado en BD, sin contraseña en logs).

## Iteraciones

### Iteración 1 — Backend + frontend
- **BUILDER**: migración `V11__create_password_reset_tokens.sql`; modelo `PasswordResetToken`; puertos in (`RequestPasswordResetUseCase`, `ResetPasswordUseCase`) y out (`PasswordResetTokenRepositoryPort`, `OneTimeTokenPort`); servicios `RequestPasswordResetService` (sin enumeración, invalida anteriores, expone token en laboratorio) y `ResetPasswordService` (valida vigencia/uso, actualiza contraseña, consume token); `Sha256OneTimeTokenAdapter` (valor aleatorio + hash SHA-256); `UserRepositoryPort.updatePassword` (+ setter en la entidad); endpoints en `AuthController` (`/auth/password-reset/request` y `/confirm`); propiedad `app.password-reset.expose-token`. Pruebas `RequestPasswordResetServiceTest` (3) y `ResetPasswordServiceTest` (5). Frontend: `requestPasswordReset`/`confirmPasswordReset` + flujo "¿Olvidaste tu contraseña?" en `LoginScreen`.
- **VERIFIER** (diff + `./mvnw test` + e2e API + e2e navegador + revisión de seguridad): **PASS**.
  - Backend: **143/143** en verde.
  - E2E API: solicitar → `devToken` expuesto; email inexistente → mismo mensaje con `devToken` null (sin enumeración); confirmar → 204; login con la clave nueva → 200; login con la clave vieja → 401; reusar el token consumido → 400; token basura → 400; contraseña corta → 400.
  - Seguridad: en el log de ejecución **0** apariciones de la contraseña; en BD el token se guarda como **hash SHA-256 (64 hex)**, nunca en crudo; el token usado queda marcado (`used_at`).
  - E2E navegador: desde la pantalla de login, "¿Olvidaste tu contraseña?" → solicitar → aviso de laboratorio con el token precargado → definir nueva contraseña → vuelta al login con banner de éxito → **login con la nueva contraseña exitoso**.

```json
{ "goal": "recuperar-contrasena", "iteration": 1, "builder": "completed",
  "backendTests": "pass (143)", "frontendGate": "pass",
  "e2e": "pass (CA-01/02/03)", "security": "pass (token hasheado, sin contraseña en logs)",
  "verifier": "PASS", "result": "COMPLETED" }
```

## Resultado
**COMPLETED en 1 iteración** (dentro del presupuesto). HU-003 verificada por pruebas unitarias, e2e de API, verificación de seguridad y e2e de navegador.
