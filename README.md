# Jobsy - Backend

## Descripción general

El proyecto consiste en desarrollar una **API RESTful** que funcione como intermediario entre **proveedores de servicios locales** (plomeros, electricistas, tutores, etc.) y **clientes** que buscan contratarlos.
La API permitirá el registro de usuarios, la publicación de servicios, la gestión de reservas, pagos simulados, reseñas y un sistema de mensajería.

## Objetivo

Conectar de manera eficiente a clientes y proveedores mediante una plataforma escalable y segura, que soporte operaciones CRUD, autenticación con JWT y funcionalidades propias de un marketplace de servicios.

## Roles de usuario

* **Cliente**: busca servicios, reserva citas, paga anticipos, deja reseñas.
* **Proveedor**: ofrece servicios, define disponibilidad, gestiona reservas y responde mensajes.
* **Administrador**: supervisa usuarios y servicios.

## Funcionalidades expuestas por la API

1. **Autenticación y Roles**

    * Registro e inicio de sesión con JWT.
    * Gestión de roles y permisos.

2. **Gestión de Proveedores y Servicios**

    * CRUD de perfiles de proveedores.
    * CRUD de servicios (categorías, descripción, tarifas).

3. **Reservas / Agenda**

    * Crear y gestionar reservas.
    * Estados de cita: pendiente, confirmada, completada, cancelada.

4. **Pagos (simulación)**

    * Flujo de anticipos mediante Stripe/PayPal sandbox.
    * Historial de pagos.

5. **Reseñas y Calificaciones**

    * Registro de calificaciones y comentarios por parte del cliente.
    * Cálculo de promedio de calificaciones por proveedor.

6. **Geolocalización y búsqueda**

    * Búsqueda de proveedores cercanos.
    * Filtros por categoría, precio y calificación.

7. **Mensajería básica**

    * Chat entre cliente y proveedor vinculado a una cita.

8. **Paneles / Dashboards** (expuestos vía API para el frontend)

    * Cliente: historial de reservas, pagos y reseñas.
    * Proveedor: agenda, historial de servicios completados, calificaciones.

## Diseño de la API REST

* **Estilo:** RESTful, recursos identificados por URL.
* **Formato:** JSON para request/response.
* **Seguridad:** JWT para autenticación, control de acceso por roles.
* **Versionado:** `/api/v1/...`