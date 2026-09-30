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

## Contrato REST (ya expuesto por el backend)

Nuestro `citas-api` **ya expone** ambas piezas (cableado de S5):

1. **Lectura (WF-001, WF-003)** — `GET /api/v1/admin/appointments/upcoming?from=YYYY-MM-DD&to=YYYY-MM-DD[&locationId=]`
   (rol ADMIN). Devuelve un arreglo de citas `APPROVED` con
   `id, specialty, professional, location, durationMinutes, status, scheduledStartAt, scheduledEndAt`
   (nombres alineados con lo que consumen los workflows). Sin rango, usa hoy→mañana.
2. **Webhook de salida (WF-002)** — el backend hace `POST` a la URL del webhook de n8n
   (`https://<n8n>/webhook/citas/fcv/status-events`) con `Authorization: Bearer <token>` y cuerpo:

   ```json
   { "schemaVersion": "1", "eventId": "<uuid>", "eventType": "AppointmentStatusChanged",
     "appointmentId": 5, "status": "APPROVED|REJECTED|CANCELLED", "source": "ADMIN|USER|SYSTEM",
     "actorUserId": 1, "occurredAt": "<iso-8601>" }
   ```

   Lo emite `N8nAppointmentEventPublisher` en las transiciones de **aprobación/rechazo** (ADMIN) y
   **cancelación** (USER), **después del commit** y sin afectar la transacción si falla la entrega.
   Está **desactivado por defecto**; se habilita con `app.n8n.webhook.enabled=true` + `url` + `bearer-token`
   (`N8N_WEBHOOK_ENABLED`, `N8N_WEBHOOK_URL`, `N8N_WEBHOOK_BEARER_TOKEN`), que viven en entorno, no en el repo.

   Para usarlo: importar y **activar** WF-002 en n8n, conectar sus credenciales, copiar su URL de producción a
   `N8N_WEBHOOK_URL` y el mismo bearer a la credencial *FCV Webhook Bearer* y a `N8N_WEBHOOK_BEARER_TOKEN`.

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
