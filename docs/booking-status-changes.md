# Gestión de Estados de Reservas (Bookings)

## Estados Disponibles

```java
public enum BookingStatus {
    PENDING,      // Reserva creada, esperando confirmación del proveedor
    CONFIRMED,    // Proveedor confirmó la reserva
    COMPLETED,    // Servicio completado (solo proveedor puede marcar)
    CANCELED,     // Cliente canceló su propia reserva
    REJECTED,     // Proveedor rechazó la reserva
    IN_PROGRESS   // Servicio en progreso (futuro uso)
}
```

## Flujo de Estados

### 1. PENDING → CONFIRMED
- **Quién:** Solo el **proveedor**
- **Desde estado:** `PENDING`
- **Acción:** Confirmar la reserva
- **Notificación:** Se notifica al cliente
- **Efecto adicional:** Se crea una conversación entre cliente y proveedor

### 2. PENDING → REJECTED
- **Quién:** Solo el **proveedor**
- **Desde estado:** `PENDING`
- **Acción:** Rechazar la reserva
- **Notificación:** Se notifica al cliente con el motivo del rechazo

### 3. ANY → CANCELED
- **Quién:** Solo el **cliente**
- **Desde estado:** Cualquiera excepto `CANCELED`, `REJECTED` o `COMPLETED`
- **Acción:** Cancelar su propia reserva
- **Notificación:** Se notifica al proveedor

### 4. CONFIRMED → COMPLETED
- **Quién:** Solo el **proveedor**
- **Desde estado:** `CONFIRMED`
- **Acción:** Marcar servicio como completado
- **Notificación:** Se notifica al cliente
- **Campos adicionales:** 
  - `finalPrice` (opcional): Precio real cobrado al finalizar el servicio

## Ejemplos de Uso desde el Frontend

### Proveedor confirma una reserva
```json
PATCH /api/v1/bookings/{id}/status
{
  "status": "CONFIRMED",
  "comment": "Confirmado, nos vemos a la hora acordada"
}
```

### Proveedor rechaza una reserva
```json
PATCH /api/v1/bookings/{id}/status
{
  "status": "REJECTED",
  "comment": "Lo siento, no tengo disponibilidad para esa fecha"
}
```

### Cliente cancela una reserva
```json
PATCH /api/v1/bookings/{id}/status
{
  "status": "CANCELED",
  "comment": "Ya no necesito el servicio"
}
```

### Proveedor completa una reserva (sin cambio de precio)
```json
PATCH /api/v1/bookings/{id}/status
{
  "status": "COMPLETED",
  "comment": "Servicio completado satisfactoriamente"
}
```

### Proveedor completa una reserva (con precio final diferente)
```json
PATCH /api/v1/bookings/{id}/status
{
  "status": "COMPLETED",
  "comment": "Servicio completado con servicios adicionales",
  "finalPrice": 150.00
}
```

## Validaciones Importantes

### CANCELED (Cliente)
- Solo el cliente puede cancelar su propia reserva
- No se puede cancelar si está en estado `CANCELED`, `REJECTED` o `COMPLETED`

### REJECTED (Proveedor)
- Solo el proveedor puede rechazar
- Solo se puede rechazar desde estado `PENDING`

### CONFIRMED (Proveedor)
- Solo el proveedor puede confirmar
- Solo se puede confirmar desde estado `PENDING`

### COMPLETED (Proveedor)
- Solo el proveedor puede completar
- Solo se puede completar desde estado `CONFIRMED`
- Opcionalmente puede establecer un precio final diferente al precio base

## Notificaciones

Cada cambio de estado genera notificaciones automáticas:

| Estado | Tipo de Notificación | Destinatario |
|--------|---------------------|--------------|
| CONFIRMED | `BOOKING_CONFIRMED` | Cliente |
| REJECTED | `BOOKING_CANCELLED` | Cliente |
| CANCELED | `BOOKING_CANCELLED` | Proveedor |
| COMPLETED | `BOOKING_COMPLETED` | Cliente |

## Diferencias Clave: CANCELED vs REJECTED

| Aspecto | CANCELED | REJECTED |
|---------|----------|----------|
| **Quién lo ejecuta** | Cliente | Proveedor |
| **Significado** | El cliente ya no quiere el servicio | El proveedor no puede/quiere proveer el servicio |
| **Estado previo típico** | Cualquiera (PENDING, CONFIRMED) | Solo PENDING |
| **Destinatario de notificación** | Proveedor | Cliente |

