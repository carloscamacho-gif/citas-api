---
id: EP-006
tipo: epica
titulo: "Búsqueda y agendamiento de citas"
estado: Borrador
historias: ["[[HU-014-consultar-disponibilidad]]", "[[HU-015-agendar-cita-general]]", "[[HU-016-solicitar-cita-especializada]]"]
dependencias: ["[[EP-005-disponibilidad-y-agenda-profesional]]"]
---

# EP-006 — Búsqueda y agendamiento de citas

## Objetivo

Permitir que USER busque disponibilidad filtrando por sede/tipo/especialidad/profesional/fecha, y agende una cita general (auto-aprobada) o solicite una especializada (pendiente de ADMIN).

## Valor esperado

Es el flujo central de valor del producto: convertir disponibilidad publicada en una cita reservada.

## Actores

- USER
- (indirectamente) ADMIN, que decide sobre las citas especializadas en [[EP-009-administracion-y-auditoria]].

## Alcance

- Filtro de disponibilidad por sede, tipo (general/especializada), especialidad, profesional y fecha, mostrando solo horarios que completen la duración requerida.
- Agendamiento de cita general con aprobación automática (`APPROVED`).
- Solicitud de cita especializada en estado `REQUESTED`, con retención del horario para evitar doble reserva.

## Fuera de alcance

- Aprobación/rechazo administrativo de la cita especializada (ver [[EP-009-administracion-y-auditoria]]).
- Cancelación/reprogramación posterior (ver [[EP-007-gestion-de-citas-del-usuario]]).

## Reglas de negocio

- RF-10: solo se muestran horarios que puedan completar toda la duración requerida.
- RF-11: cita general se aprueba automáticamente si el horario sigue disponible al confirmar; no requiere ADMIN.
- RF-12: cita especializada nace `REQUESTED`; el horario queda retenido para evitar doble reserva.
- RN-01: ninguna cita puede ocupar slots ya reservados/retenidos.
- RN-02: citas generales se aprueban automáticamente.
- RN-03: citas especializadas requieren ADMIN.

## Dependencias

- [[EP-005-disponibilidad-y-agenda-profesional]] (requiere slots publicados).
- [[EP-001-autenticacion-y-sesion]] (USER autenticado).
- [[EP-009-administracion-y-auditoria]] (toda creación de cita dispara auditoría vía [[HU-024-auditoria-de-cambios-de-estado]]).

## Historias de usuario

- [[HU-014-consultar-disponibilidad]]
- [[HU-015-agendar-cita-general]]
- [[HU-016-solicitar-cita-especializada]]

## Criterio de completitud de la épica

- [ ] Todas las HU obligatorias de esta épica están `Completada`.
- [ ] No quedan dependencias bloqueantes dentro del alcance de la épica.

## Riesgos e incógnitas

- El PRD no define qué especialidad(es) se consideran "Medicina General" más allá del nombre; se asume que es un valor del catálogo de especialidades marcado como general, a confirmar durante la implementación de HU-015.
