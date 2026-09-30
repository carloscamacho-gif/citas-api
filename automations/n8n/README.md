# Automatizaciones n8n — FCV (S5/S6)

Exportaciones JSON de los tres workflows de n8n del laboratorio FCV (datos ficticios). Cada archivo se
importa en n8n (Workflows → Import from File). Los workflows quedan **inactivos** y **sin secretos**: las
credenciales se referencian por nombre y se conectan en n8n; la URL del backend y los correos de laboratorio
se ajustan en el nodo **Configuracion** de cada workflow.

| Archivo | Trigger | Qué hace |
|---|---|---|
| `WF-001-appointment-reminders.json` | Schedule (cada hora) | Consulta citas `APPROVED` próximas, descarta las ya recordadas (Data Table, idempotencia) y envía recordatorio por Gmail; registra `SENT`/`FAILED`. |
| `WF-002-status-notifications.json` | Webhook (desde `citas-api`) | Valida el evento de cambio de estado, ramifica por estado y notifica por Gmail; respuesta determinista `200`/`202`/`400`. |
| `WF-003-daily-operational-summary.json` | Schedule (diario) | Agrupa las citas `APPROVED` del día por sede/especialidad/estado y envía un resumen operativo por Gmail (bonus). |

En la instancia de n8n del curso los workflows están como `FCV-WF-001/002/003-*` (proyecto personal), inactivos,
usando la Data Table `FCV-Automatizaciones-Log`. Cada *sticky note* lleva las instrucciones de activación.

## Credenciales a conectar (no van en el JSON)

- **FCV Gmail** — `gmailOAuth2`. OAuth2 de Gmail para envío. Cada estudiante crea la suya (privilegio mínimo: solo envío).
- **FCV API Auth** — `httpCustomAuth` (Templated Custom Auth). Plantilla que inyecta la cabecera de autenticación
  contra `citas-api`, p. ej. `{"headers":{"Authorization":"Bearer {{token}}"}}` con un token de ADMIN/servicio.
  Usada por WF-001 y WF-003.
- **FCV Webhook Bearer** — `httpHeaderAuth` (WF-002). Header `Authorization` = `Bearer <token>`, el mismo que
  configure el backend al emitir el webhook.

## Contrato REST esperado (lo que el backend debe exponer)

Los workflows asumen este contrato; nuestro `citas-api` **todavía no lo expone** y debe completarse en S5:

1. **Lectura (WF-001, WF-003)** — `GET /api/v1/admin/appointments/upcoming?from=YYYY-MM-DD&to=YYYY-MM-DD`
   (rol ADMIN o clave de servicio). Debe devolver un arreglo de citas `APPROVED` con al menos
   `id, specialty, professional, location, durationMinutes, status, scheduledStartAt, scheduledEndAt`.
   *Estado actual:* existe `GET /api/v1/admin/appointments/pending-specialized` (solo `REQUESTED`) y
   `GET /api/v1/professional/agenda` (APPROVED por profesional); falta el listado global "próximas aprobadas".
2. **Webhook de salida (WF-002)** — el backend debe hacer `POST` a la URL del webhook de n8n
   (`https://<n8n>/webhook/citas/fcv/status-events`) con `Authorization: Bearer <token>` y cuerpo:

   ```json
   { "schemaVersion": "1", "eventId": "<uuid>", "eventType": "AppointmentStatusChanged",
     "appointmentId": 5, "status": "APPROVED|REJECTED|CANCELLED", "source": "ADMIN|USER|SYSTEM",
     "actorUserId": 1, "occurredAt": "<iso-8601>" }
   ```

   *Estado actual:* no hay publicador de eventos saliente; debe añadirse (emisor que dispare en las
   transiciones de aprobación/rechazo/cancelación, configurable por propiedades y desactivado por defecto).

> Estas dos piezas de backend son el "cableado" de S5 (exponer/invocar la automatización). Los JSON de n8n ya
> están listos para consumirlas en cuanto existan.

## Idempotencia y trazabilidad

- **WF-001** deduplica con la Data Table `FCV-Automatizaciones-Log` (clave `appointmentId|scheduledStartAt`
  + `workflow` + `result=SENT`) y registra cada envío (`SENT`/`FAILED`) con el destinatario enmascarado.
- **WF-002** responde `200` (enviado), `202` (correo diferido, el emisor puede reintentar) o `400` (payload inválido).
- La traza fina de cada corrida queda en el historial de ejecuciones de n8n.

## Verificación realizada (ejecución controlada, sin envíos reales)

Los tres workflows se construyeron con el SDK de n8n, se validaron y se probaron con *pin data*:

- **WF-002:** payload `APPROVED` válido → rama de envío + `200`; estado no soportado → rama `400`.
- **WF-001:** una cita `APPROVED` → preparación + envío (Gmail simulado) + registro; segunda corrida con la
  misma cita → la Data Table la **descarta** (idempotencia).
- **WF-003:** varias citas en distintas sedes/especialidades → agregación correcta por sede/especialidad/estado.

## Notas de seguridad (contenido no confiable — S5/S6)

- Los secretos (tokens, OAuth) viven en credenciales de n8n, nunca en estos JSON.
- El destinatario se enmascara en los registros.
- Todo payload de webhook se trata como no confiable: WF-002 valida tipo/estado/campos antes de actuar.
