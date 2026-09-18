---
id: HU-010
tipo: historia-de-usuario
titulo: "Crear profesional con especialidades y sedes"
estado: Borrador
epica: "[[EP-004-gestion-de-profesionales]]"
esfuerzo: "Medio"
sprint_sugerido: "Sprint 2"
dependencias: ["[[HU-001-registrar-usuario]]", "[[HU-006-consultar-catalogos-fijos]]", "[[HU-009-crud-de-especialidades]]"]
relacionadas: ["[[HU-011-activar-desactivar-profesional]]", "[[HU-012-gestionar-bloques-de-disponibilidad]]"]
---

# HU-010 — Crear profesional con especialidades y sedes

## Historia de usuario

**COMO** ADMIN
**QUIERO** crear un profesional ficticio con su código, matrícula, especialidades y sedes
**PARA** que pueda publicar disponibilidad y recibir citas

> Como ADMIN, quiero crear profesionales configurados para habilitar el agendamiento de citas.

## Contexto y descripción

El profesional es primero un usuario con rol PROFESSIONAL; `professionals` agrega los datos propios (código, matrícula ficticia). Las relaciones con especialidades y sedes son N:M.

## Alcance

- Crear el usuario base con rol PROFESSIONAL.
- Registrar código profesional y matrícula ficticia.
- Asignar una o varias especialidades activas, marcando una como primaria.
- Asignar una o ambas sedes fijas (HIC, ICV).

## Fuera de alcance

- Activación/desactivación posterior del profesional (ver [[HU-011-activar-desactivar-profesional]]).
- Gestión de bloques de disponibilidad (ver [[HU-012-gestionar-bloques-de-disponibilidad]]).

## Reglas de negocio

- RF-07: ADMIN crea el usuario PROFESSIONAL, registra código/matrícula, asigna una o varias especialidades marcando una primaria, y asigna una o ambas sedes.
- RN-08: una especialidad debe estar activa y asociada al profesional para reservarse.

## Dependencias y relaciones

- Épica: [[EP-004-gestion-de-profesionales]]
- Dependencias: [[HU-001-registrar-usuario]] (mecanismo de creación de usuario, aunque aquí lo ejecuta ADMIN, no autorregistro), [[HU-006-consultar-catalogos-fijos]] (sedes), [[HU-009-crud-de-especialidades]] (especialidades activas).
- Relacionadas: [[HU-011-activar-desactivar-profesional]], [[HU-012-gestionar-bloques-de-disponibilidad]].

## Esfuerzo

**Nivel:** Medio

**Justificación de dificultad:** crea un usuario y coordina dos relaciones N:M (especialidades con marca de primaria, y sedes) en una sola operación.

## Tareas de desarrollo

- [ ] **T-01 — Endpoint de creación de profesional**
  Dificultad: Medio
  Descripción: Crea el usuario base con rol PROFESSIONAL, sus datos propios (código, matrícula) y las asignaciones de especialidades/sedes en una operación coherente.

- [ ] **T-02 — Validación de especialidad primaria**
  Dificultad: Bajo
  Descripción: Exactamente una especialidad asignada debe marcarse como primaria.

- [ ] **T-03 — Validación de especialidades/sedes activas**
  Dificultad: Bajo
  Descripción: Rechazar asignación de especialidades inactivas o sedes inexistentes.

## Criterios de aceptación

### CA-01 — Alta de profesional válida

**Dado** un ADMIN autenticado, datos de usuario válidos, al menos una especialidad activa marcada como primaria y al menos una sede
**Cuando** crea el profesional
**Entonces** se crea el usuario con rol PROFESSIONAL junto con sus asignaciones de especialidad(es) y sede(s)

### CA-02 — Rechazo sin especialidad primaria

**Dado** una solicitud de alta sin ninguna especialidad marcada como primaria, o con más de una
**Cuando** se envía la solicitud
**Entonces** el sistema la rechaza

### CA-03 — Rechazo de especialidad inactiva

**Dado** una especialidad desactivada
**Cuando** un ADMIN intenta asignarla a un nuevo profesional
**Entonces** el sistema rechaza la asignación

## Definition of Done

- [ ] Todos los criterios de aceptación obligatorios están validados con evidencia.
- [ ] Existe una migración Flyway coherente para `professionals` y las tablas puente de especialidades/sedes.
- [ ] El contrato REST de alta de profesional está documentado — RF-20.
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

- Ninguna nota adicional.
