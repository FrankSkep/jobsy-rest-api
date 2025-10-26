# Jobsy - REST API

> Para la versión en inglés, haz clic aquí → [English Version](./README.md)

## Descripción

**Jobsy** es una plataforma que conecta a **proveedores de servicios locales** (como plomeros, electricistas, tutores, entre otros) con **clientes** que necesitan contratarlos.
Esta API RESTful permite la gestión de usuarios, verificación de proveedores, servicios, reservas, reseñas y comunicación en tiempo real.

---

## Objetivo

Proveer una plataforma **escalable y segura** que conecte de forma eficiente a clientes y proveedores verificados mediante operaciones CRUD, autenticación con JWT y funcionalidades específicas de un marketplace.

---

## Roles de Usuario

* **USER**: Rol predeterminado tras el registro. Puede buscar servicios, reservar proveedores y dejar reseñas.
  También puede solicitar convertirse en proveedor enviando documentos de verificación.
* **PROVIDER**: Usuario verificado y aprobado por un administrador. Puede publicar servicios, gestionar su disponibilidad y recibir reservas.
  Antes de obtener este rol, debe enviar una solicitud de verificación (INE, fotos y datos del perfil).
* **ADMIN**: Supervisa usuarios y solicitudes de proveedor, gestiona categorías y mantiene la integridad del sistema.

---

## Funcionalidades Principales

### Autenticación y Autorización

* Registro e inicio de sesión con JWT
* Control de acceso basado en roles (RBAC)
* Validación y renovación de tokens
* Encriptación segura de contraseñas con BCrypt

---

### Verificación de Usuarios y Proveedores

* Los usuarios pueden enviar una **solicitud de proveedor** que incluye:

    * Información personal y ubicación
    * Fotos de identificación (INE, ambos lados) y selfies
    * Datos profesionales (experiencia, biografía, tarifa por hora)
* Los administradores revisan manualmente las solicitudes para aprobarlas o rechazarlas.
* Una vez aprobada, el rol del usuario cambia de **USER** → **PROVIDER**.

---

### Gestión de Proveedores y Servicios

* CRUD completo para los servicios de proveedores verificados
* Categorías, descripciones y tarifas personalizables
* Certificaciones y portafolio opcional
* Soporte de geolocalización para establecer el radio de servicio

---

### Sistema de Reservas

* Los clientes pueden agendar citas con proveedores
* Estados de la reserva: `PENDING`, `CONFIRMED`, `COMPLETED`, `CANCELLED`
* Los proveedores gestionan sus horarios de disponibilidad
* Notificaciones para actualizaciones y confirmaciones de reservas

---

### Reseñas y Calificaciones

* Los clientes pueden dejar reseñas después de completar un servicio
* Cálculo automático del promedio de calificación por proveedor
* Comentarios textuales para mayor transparencia

---

### Geolocalización y Búsqueda

* Búsqueda de proveedores cercanos según ubicación
* Filtros por categoría, distancia y calificación
* Resultados optimizados según ubicación geográfica

---

### Mensajería y Notificaciones en Tiempo Real

* Chat entre cliente y proveedor mediante WebSockets
* Asociado a una reserva específica
* Notificaciones por correo electrónico y dentro de la plataforma

---

### Panel de Administración

* Visualización y revisión de solicitudes de verificación de proveedores
* Aprobación o rechazo de solicitudes con motivo
* Gestión de categorías y monitoreo general del sistema

---

## Tecnologías Utilizadas

* **Framework:** Spring Boot 3.2.8
* **Lenguaje:** Java 21
* **Base de datos:** PostgreSQL 16
* **Autenticación:** JWT (JSON Web Tokens)
* **Seguridad:** Spring Security
* **Persistencia:** Spring Data JPA
* **Mapeo:** MapStruct
* **Documentación:** SpringDoc OpenAPI (Swagger)
* **Gestión de archivos:** Cloudinary
* **Correo electrónico:** Spring Mail
* **Validación:** Jakarta Bean Validation
* **Cacheo:** Redis
* **Compilación:** Maven
* **Comunicación en tiempo real:** WebSockets

---

## Instalación y Configuración

### Prerrequisitos

* Java 21+
* Maven 3.6+
* PostgreSQL 16+

### Pasos de instalación

1. **Clonar el repositorio**

   ```bash
   git clone https://github.com/FrankSkep/jobsy-rest-api.git
   cd jobsy-rest-api
   ```

2. **Configurar variables de entorno**
   Crea un archivo `.env` en la raíz del proyecto:

   ```properties
   DB_URL=jdbc:postgresql://localhost:5432/jobsy
   DB_USERNAME=tu_usuario
   DB_PASSWORD=tu_contraseña
   JWT_SECRET=tu_clave_secreta
   JWT_EXPIRATION_MS=tiempo_de_expiracion_en_ms
   CLOUDINARY_API_SECRET=tu_cloudinary_api_secret
   CLOUDINARY_API_KEY=tu_cloudinary_api_key
   CLOUDINARY_CLOUD_NAME=tu_cloudinary_cloud_name
   MAIL_HOST=smtp.gmail.com
   MAIL_PORT=587
   MAIL_USERNAME=tu_correo
   MAIL_PASSWORD=tu_contraseña
   ```

3. **Instalar dependencias**

   ```bash
   ./mvnw clean install
   ```

4. **Ejecutar la aplicación**

   ```bash
   ./mvnw spring-boot:run
   ```

   La API estará disponible en `http://localhost:8080`.

---

## Documentación de la API

Una vez ejecutada la aplicación, puedes acceder a la documentación interactiva:

* **Swagger UI:** `http://localhost:8080/swagger-ui/index.html`

---

## Licencia

Este proyecto está licenciado bajo la **Licencia MIT**.

---

## Autor

**FrankSkep** — [GitHub](https://github.com/FrankSkep)