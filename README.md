# 🚗 Parking API - Sistema de Gestión de Parqueaderos

## 📋 Descripción

API REST para la gestión de parqueaderos desarrollada en Spring Boot. Permite el registro de entrada y salida de vehículos, gestión de parqueaderos, autenticación de usuarios y generación de reportes e indicadores.

## 🏗️ Arquitectura

El proyecto sigue una arquitectura en capas con separación clara de responsabilidades:

```
src/main/java/com/nelumbo/parking/
├── config/          # Configuraciones (Security, DataLoader)
├── controllers/     # Controladores REST
├── dto/            # Objetos de transferencia de datos
├── entities/       # Entidades JPA
├── enums/          # Enumeraciones
├── exceptions/     # Excepciones personalizadas
├── repositories/   # Repositorios de datos
├── security/       # Configuración de seguridad y JWT
└── services/       # Lógica de negocio
```

## 🚀 Tecnologías Utilizadas

- **Java 21**
- **Spring Boot 3.5.4**
- **Spring Security** con JWT
- **Spring Data JPA** con Hibernate
- **PostgreSQL** como base de datos
- **Docker** para containerización
- **Maven** para gestión de dependencias
- **Lombok** para reducción de código boilerplate

## 📋 Requisitos Previos

- Java 17 o superior
- Maven 3.6+
- Docker y Docker Compose
- PostgreSQL (opcional, se incluye en Docker)

## 🛠️ Instalación y Configuración

### 1. Clonar el repositorio
```bash
git clone <url-del-repositorio>
cd parking-api
```

### 2. Configurar base de datos
```bash
# Iniciar PostgreSQL con Docker
docker-compose up -d
```

### 3. Configurar aplicación
El archivo `application.properties` ya está configurado para usar:
- Puerto: 8080
- Base de datos: PostgreSQL en puerto 5434
- Usuario: admin
- Contraseña: 12345678
- Base de datos: parking

### 4. Ejecutar la aplicación
```bash
mvn spring-boot:run
```

La aplicación estará disponible en: `http://localhost:8080`

## 📚 Documentación

Para información detallada sobre configuración, uso de la API y desarrollo, consulta la [documentación completa](docs/README.md):

- **🔧 [Configuración del Entorno](docs/setup/ENVIRONMENT_SETUP.md)** - Guía de variables de entorno y configuración
- **🏗️ [Estructura de Configuración](docs/setup/CONFIGURATION_STRUCTURE.md)** - Arquitectura de la configuración
- **🌐 [Colección Postman](docs/api/Parking-API-Postman-Collection.json)** - Endpoints y pruebas de la API

## 🔐 Autenticación y Autorización

### Roles del Sistema
- **ADMIN**: Acceso completo a todas las funcionalidades
- **SOCIO**: Acceso limitado a operaciones de vehículos y reportes básicos

### Endpoints de Autenticación

#### Login
```http
POST /auth/login
Content-Type: application/json

{
    "email": "admin@nelumbo.com",
    "password": "admin123"
}
```

#### Registro (Solo ADMIN)
```http
POST /auth/register
Authorization: Bearer {token_admin}
Content-Type: application/json

{
    "name": "Nuevo Usuario",
    "email": "usuario@ejemplo.com",
    "password": "password123",
    "role": "SOCIO"
}
```

#### Logout
```http
POST /auth/logout
Authorization: Bearer {token}
```

## 🚗 Gestión de Vehículos

### Entrada de Vehículo
```http
POST /vehicles/entry
Authorization: Bearer {token}
Content-Type: application/json

{
    "licensePlate": "ABC123",
    "parkingId": 1
}
```

### Salida de Vehículo
```http
POST /vehicles/exit
Authorization: Bearer {token}
Content-Type: application/json

{
    "licensePlate": "ABC123",
    "parkingId": 1
}
```

### Vehículos Estacionados
```http
GET /vehicles/parked/{parkingId}
Authorization: Bearer {token}
```

### Búsqueda por Placa
```http
GET /vehicles/search?partialPlate=ABC
Authorization: Bearer {token}
```

## 🏢 Gestión de Parqueaderos

### Crear Parqueadero
```http
POST /parkings
Authorization: Bearer {token_admin}
Content-Type: application/json

{
    "name": "Parqueadero Central",
    "capacity": 100,
    "hourlyRate": 5.00,
    "partnerId": 1
}
```

### Listar Parqueaderos
```http
GET /parkings
Authorization: Bearer {token}
```

### Actualizar Parqueadero
```http
PUT /parkings/{id}
Authorization: Bearer {token_admin}
Content-Type: application/json

{
    "name": "Parqueadero Central Actualizado",
    "capacity": 120,
    "hourlyRate": 6.00
}
```

### Eliminar Parqueadero
```http
DELETE /parkings/{id}
Authorization: Bearer {token_admin}
```

