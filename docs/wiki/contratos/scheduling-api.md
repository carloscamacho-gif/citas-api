# Contrato REST S3 — profesionales, disponibilidad y citas

**Versión:** 1.0  
**Estado:** Aprobado para implementación S3  
**Base URL:** `/api/v1`

Todas las rutas protegidas reciben `Authorization: Bearer <access-token>`. Los errores usan la forma común `ApiError` con `message` y `details`.

## Decisiones

- Los bloques son independientes de la especialidad. La disponibilidad cruza bloque, sede y especialidades activas asignadas al profesional.
- La duración se toma del catálogo de especialidades: 30 minutos ocupa un slot; 60 minutos ocupa dos slots consecutivos.
- `isGeneral=true` produce citas `APPROVED`; las demás nacen `REQUESTED`.
- Desactivar un profesional impide nuevos bloques y reservas, sin cancelar citas existentes.
- La aprobación automática de una cita general registra fuente `SYSTEM` y actor USER.
- Fechas y horas se intercambian como ISO-8601 con offset. La zona operativa del laboratorio es `America/Bogota`.
- Un horario ocupado, una decisión repetida o un solapamiento devuelve `409 Conflict`.

## Profesionales — ADMIN

### `GET /admin/professionals`

Devuelve profesionales, datos básicos y asignaciones.

### `POST /admin/professionals`

```json
{
  "firstName": "Laura",
  "lastName": "Demo",
  "documentType": "CC",
  "documentNumber": "990001",
  "email": "laura.demo@example.test",
  "phone": "3000000001",
  "password": "valor-solo-en-transito",
  "professionalCode": "PRO-001",
  "licenseNumber": "LIC-001",
  "specialtyIds": [1, 2],
  "primarySpecialtyId": 1,
  "locationIds": [1]
}
```

Debe existir exactamente una primaria, incluida en `specialtyIds`. Todas las especialidades y sedes deben estar activas.

### `PUT /admin/professionals/{id}/specialties`

```json
{ "specialtyIds": [1, 2], "primarySpecialtyId": 1 }
```

### `PUT /admin/professionals/{id}/locations`

```json
{ "locationIds": [1, 2] }
```

### `PATCH /admin/professionals/{id}/active`

```json
{ "active": false }
```

## Bloques — PROFESSIONAL

### `GET /professional/availability-blocks?date=2026-10-01&locationId=1`

### `POST /professional/availability-blocks`

```json
{
  "locationId": 1,
  "startAt": "2026-10-01T08:00:00-05:00",
  "endAt": "2026-10-01T12:00:00-05:00"
}
```

### `PATCH /professional/availability-blocks/{id}`

Usa el mismo cuerpo. Solo permite bloques propios, futuros y sin slots comprometidos.

### `DELETE /professional/availability-blocks/{id}`

Devuelve `204 No Content`.

## Disponibilidad — USER

### `GET /availability?locationId=1&specialtyId=1&date=2026-10-01&professionalId=2`

`professionalId` es opcional.

```json
[
  {
    "id": "2",
    "name": "Laura Demo",
    "slots": [
      {
        "startAt": "2026-10-01T08:00:00-05:00",
        "endAt": "2026-10-01T08:30:00-05:00"
      }
    ]
  }
]
```

## Citas — USER

### `POST /appointments`

```json
{
  "professionalId": 2,
  "locationId": 1,
  "specialtyId": 1,
  "startAt": "2026-10-01T08:00:00-05:00",
  "reason": "Control"
}
```

La respuesta contiene `id`, `status`, `professionalName`, `specialtyName`, `locationName`, `startAt`, `durationMinutes` y `rejectionReason`. El backend calcula duración, fin y estado.

### `GET /appointments/{id}/history`

USER ve citas propias, PROFESSIONAL sus citas y ADMIN todas.

## Decisión especializada — ADMIN

### `GET /admin/appointments/pending-specialized`

Acepta filtros opcionales `locationId`, `professionalId`, `specialtyId` y `date`.

### `POST /admin/appointments/{id}/decision`

```json
{ "decision": "REJECT", "reason": "Motivo obligatorio al rechazar" }
```

`APPROVE` mantiene los slots y cambia a `APPROVED`. `REJECT` exige motivo y libera los slots en la misma transacción.
