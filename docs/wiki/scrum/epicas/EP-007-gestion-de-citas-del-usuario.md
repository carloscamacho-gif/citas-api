---
id: EP-007
tipo: epica
titulo: "Gestión de citas del usuario"
estado: Borrador
historias: ["[[HU-017-consultar-mis-citas]]", "[[HU-018-cancelar-cita]]", "[[HU-019-solicitar-reprogramacion]]"]
dependencias: ["[[EP-006-busqueda-y-agendamiento-de-citas]]"]
---

# EP-007 — Gestión de citas del usuario

## Objetivo

Permitir que USER consulte sus citas, las cancele si son futuras y no terminales, y solicite reprogramación de una cita aprobada y futura.

## Valor esperado

Cierra el ciclo de vida de la cita desde la perspectiva del paciente ficticio, evitando que quede atado a una reserva que ya no le sirve.

## Actores

- USER
- (indirectamente) ADMIN, que decide sobre la reprogramación en [[EP-009-administracion-y-auditoria]].

## Alcance

- Listado y filtro de "mis citas" por estado/fecha, con sede, profesional, especialidad, fecha/hora, duración, estado y motivo de rechazo cuando exista.
- Cancelación de cita futura no terminal, liberando slots.
- Solicitud de reprogramación de cita aprobada y futura, conservando profesional y especialidad.

## Fuera de alcance

- Cambiar de profesional en una reprogramación (se trata como nueva cita, ver [[EP-006-busqueda-y-agendamiento-de-citas]]).
- Decisión ADMIN sobre la reprogramación (ver [[EP-009-administracion-y-auditoria]]).

## Reglas de negocio

- RF-13: mínimo de datos visibles en "mis citas".
- RF-14: `CANCELLED` libera slots; una cita cancelada no se reactiva directamente; debe registrarse historial.
- RF-15: solo una cita aprobada y futura puede solicitar reprogramación; la nueva franja se retiene mientras está `PENDING`; la cita original conserva su franja hasta que ADMIN decida; tras un rechazo, USER puede conservar o cancelar la cita.
- RN-09: cancelar/rechazar libera reservas correspondientes.
- RN-10: la reprogramación no destruye la cita anterior hasta que sea aprobada.

## Dependencias

- [[EP-006-busqueda-y-agendamiento-de-citas]] (requiere citas existentes).
- [[EP-009-administracion-y-auditoria]] (auditoría de cada cambio de estado; decisión ADMIN de reprogramación).

## Historias de usuario

- [[HU-017-consultar-mis-citas]]
- [[HU-018-cancelar-cita]]
- [[HU-019-solicitar-reprogramacion]]

## Criterio de completitud de la épica

- [ ] Todas las HU obligatorias de esta épica están `Completada`.
- [ ] No quedan dependencias bloqueantes dentro del alcance de la épica.

## Riesgos e incógnitas

- Ninguna incógnita bloqueante identificada; el PRD es explícito en RF-13 a RF-15.