### Asociar Socio
```http
POST /parkings/{parkingId}/associate-partner
Authorization: Bearer {token_admin}
Content-Type: application/json

{
    "partnerId": 1
}
```

## 📊 Reportes e Indicadores

### Top Vehículos (Todos los Parqueaderos)
```http
GET /reports/top-vehicles-all-parkings?limit=10
Authorization: Bearer {token_admin} o {token_socio}
```

### Top Vehículos (Parqueadero Específico)
```http
GET /reports/parking/{parkingId}/top-vehicles?limit=10
Authorization: Bearer {token_admin} o {token_socio}
```

### Vehículos de Primera Vez
```http
GET /reports/parking/{parkingId}/first-time-vehicles
Authorization: Bearer {token_admin} o {token_socio}
```

### Ganancias por Período (SOCIO)
```http
GET /reports/parking/{parkingId}/earnings-period?period=today
GET /reports/parking/{parkingId}/earnings-period?period=week
GET /reports/parking/{parkingId}/earnings-period?period=month
GET /reports/parking/{parkingId}/earnings-period?period=year
Authorization: Bearer {token_socio}
```

### Ganancias por Fecha (ADMIN)
```http
GET /reports/parking/{parkingId}/earnings?date=2024-12-15
Authorization: Bearer {token_admin}
```

### Estadísticas Generales (ADMIN)
```http
GET /reports/statistics
Authorization: Bearer {token_admin}
```

## 📧 Microservicio de Email

### Envío de Email
```http
POST /email/send
Content-Type: application/json

{
    "email": "usuario@ejemplo.com",
    "placa": "ABC123",
    "mensaje": "Vehículo registrado exitosamente",
    "parqueaderoNombre": "Parqueadero Central"
}
```

**Nota**: Este endpoint se llama automáticamente cuando:
- Se registra la entrada de un vehículo
- Se registra la salida de un vehículo

## 🧪 Testing

### Ejecutar Tests Unitarios
```bash
mvn test
```

### Ejecutar Tests de Integración
```bash
mvn verify
```

## 📁 Estructura de Base de Datos

### Tablas Principales
- **users**: Usuarios del sistema (ADMIN, SOCIO)
- **parkings**: Parqueaderos disponibles
- **vehicles**: Vehículos registrados
- **parking_records**: Registros activos de estacionamiento
- **vehicle_history**: Historial de estacionamientos
- **invalid_tokens**: Tokens JWT invalidados

## 🔧 Configuración de Desarrollo

### Variables de Entorno
```properties
# Base de datos
spring.datasource.url=jdbc:postgresql://localhost:5434/parking
spring.datasource.username=admin
spring.datasource.password=12345678

# JWT
app.jwt.secret=ZmQ0ZGI5NjQ0MDQwY2I4MjMxY2Y3ZmI3MjdhN2ZmYTA=
app.jwt.expiration=21600000

# Puerto de la aplicación
server.port=8080
```

### Docker Compose
```yaml
version: '3.8'
services:
  postgres:
    image: postgres:15
    environment:
      POSTGRES_DB: parking
      POSTGRES_USER: admin
      POSTGRES_PASSWORD: 12345678
    ports:
      - "5434:5432"
    volumes:
      - postgres_data:/var/lib/postgresql/data

volumes:
  postgres_data:
```

## 📚 Documentación de la API

### Postman Collection
Se incluye una colección de Postman con todos los endpoints configurados:
```
Parking-API-Postman-Collection.json
```

## 🚨 Manejo de Errores

El sistema incluye un manejador global de excepciones que devuelve respuestas HTTP apropiadas:

- **400 Bad Request**: Errores de validación
- **401 Unauthorized**: No autenticado
- **403 Forbidden**: No autorizado
- **404 Not Found**: Recurso no encontrado
- **500 Internal Server Error**: Errores del servidor

### Excepciones Personalizadas
- `AuthenticationException`: Errores de autenticación
- `AuthorizationException`: Errores de autorización
- `ValidationException`: Errores de validación de datos

## 🔒 Seguridad

### JWT (JSON Web Tokens)
- Algoritmo: HS256
- Expiración: 6 horas (configurable)
- Clave secreta: 256 bits (Base64 encoded)

### Endpoints Públicos
- `POST /auth/login`
- `GET /email/health`

### Endpoints Protegidos
- Todos los demás endpoints requieren autenticación JWT
- Los roles se verifican mediante anotaciones `@PreAuthorize`

## 📈 Monitoreo y Logs

### Logs de Aplicación
- Los logs se muestran en la consola
- Nivel por defecto: INFO
- Incluye logs de simulación de emails

### Health Checks
```http
GET /email/health
```

## 👥 Autores

- **Pamela Galvis** - *Desarrollo inicial* - [https://github.com/pamelamichellga01](https://github.com/TuUsuario)
---

**¡Gracias por usar Parking API! 🚗✨**

