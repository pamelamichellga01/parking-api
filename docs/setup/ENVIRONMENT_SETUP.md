# Configuración de Variables de Entorno - Parking API

## 📋 Descripción General

Este documento describe cómo configurar y usar las variables de entorno en la aplicación Parking API. La aplicación utiliza Spring Boot con **configuración unificada** que lee automáticamente las variables del archivo `.env` gracias a la librería `spring-dotenv`.

## 🚀 Inicio Rápido

### 1. Configuración Básica

```bash
# Copia el archivo de ejemplo
cp env.example .env

# Edita el archivo .env con tus valores
nano .env
```

### 2. Ejecutar la Aplicación

```bash
# Ejecutar normalmente (lee .env automáticamente)
./mvnw spring-boot:run

# O compilar y ejecutar
./mvnw clean package
java -jar target/parking-api-0.0.1-SNAPSHOT.jar
```

## 🔧 Variables de Entorno Principales

### Configuración de la Aplicación
| Variable | Descripción | Valor por Defecto |
|----------|-------------|-------------------|
| `APP_ENVIRONMENT` | Entorno de la aplicación | `development` |
| `APP_NAME` | Nombre de la aplicación | `parking-api` |
| `APP_VERSION` | Versión de la aplicación | `1.0.0` |
| `APP_TIMEZONE` | Zona horaria | `America/Bogota` |
| `APP_LOCALE` | Configuración regional | `es_CO` |

### Base de Datos
| Variable | Descripción | Valor por Defecto |
|----------|-------------|-------------------|
| `DB_URL` | URL de conexión a la BD | `jdbc:postgresql://localhost:5434/parking` |
| `DB_USERNAME` | Usuario de la BD | `admin` |
| `DB_PASSWORD` | Contraseña de la BD | `12345678` |
| `DB_DRIVER` | Driver de la BD | `org.postgresql.Driver` |
| `DB_MAX_POOL_SIZE` | Tamaño máximo del pool | `10` |
| `DB_MIN_IDLE` | Conexiones mínimas inactivas | `5` |
| `DB_CONNECTION_TIMEOUT` | Timeout de conexión (ms) | `30000` |

### JPA/Hibernate
| Variable | Descripción | Valor por Defecto |
|----------|-------------|-------------------|
| `JPA_DDL_AUTO` | Estrategia de DDL | `update` |
| `JPA_SHOW_SQL` | Mostrar SQL en logs | `true` |
| `HIBERNATE_DIALECT` | Dialecto de Hibernate | `org.hibernate.dialect.PostgreSQLDialect` |
| `JPA_DEFER_DATASOURCE_INIT` | Inicialización diferida | `true` |

### Seguridad JWT
| Variable | Descripción | Valor por Defecto |
|----------|-------------|-------------------|
| `JWT_SECRET` | Clave secreta para JWT | `ZmQ0ZGI5NjQ0MDQwY2I4MjMxY2Y3ZmI3MjdhN2ZmYTA=` |
| `JWT_EXPIRATION` | Expiración del token (ms) | `21600000` (6 horas) |
| `JWT_ALGORITHM` | Algoritmo de firma | `HS256` |
| `MAX_LOGIN_ATTEMPTS` | Máximos intentos de login | `5` |
| `LOCKOUT_DURATION` | Duración del bloqueo (min) | `15` |

### Email
| Variable | Descripción | Valor por Defecto |
|----------|-------------|-------------------|
| `EMAIL_USERNAME` | Usuario del email | - |
| `EMAIL_PASSWORD` | Contraseña del email | - |
| `EMAIL_SMTP_HOST` | Servidor SMTP | `smtp.gmail.com` |
| `EMAIL_SMTP_PORT` | Puerto SMTP | `587` |

### Scheduling
| Variable | Descripción | Valor por Defecto |
|----------|-------------|-------------------|
| `TOKEN_CLEANUP_CRON` | Expresión cron para limpieza | `0 0 2 * * ?` (2 AM diario) |

### Servidor
| Variable | Descripción | Valor por Defecto |
|----------|-------------|-------------------|
| `SERVER_PORT` | Puerto del servidor | `8080` |

### Logging
| Variable | Descripción | Valor por Defecto |
|----------|-------------|-------------------|
| `LOG_LEVEL_COM_NELUMBO_PARKING` | Nivel de log de la app | `INFO` |
| `LOG_LEVEL_SPRING_SECURITY` | Nivel de log de seguridad | `INFO` |

### Cache
| Variable | Descripción | Valor por Defecto |
|----------|-------------|-------------------|
| `CACHE_MAX_SIZE` | Tamaño máximo del cache | `500` |
| `CACHE_EXPIRE_AFTER_WRITE` | Expiración del cache (s) | `600` |

### Parking
| Variable | Descripción | Valor por Defecto |
|----------|-------------|-------------------|
| `PARKING_MAX_CAPACITY` | Capacidad máxima | `100` |
| `PARKING_HOURLY_RATE` | Tarifa por hora | `5000` |
| `PARKING_DAILY_RATE` | Tarifa por día | `50000` |
| `PARKING_CURRENCY` | Moneda | `COP` |

### Reportes
| Variable | Descripción | Valor por Defecto |
|----------|-------------|-------------------|
| `REPORT_MAX_DAYS_RANGE` | Rango máximo de días | `365` |
| `REPORT_DEFAULT_FORMAT` | Formato por defecto | `PDF` |
| `REPORT_ENABLE_EXPORT` | Habilitar exportación | `true` |

