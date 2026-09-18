---
id: HU-006
tipo: historia-de-usuario
titulo: "Consultar catálogos fijos"
estado: Aprobada
epica: "[[EP-003-catalogos-del-sistema]]"
esfuerzo: "Bajo"
sprint_sugerido: "Sprint 1"
dependencias: []
relacionadas: ["[[HU-001-registrar-usuario]]", "[[HU-010-crear-profesional-con-especialidades-y-sedes]]"]
---

# HU-006 — Consultar catálogos fijos

## Historia de usuario

**COMO** cliente de la API (frontend o cualquier rol autenticado)
**QUIERO** consultar los catálogos fijos precargados
**PARA** poder construir formularios y validaciones sin hardcodear valores

> Como cliente de la API, quiero consultar los catálogos fijos para construir la interfaz sin duplicar valores hardcodeados.

## Contexto y descripción

Roles, estados de cita, estados de reprogramación, regímenes y sedes son catálogos de solo lectura precargados por seed. Son consumidos por prácticamente todas las demás épicas (registro, profesionales, agendamiento).

## Alcance

- Endpoints de solo lectura para: roles, estados de cita, estados de reprogramación, regímenes, sedes.
- Datos precargados por migración/seed Flyway (dos sedes fijas: HIC e ICV, según PRD sección 3).

## Fuera de alcance

- Cualquier operación de escritura sobre estos catálogos (son fijos por definición, RF-05).
- Catálogos configurables (EPS, planes, especialidades — ver [[HU-007-crud-de-eps]] y siguientes).

## Reglas de negocio

- RF-05: catálogos fijos precargados y de solo lectura.
- PRD sección 3: sedes fijas son Hospital Internacional de Colombia (HIC) e Instituto Cardiovascular (ICV).

## Dependencias y relaciones

- Épica: [[EP-003-catalogos-del-sistema]]
- Dependencias: ninguna (precargado por seed).
- Relacionadas: [[HU-001-registrar-usuario]] (rol USER), [[HU-010-crear-profesional-con-especialidades-y-sedes]] (sedes).

## Esfuerzo

**Nivel:** Bajo

**Justificación de dificultad:** endpoints de solo lectura sobre datos ya sembrados, sin lógica de negocio compleja.

## Tareas de desarrollo

- [ ] **T-01 — Migración Flyway de catálogos fijos**
  Dificultad: Bajo
  Descripción: Tablas y datos semilla de roles, estados de cita, estados de reprogramación, regímenes y sedes (HIC, ICV).

- [ ] **T-02 — Endpoints de solo lectura**
  Dificultad: Bajo
  Descripción: Un endpoint por catálogo (o uno agregado) que expone los valores precargados.

## Criterios de aceptación

### CA-01 — Sedes fijas disponibles

**Dado** el sistema con la migración de seed aplicada
**Cuando** se consultan las sedes
**Entonces** se listan exactamente Hospital Internacional de Colombia (HIC) e Instituto Cardiovascular (ICV)

### CA-02 — Catálogos de solo lectura

**Dado** cualquier cliente autenticado
**Cuando** intenta crear, modificar o eliminar un valor de un catálogo fijo
**Entonces** el sistema no expone una operación de escritura para ese recurso

## Definition of Done

- [ ] Todos los criterios de aceptación obligatorios están validados con evidencia.
- [ ] Existe una migración Flyway con los datos semilla de catálogos fijos.
- [ ] El contrato REST de consulta de catálogos está documentado — RF-20.
- [ ] La trazabilidad de esta HU y su épica está actualizada en `docs/wiki/scrum/`.

## Evidencia de validación

| Elemento | Resultado | Evidencia | Observación |
|---|---|---|---|
| CA-01 | Pendiente | — | — |
| CA-02 | Pendiente | — | — |
| DoD-01 | Pendiente | — | — |

## Historial de validación

- 2026-09-18 — HU creada en estado `Borrador` durante la planificación inicial del backlog.
- 2026-09-18 — Aprobada para Sprint 1 (alcance de S2) por el estudiante, como dependencia de base para registro y BD conectada.

## Notas y decisiones

- Ninguna nota adicional.
