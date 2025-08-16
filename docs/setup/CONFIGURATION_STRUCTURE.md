# Estructura de Configuración - Parking API

## 📋 Descripción General

La aplicación Parking API ahora utiliza una **configuración unificada y simplificada** que elimina la complejidad de múltiples perfiles y centraliza toda la configuración en un solo archivo. **Las variables de entorno se leen automáticamente del archivo `.env`** gracias a la librería `spring-dotenv`.

## 🏗️ Estructura de Archivos

```
src/main/resources/
├── application.properties          # Configuración única y completa
.env                               # Variables de entorno (valores reales)
env.example                        # Plantilla de variables de entorno
```

## 🔄 Cómo Funciona

1. **`application.properties`**: Contiene todas las configuraciones con referencias a variables de entorno
2. **`.env`**: Contiene los valores reales de las variables de entorno
3. **`spring-dotenv`**: Lee automáticamente el archivo `.env` y las hace disponibles para Spring Boot
4. **Valores por defecto**: Si una variable no está definida, se usa el valor por defecto

## 📁 Contenido de Cada Archivo

### `application.properties` - Configuración Única
Contiene **TODAS** las configuraciones de la aplicación con referencias a variables de entorno:

- ✅ **Aplicación**: Nombre, versión, timezone, locale
- ✅ **Servidor**: Puerto configurable
- ✅ **Base de datos**: URL, credenciales, pool, dialecto
- ✅ **JWT**: Secret, expiración, algoritmo
- ✅ **Scheduling**: Cron de limpieza de tokens
- ✅ **Logging**: Niveles configurables
- ✅ **Actuator**: Endpoints de monitoreo
- ✅ **Email**: Configuración SMTP completa
- ✅ **Parking**: Capacidad, tarifas, moneda
- ✅ **Reportes**: Formato, exportación, rangos
- ✅ **Cache**: Configuración de Caffeine
- ✅ **Seguridad**: Intentos de login, bloqueos
- ✅ **Testing**: Soporte para H2 y configuraciones de test

### `.env` - Variables de Entorno
Contiene los **valores reales** de las variables de entorno:

- 🔧 **Aplicación**: Entorno, nombre, versión
- 🗄️ **Base de datos**: URL, usuario, contraseña, driver
- 🔐 **JWT**: Secret, expiración, algoritmo
- 📧 **Email**: Credenciales SMTP
- ⏰ **Scheduling**: Expresión cron
- 🖥️ **Servidor**: Puerto
- 📝 **Logging**: Niveles
- 💾 **Cache**: Tamaño y expiración
- 🚗 **Parking**: Capacidad y tarifas
- 📊 **Reportes**: Configuración
- 🔒 **Seguridad**: Intentos y bloqueos

### `env.example` - Plantilla
Archivo de ejemplo que documenta todas las variables disponibles.

## 🚀 Cómo Usar

### 1. Configurar Variables de Entorno

```bash
# Copiar la plantilla
cp env.example .env

# Editar con valores reales
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

### 3. Cambiar Configuración

Para cambiar cualquier configuración:

1. **Editar `.env`**: Cambiar el valor de la variable
2. **Reiniciar**: La aplicación recarga automáticamente

## 🔧 Configuraciones Especiales

### Para Testing con H2

```properties
# En .env, cambiar estas variables:
DB_URL=jdbc:h2:mem:testdb
DB_DRIVER=org.h2.Driver
HIBERNATE_DIALECT=org.hibernate.dialect.H2Dialect
JPA_DDL_AUTO=create-drop
JPA_SHOW_SQL=false
```

### Para Producción

```properties
# En .env, ajustar para producción:
APP_ENVIRONMENT=production
LOG_LEVEL_COM_NELUMBO_PARKING=WARN
LOG_LEVEL_SPRING_SECURITY=WARN
MANAGEMENT_ENDPOINTS_WEB_EXPOSURE_INCLUDE=health,info
```

## 💡 Ventajas de Esta Configuración

1. **✅ Simplicidad**: Un solo archivo de configuración
2. **✅ Flexibilidad**: Variables de entorno para cualquier valor
3. **✅ Mantenibilidad**: Fácil de entender y modificar
4. **✅ Seguridad**: Credenciales separadas del código
5. **✅ Portabilidad**: Funciona en cualquier entorno
6. **✅ Testing**: Fácil cambiar a H2 para tests

## 🚨 Consideraciones Importantes

1. **Nunca comitear `.env`**: Contiene información sensible
2. **Siempre incluir `env.example`**: Para documentar variables
3. **Valores por defecto**: Siempre proporcionar valores de respaldo
4. **Validación**: Verificar que las variables requeridas estén definidas

## 🔍 Ejemplo de Uso

```properties
# En application.properties
spring.datasource.username=${DB_USERNAME:admin}

# En .env
DB_USERNAME=usuario_real

# Resultado: Spring Boot usa "usuario_real"
# Si .env no existe, usa "admin" como valor por defecto
```

Esta configuración simplificada hace que la aplicación sea más fácil de mantener, configurar y desplegar en cualquier entorno.
