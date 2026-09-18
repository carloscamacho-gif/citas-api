---
tipo: wiki
---

# Decisiones — Sistema de Agendamiento de Citas FCV

Decisiones explícitas tomadas por el estudiante o derivadas directamente de instrucciones aprobadas. No incluir aquí supuestos no confirmados (van en [[riesgos]]).

## 2026-09-18

- **Backlog Scrum**: se generaron 9 épicas y 24 HU cubriendo RF-01 a RF-19 del PRD. RF-20 (contrato REST) se trató como ítem transversal de Definition of Done en vez de HU propia, por ser un requisito puramente técnico sin valor observable independiente.
- **Alcance Sprint 1 (S2)**: aprobadas HU-001 (Registrar usuario), HU-002 (Iniciar sesión y sesión JWT) y HU-006 (Consultar catálogos fijos). El resto del backlog permanece en `Borrador` hasta nueva aprobación explícita.
- **AGENTS.md raíz y estructura LLM Wiki**: aprobados y creados tal como fueron propuestos por el agente orquestador (ver `AGENTS.md` en la raíz del workspace).
- **Workflows n8n (RF de la sección 10 del PRD)**: quedan fuera del backlog Scrum funcional; ya tienen especificación propia en `citas-api/automations/n8n/WF-00x-*.md` y corresponden a sesiones S5/S6.
- **Nombre de la base de datos de la aplicación**: `MYSQL_DATABASE`/`DB_NAME` pasan de `citas_fcv_training` a `citas_fcv_app` en `.env`, `.env.example` (raíz) y `citas-api/.env.example`. Motivo: `citas_fcv_training` ya tenía cargado el esquema de referencia (`database/reference/db.sql`) en el volumen Docker de este laboratorio, y Flyway no puede gestionar un esquema no vacío sin tabla de historial. `citas_fcv_app` es ahora la única base que las migraciones de `citas-api` deben tocar; `citas_fcv_training` se conserva sin modificar para quien quiera explorar el modelo de referencia/ERD por separado. Ver [[riesgos]] si en algún momento se quiere unificar ambas bases.

## Ver también

- [[riesgos]] para las decisiones aún pendientes de tomar.
- `citas-api/docs/wiki/scrum/README.md` para el detalle completo del backlog.
