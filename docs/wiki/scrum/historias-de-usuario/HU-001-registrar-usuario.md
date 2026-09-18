---
id: HU-001
tipo: historia-de-usuario
titulo: "Registrar usuario"
estado: Completada
epica: "[[EP-001-autenticacion-y-sesion]]"
esfuerzo: "Bajo"
sprint_sugerido: "Sprint 1"
dependencias: []
relacionadas: ["[[HU-002-iniciar-sesion-y-sesion-jwt]]", "[[HU-006-consultar-catalogos-fijos]]"]
---

# HU-001 — Registrar usuario

## Historia de usuario

**COMO** visitante
**QUIERO** crear una cuenta de tipo USER con mis datos básicos
**PARA** poder acceder a la aplicación de agendamiento de citas

> Como visitante, quiero crear una cuenta USER para poder acceder a la aplicación de agendamiento de citas.

## Contexto y descripción

Cualquier visitante puede autorregistrarse. Es el punto de entrada de todo el sistema: sin esta HU no existe ningún otro flujo posible. No se autorregistran PROFESSIONAL ni ADMIN (RF-07: los crea ADMIN).

## Alcance

- Formulario/endpoint de registro con: nombres, apellidos, tipo/número de documento, email, teléfono, contraseña.
- Validación de unicidad de email y documento.
- Hash adaptativo de la contraseña (BCrypt/Argon2) antes de persistir.
- Asignación del rol `USER` al usuario creado.

## Fuera de alcance

- Login inmediato tras el registro (ver [[HU-002-iniciar-sesion-y-sesion-jwt]]).
- Verificación de email por correo (no exigida por el PRD).

## Reglas de negocio

- RF-01: datos mínimos nombres, apellidos, tipo/número de documento, email, teléfono, contraseña; email y documento únicos; contraseñas nunca en texto plano.
- Sección 8 del PRD: passwords con hash adaptativo compatible con Spring Security.

## Dependencias y relaciones

- Épica: [[EP-001-autenticacion-y-sesion]]
- Dependencias: ninguna.
- Relacionadas: [[HU-002-iniciar-sesion-y-sesion-jwt]], [[HU-006-consultar-catalogos-fijos]] (rol `USER` es un catálogo fijo precargado).

## Esfuerzo

**Nivel:** Bajo

**Justificación de dificultad:** una sola entidad, validaciones de unicidad estándar y hash de contraseña; sin dependencias externas ni coordinación entre capas complejas.

## Tareas de desarrollo

- [x] **T-01 — Endpoint de registro**
  Dificultad: Bajo
  Descripción: Controlador REST que recibe los datos mínimos y devuelve el resultado del registro (sin exponer la contraseña ni su hash).

- [x] **T-02 — Validación de unicidad y reglas de dominio**
  Dificultad: Bajo
  Descripción: Validar unicidad de email y documento a nivel de aplicación/BD antes de persistir; rechazar con error claro si se duplica.

- [x] **T-03 — Persistencia con hash de contraseña**
  Dificultad: Bajo
  Descripción: Repositorio/entidad de usuario con contraseña almacenada como hash adaptativo; migración Flyway de la tabla de usuarios y rol asignado por defecto.

## Criterios de aceptación

### CA-01 — Registro exitoso

**Dado** un visitante que envía nombres, apellidos, tipo/número de documento, email, teléfono y contraseña válidos y no usados antes
**Cuando** solicita el registro
**Entonces** se crea la cuenta con rol `USER` y la respuesta no expone la contraseña ni su hash

### CA-02 — Rechazo por email duplicado

**Dado** un email ya registrado
**Cuando** un visitante intenta registrarse con ese email
**Entonces** el sistema rechaza la solicitud sin crear una cuenta duplicada

### CA-03 — Rechazo por documento duplicado

**Dado** un tipo/número de documento ya registrado
**Cuando** un visitante intenta registrarse con ese documento
**Entonces** el sistema rechaza la solicitud sin crear una cuenta duplicada

### CA-04 — Contraseña nunca en texto plano

**Dado** un registro exitoso
**Cuando** se inspecciona el almacenamiento de la contraseña
**Entonces** el valor persistido es un hash adaptativo, no el texto plano original

## Definition of Done

- [x] Todos los criterios de aceptación obligatorios están validados con evidencia.
- [x] Existe una migración Flyway coherente para la tabla de usuarios/roles.
- [x] El contrato REST del endpoint de registro está documentado (request/response, códigos de error) — RF-20.
- [x] La trazabilidad de esta HU y su épica está actualizada en `docs/wiki/scrum/`.

## Evidencia de validación

| Elemento | Resultado | Evidencia | Observación |
|---|---|---|---|
| CA-01 | Cumple | `RegisterUserServiceTest.registersUserWithHashedPasswordAndUserRole`; end-to-end `POST /auth/register` → `201 Created` contra MySQL real | — |
| CA-02 | Cumple | `RegisterUserServiceTest.rejectsDuplicateEmail`; end-to-end `POST /auth/register` con email repetido → `409 Conflict` | — |
| CA-03 | Cumple | `RegisterUserServiceTest.rejectsDuplicateDocument`; end-to-end `POST /auth/register` con documento repetido → `409 Conflict` (`uq_users_document`, migración V2) | — |
| CA-04 | Cumple | `RegisterUserServiceTest.neverPersistsRawPassword`, `BCryptPasswordHasherAdapter` | — |
| DoD-01 | Cumple | migraciones `V1__create_roles_table.sql`, `V2__create_users_table.sql` | — |
| DoD-02 | Cumple | [[auth-api]] (`docs/wiki/contratos/auth-api.md`), sección `POST /api/v1/auth/register` | — |

## Historial de validación

- 2026-09-18 — HU creada en estado `Borrador` durante la planificación inicial del backlog.
- 2026-09-18 — Aprobada para Sprint 1 (alcance de S2) por el estudiante.
- 2026-09-18 — Estado movido a `En desarrollo`: implementado en `citas-api` (bounded context `auth`), verificado con `./mvnw test` (unitarias en verde). Pendiente prueba de integración contra MySQL real antes de poder marcarla `Completada`.
- 2026-09-18 — Verificación end-to-end contra MySQL real (contenedor `fcv-citas-mysql`, base `citas_fcv_app`): registro exitoso (201), rechazo de email duplicado (409) y de contraseña corta (400) confirmados con la app corriendo de verdad.
- 2026-09-18 — Verificado también el rechazo por documento duplicado end-to-end (409) y documentado el contrato REST en `docs/wiki/contratos/auth-api.md`. Todos los CA y toda la DoD están `Cumple` con evidencia: HU movida a `Completada`.

## Notas y decisiones

- Ninguna nota adicional.
