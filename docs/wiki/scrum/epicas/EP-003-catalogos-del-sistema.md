---
id: EP-003
tipo: epica
titulo: "Catálogos del sistema"
estado: Borrador
historias: ["[[HU-006-consultar-catalogos-fijos]]", "[[HU-007-crud-de-eps]]", "[[HU-008-crud-de-planes-de-eps]]", "[[HU-009-crud-de-especialidades]]"]
dependencias: []
---

# EP-003 — Catálogos del sistema

## Objetivo

Proveer los catálogos que sostienen el resto del dominio: catálogos fijos de solo lectura (roles, estados de cita, estados de reprogramación, regímenes, sedes) y catálogos configurables por ADMIN (EPS, planes de EPS, especialidades).

## Valor esperado

Sin estos catálogos no se puede registrar afiliación, crear profesionales ni agendar citas; es infraestructura de dominio precargada por seed.

## Actores

- ADMIN (gestiona catálogos configurables)
- USER, PROFESSIONAL (consumen catálogos, no los modifican)

## Alcance

- Exposición de catálogos fijos precargados por seed (solo lectura).
- CRUD de EPS, planes de EPS y especialidades por ADMIN.
- Activación/desactivación en lugar de borrado físico cuando el catálogo está referenciado por transacciones.

## Fuera de alcance

- Migración/carga inicial de datos semilla (es responsabilidad de Flyway/seed del backend, no de esta HU de negocio).
- Consumo de catálogos desde otras épicas (cada una declara su propia dependencia).

## Reglas de negocio

- RF-05: catálogos fijos son de solo lectura.
- RF-06: EPS, planes de EPS y especialidades son configurables por ADMIN vía CRUD.
- Regla explícita del PRD (sección RF-06): no se permite borrar físicamente un catálogo referenciado por transacciones; usar activación/desactivación cuando aplique.

## Dependencias

- Ninguna a nivel de catálogos fijos (se precargan por seed).
- [[EP-001-autenticacion-y-sesion]] para las operaciones CRUD, que requieren un ADMIN autenticado.

## Historias de usuario

- [[HU-006-consultar-catalogos-fijos]]
- [[HU-007-crud-de-eps]]
- [[HU-008-crud-de-planes-de-eps]]
- [[HU-009-crud-de-especialidades]]

## Criterio de completitud de la épica

- [ ] Todas las HU obligatorias de esta épica están `Completada`.
- [ ] No quedan dependencias bloqueantes dentro del alcance de la épica.

## Riesgos e incógnitas

- El PRD no detalla el contenido semilla exacto de EPS/planes/especialidades; se resuelve con datos sintéticos durante la implementación, sin inventar reglas de negocio adicionales.
