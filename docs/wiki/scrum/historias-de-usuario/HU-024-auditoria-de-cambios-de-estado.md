---
id: HU-024
tipo: historia-de-usuario
titulo: "Auditoría de cambios de estado"
estado: Borrador
epica: "[[EP-009-administracion-y-auditoria]]"
esfuerzo: "Medio"
sprint_sugerido: "Sprint 2"
dependencias: []
relacionadas: ["[[HU-015-agendar-cita-general]]", "[[HU-016-solicitar-cita-especializada]]", "[[HU-018-cancelar-cita]]", "[[HU-019-solicitar-reprogramacion]]", "[[HU-021-cerrar-atencion]]", "[[HU-022-bandeja-de-citas-especializadas-pendientes]]", "[[HU-023-bandeja-de-reprogramaciones-pendientes]]"]
---

# HU-024 — Auditoría de cambios de estado

## Historia de usuario

**COMO** sistema (mecanismo transversal invocado por cada transición de estado de cita)
**QUIERO** registrar cada cambio de estado de una cita con su contexto
**PARA** garantizar trazabilidad completa exigible por evaluación y por reglas de negocio

> Como sistema, quiero registrar cada cambio de estado de cita para sostener la auditoría exigida por el PRD.

## Contexto y descripción

Es un mecanismo transversal consumido por toda HU que cambie el estado de una cita (creación, aprobación, rechazo, cancelación, reprogramación, cierre). Se modela como HU propia porque tiene su propia entidad (`appointment_status_history`), reglas de integridad (append-only) y forma parte explícita del alcance funcional (RF-19), no solo como ítem de DoD.

## Alcance

- Registro por cada transición de estado de cita: cita, estado nuevo, actor cuando existe, fuente (`SYSTEM`, `USER` o `ADMIN`), fecha/hora, motivo opcional.
- Naturaleza append-only: los registros de auditoría no se editan ni eliminan como un CRUD normal.
- Consulta del historial de una cita (consumida, por ejemplo, por [[HU-017-consultar-mis-citas]] para mostrar el motivo de rechazo).

## Fuera de alcance

- Las HU que disparan cada transición (crear, aprobar, rechazar, cancelar, reprogramar, cerrar) implementan su propia lógica de negocio; esta HU solo cubre el registro y su modelo de datos.

## Reglas de negocio

- RF-19: todo cambio de estado de cita guarda cita, estado nuevo, actor cuando existe, fuente `SYSTEM`/`USER`/`ADMIN`, fecha/hora, motivo opcional.
- RN-11: transiciones de estado deben ser explícitas y verificables.
- RN-12: datos de auditoría no se modifican como CRUD normal.

## Dependencias y relaciones

- Épica: [[EP-009-administracion-y-auditoria]]
- Dependencias: ninguna (es infraestructura de dominio consumida por otras HU).
- Relacionadas: [[HU-015-agendar-cita-general]], [[HU-016-solicitar-cita-especializada]], [[HU-018-cancelar-cita]], [[HU-019-solicitar-reprogramacion]], [[HU-021-cerrar-atencion]], [[HU-022-bandeja-de-citas-especializadas-pendientes]], [[HU-023-bandeja-de-reprogramaciones-pendientes]].

## Esfuerzo

**Nivel:** Medio

**Justificación de dificultad:** el modelo de datos y el punto de integración son simples, pero debe integrarse correctamente en cada punto de transición de estado del sistema sin quedar nunca en un estado inconsistente (transición sin auditoría).

## Tareas de desarrollo

- [ ] **T-01 — Modelo de auditoría append-only**
  Dificultad: Bajo
  Descripción: Entidad/tabla `appointment_status_history` con cita, estado nuevo, actor, fuente, fecha/hora y motivo opcional; sin operaciones de edición/borrado expuestas.

- [ ] **T-02 — Punto de integración reutilizable**
  Dificultad: Medio
  Descripción: Componente de aplicación invocado por toda transición de estado de cita, para evitar duplicar la lógica de registro en cada HU consumidora.

- [ ] **T-03 — Endpoint de consulta de historial por cita**
  Dificultad: Bajo
  Descripción: Devuelve el historial de estados de una cita, respetando ownership (USER ve las suyas, PROFESSIONAL las suyas, ADMIN todas).

## Criterios de aceptación

### CA-01 — Registro de cada transición

**Dado** cualquier cambio de estado de una cita (creación, aprobación, rechazo, cancelación, reprogramación, cierre)
**Cuando** ocurre la transición
**Entonces** se crea un registro de auditoría con cita, estado nuevo, actor (cuando existe), fuente y fecha/hora

### CA-02 — Motivo opcional presente cuando aplica

**Dado** un rechazo administrativo (que exige motivo por RN-04)
**Cuando** se registra la auditoría de esa transición
**Entonces** el motivo queda almacenado en el registro correspondiente

### CA-03 — No editable ni borrable

**Dado** un registro de auditoría existente
**Cuando** se intenta modificarlo o eliminarlo mediante una operación CRUD estándar
**Entonces** el sistema no expone esa operación

## Definition of Done

- [ ] Todos los criterios de aceptación obligatorios están validados con evidencia.
- [ ] Existe una migración Flyway coherente para `appointment_status_history`.
- [ ] El contrato REST de consulta de historial está documentado — RF-20.
- [ ] La trazabilidad de esta HU y su épica está actualizada en `docs/wiki/scrum/`.

## Evidencia de validación

| Elemento | Resultado | Evidencia | Observación |
|---|---|---|---|
| CA-01 | Pendiente | — | — |
| CA-02 | Pendiente | — | — |
| CA-03 | Pendiente | — | — |
| DoD-01 | Pendiente | — | — |

## Historial de validación

- 2026-09-18 — HU creada en estado `Borrador` durante la planificación inicial del backlog.

## Notas y decisiones

- Se recomienda implementar T-02 desde el primer flujo que cambie estado (cita general, Sprint 2) para no tener que retrofitear auditoría en HU posteriores.