### Actuator
| Variable | Descripción | Valor por Defecto |
|----------|-------------|-------------------|
| `MANAGEMENT_ENDPOINTS_WEB_EXPOSURE_INCLUDE` | Endpoints expuestos | `health,info` |
| `MANAGEMENT_ENDPOINT_HEALTH_SHOW_DETAILS` | Detalles de health | `when-authorized` |

## 📁 Archivos de Configuración

### Estructura de Archivos
```
src/main/resources/
├── application.properties          # Configuración única y completa
.env                               # Variables de entorno (valores reales)
env.example                        # Plantilla de variables de entorno
```

### Cómo Funciona
1. **`application.properties`**: Contiene todas las configuraciones con referencias a variables de entorno
2. **`.env`**: Contiene los valores reales de las variables de entorno
3. **`spring-dotenv`**: Lee automáticamente el archivo `.env` y las hace disponibles para Spring Boot
4. **Valores por defecto**: Si una variable no está definida, se usa el valor por defecto

## 🌍 Configuraciones por Entorno

### Desarrollo
Para desarrollo local, usa los valores por defecto del archivo `.env`:

```properties
APP_ENVIRONMENT=development
LOG_LEVEL_COM_NELUMBO_PARKING=INFO
LOG_LEVEL_SPRING_SECURITY=INFO
JPA_SHOW_SQL=true
```

### Testing
Para testing, puedes cambiar a H2 en memoria:

```properties
# En .env, cambiar estas variables:
DB_URL=jdbc:h2:mem:testdb
DB_DRIVER=org.h2.Driver
HIBERNATE_DIALECT=org.hibernate.dialect.H2Dialect
JPA_DDL_AUTO=create-drop
JPA_SHOW_SQL=false
```

### Producción
Para producción, ajusta los valores de seguridad y rendimiento:

```properties
# En .env, ajustar para producción:
APP_ENVIRONMENT=production
LOG_LEVEL_COM_NELUMBO_PARKING=WARN
LOG_LEVEL_SPRING_SECURITY=WARN
JPA_SHOW_SQL=false
MANAGEMENT_ENDPOINTS_WEB_EXPOSURE_INCLUDE=health,info
```

## 🔍 Debugging de Configuración

### 1. Ver Variables Cargadas
```bash
# Endpoint de Actuator (si está habilitado)
GET /actuator/env
```

### 2. Ver Variables de Entorno del Sistema
```bash
# En Windows PowerShell
Get-ChildItem Env:

# En Linux/Mac
env | grep APP_
```

### 3. Ver Logs de Configuración
```bash
# En los logs de la aplicación
2024-01-01 10:00:00.000  INFO 1234 --- [main] c.n.p.config.EnvironmentConfig : Inicializando configuración de entorno...
```

## 🚨 Consideraciones Importantes

### 1. Seguridad
- **Nunca comitear `.env`**: Contiene información sensible
- **Siempre incluir `env.example`**: Para documentar variables
- **Usar valores por defecto seguros**: Para desarrollo local

### 2. Variables Requeridas
- **Base de datos**: `DB_URL`, `DB_USERNAME`, `DB_PASSWORD`
- **JWT**: `JWT_SECRET`
- **Email**: `EMAIL_USERNAME`, `EMAIL_PASSWORD` (si se usa email)

### 3. Valores por Defecto
- **Siempre proporcionar valores de respaldo** en `application.properties`
- **Usar valores apropiados** para desarrollo local
- **Documentar todas las variables** en `env.example`

## 🧪 Testing de Configuración

### 1. Verificar Carga de Variables
```bash
# Ejecutar la aplicación y verificar logs
./mvnw spring-boot:run
```

### 2. Verificar Conexión a Base de Datos
```bash
# Verificar endpoint de health
curl http://localhost:8080/actuator/health
```

### 3. Verificar Variables Específicas
```bash
# En los logs de la aplicación, buscar:
# "Inicializando configuración de entorno..."
# "Perfil activo: default"
# "Entorno: development"
```

## 📚 Referencias

- [Spring Boot Externalized Configuration](https://docs.spring.io/spring-boot/docs/current/reference/html/features.html#features.external-config)
- [Spring Boot Configuration Properties](https://docs.spring.io/spring-boot/docs/current/reference/html/features.html#features.external-config.typesafe-configuration-properties)
- [spring-dotenv Documentation](https://github.com/paulschwarz/spring-dotenv)

## 🔧 Ejemplo de Uso Completo

### 1. Crear archivo `.env`
```bash
cp env.example .env
```

### 2. Editar `.env` con valores reales
```properties
# Base de datos
DB_URL=jdbc:postgresql://localhost:5434/parking
DB_USERNAME=usuario_real
DB_PASSWORD=contraseña_real

# JWT
JWT_SECRET=mi_clave_secreta_muy_larga_y_segura

# Email
EMAIL_USERNAME=mi-email@gmail.com
EMAIL_PASSWORD=mi-password-de-aplicacion
```

### 3. Ejecutar aplicación
```bash
./mvnw spring-boot:run
```

### 4. Verificar funcionamiento
```bash
curl http://localhost:8080/actuator/health
```

Esta configuración simplificada hace que la aplicación sea más fácil de mantener, configurar y desplegar en cualquier entorno.
