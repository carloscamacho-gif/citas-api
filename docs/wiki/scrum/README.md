# Scrum — Sistema de Agendamiento de Citas FCV

## Objetivo del proyecto

Construir una aplicación web de agendamiento de citas (dominio académico/ficticio) que permita practicar desarrollo asistido por agentes, Spec-Driven Development, verificación automatizada y automatización n8n. Fuente de verdad: `PRD.md` y `RESTRICCIONES_TECNICAS.md` en la raíz del workspace.

## Stack detectado/seleccionado

- Backend: Java 21 + Spring Boot 3.5.x + Maven + arquitectura hexagonal + Spring Data JPA + Flyway + MySQL 8.4. **Aún sin inicializar** (repo `citas-api` vacío al momento de esta planificación).
- Frontend: TypeScript, React o Angular — decisión pendiente del estudiante vía Stitch/Google AI Studio. **Aún sin inicializar**.
- Comunicación: REST/JSON directo, sin BFF/Express (RF-20).

Supuesto documentado: como el repositorio aún no tiene código, el stack se toma de `RESTRICCIONES_TECNICAS.md` tal cual, sin inferencias adicionales.

## Épicas

- [[EP-001-autenticacion-y-sesion]] — Registro, login JWT y recuperación de contraseña.
- [[EP-002-perfil-y-afiliacion]] — Perfil de usuario y afiliación EPS/plan/régimen.
- [[EP-003-catalogos-del-sistema]] — Catálogos fijos y configurables (EPS, planes, especialidades).
- [[EP-004-gestion-de-profesionales]] — Alta, asignación y activación de profesionales.
- [[EP-005-disponibilidad-y-agenda-profesional]] — Bloques de disponibilidad y slots.
- [[EP-006-busqueda-y-agendamiento-de-citas]] — Disponibilidad, cita general y cita especializada.
- [[EP-007-gestion-de-citas-del-usuario]] — Mis citas, cancelación y reprogramación.
- [[EP-008-atencion-del-profesional]] — Agenda visible y cierre de atención.
- [[EP-009-administracion-y-auditoria]] — Bandeja administrativa y auditoría de estados.

## Propuesta de sprints (incrementos funcionales, sin duración ni capacidad)

> Sugerencia inicial alineada con `GUIA_SESIONES_S2_S6.md`. No implica fechas ni estimación de tiempo.

### Sprint 1 — Fundación de identidad (alcance de S2)
- [[HU-001-registrar-usuario]]
- [[HU-002-iniciar-sesion-y-sesion-jwt]]
- [[HU-006-consultar-catalogos-fijos]]

**Estado:** `Aprobada` para este sprint (ver detalle de aprobación en cada HU). El resto del backlog queda en `Borrador`/`Pendiente de aprobación` hasta que el estudiante decida avanzar de sprint.

### Sprint 2 — Agendamiento núcleo (alcance de S3)
- [[HU-005-gestionar-afiliacion-eps]]
- [[HU-007-crud-de-eps]]
- [[HU-008-crud-de-planes-de-eps]]
- [[HU-009-crud-de-especialidades]]
- [[HU-010-crear-profesional-con-especialidades-y-sedes]]
- [[HU-011-activar-desactivar-profesional]]
- [[HU-012-gestionar-bloques-de-disponibilidad]]
- [[HU-013-discretizar-bloques-en-slots]]
- [[HU-014-consultar-disponibilidad]]
- [[HU-015-agendar-cita-general]]
- [[HU-016-solicitar-cita-especializada]]
- [[HU-022-bandeja-de-citas-especializadas-pendientes]]
- [[HU-024-auditoria-de-cambios-de-estado]]

### Sprint 3 — Gestión de citas y agenda profesional (alcance de S3/S4)
- [[HU-004-consultar-y-actualizar-perfil]]
- [[HU-017-consultar-mis-citas]]
- [[HU-018-cancelar-cita]]
- [[HU-020-consultar-agenda-del-profesional]]

### Sprint 4 — Cierre del MVP (alcance de S4)
- [[HU-003-recuperar-contrasena]]
- [[HU-019-solicitar-reprogramacion]]
- [[HU-021-cerrar-atencion]]
- [[HU-023-bandeja-de-reprogramaciones-pendientes]]

## Decisiones e incógnitas pendientes

- RF-20 (contrato REST) no se modela como HU independiente: se exige como ítem de Definition of Done en cada HU que expone o consume endpoints, para evitar una historia puramente técnica sin valor observable propio.
- Los workflows n8n (RF de la sección 10 del PRD) no forman parte de este backlog funcional: ya tienen especificación propia en `citas-api/automations/n8n/WF-00x-*.md` y corresponden a S5/S6.
- La skill no asigna HU a EPS/planes/especialidades semilla concretos: el contenido de catálogos configurables se deja a la implementación del estudiante siguiendo `database/reference/README_DB.md`.
- Pendiente de decisión del estudiante: framework de frontend (React o Angular) — no bloquea la redacción de HU porque el comportamiento descrito es agnóstico de framework.
