# Actualización: Precio Final en Reservas

## Cambio Implementado

A partir de esta versión, cuando un provider marca una reserva como **COMPLETADA**, puede establecer el precio real que se cobró al cliente.

### Flujo Anterior
- Al crear la reserva, se establecía `priceAtBooking` con el valor de `offering.basePrice`
- Este valor no se modificaba posteriormente

### Flujo Nuevo
- Al crear la reserva, se sigue estableciendo `priceAtBooking` con el valor de `offering.basePrice`
- **Al completar la reserva**, el provider puede enviar un `finalPrice` opcional
- Si se proporciona `finalPrice`, se actualiza el campo `priceAtBooking` con este nuevo valor
- Esto permite reflejar el precio real cobrado (que puede incluir servicios adicionales, descuentos, etc.)

## Cambios Técnicos

### DTO `BookingStatusUpdateRequest`
```java
public record BookingStatusUpdateRequest(
    @NotNull BookingStatus status,
    @Size(max = 500) String comment,
    @Positive Double finalPrice  // Nuevo campo opcional
)
```

### Servicio `BookingServiceImpl`
El método `handleCompleted` ahora acepta y procesa el `finalPrice`:
```java
private void handleCompleted(Booking booking, Long authId, String comment, Double finalPrice) {
    validateProviderAction(booking, authId, BookingStatus.CONFIRMED, "marcar la reserva como completada");
    
    // Si el provider proporciona un precio final, actualizar el priceAtBooking
    if (finalPrice != null) {
        booking.setPriceAtBooking(finalPrice);
    }
    
    updateBookingStatusAndNotify(booking, BookingStatus.COMPLETED, comment, booking.getClient(),
            "Reserva completada", "La reserva fue completada");
}
```

## Uso desde el Frontend

Al completar una reserva, el provider puede enviar:

```json
{
  "status": "COMPLETED",
  "comment": "Servicio realizado exitosamente",
  "finalPrice": 150.00
}
```

Si no se envía `finalPrice`, el precio se mantiene como estaba (el precio base del offering).

## Notas Importantes

- El campo `finalPrice` es **opcional**
- Solo se acepta cuando el estado es `COMPLETED`
- El campo debe ser un número positivo si se proporciona
- No se requiere migración de base de datos, ya que se usa el campo existente `priceAtBooking`

