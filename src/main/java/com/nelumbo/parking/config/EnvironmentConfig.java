package com.nelumbo.parking.config;

import com.nelumbo.parking.utils.EnvironmentUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import jakarta.annotation.PostConstruct;

/**
 * Configuración para manejar variables de entorno
 */
@Slf4j
@Configuration
@PropertySource(value = "classpath:application.properties", encoding = "UTF-8")
public class EnvironmentConfig {

    private  static final String APP_ENVIRONMENT = "APP_ENVIRONMENT";
    private  static final String DEVELOPMENT = "development";

    @PostConstruct
    public void init() {
        log.info("Inicializando configuración de entorno...");
        log.info("Perfil activo: {}", EnvironmentUtils.getSystemEnv("SPRING_PROFILES_ACTIVE", "default"));
        log.info("Entorno: {}", EnvironmentUtils.getSystemEnv(APP_ENVIRONMENT, DEVELOPMENT));
        
        // Validar variables críticas si es necesario
        String environment = EnvironmentUtils.getSystemEnv(APP_ENVIRONMENT, DEVELOPMENT);
        if ("production".equalsIgnoreCase(environment)) {
            log.info("Ejecutando en modo PRODUCCIÓN - validando variables críticas...");
            // Aquí puedes agregar validaciones específicas para producción
        }
    }

    /**
     * Bean para configuración de la aplicación
     */
    @Bean
    @ConfigurationProperties(prefix = "app")
    public AppProperties appProperties() {
        return new AppProperties();
    }

    /**
     * Bean para configuración de seguridad
     */
    @Bean
    @ConfigurationProperties(prefix = "app.security")
    public SecurityProperties securityProperties() {
        return new SecurityProperties();
    }

    /**
     * Bean para configuración de base de datos
     */
    @Bean
    @ConfigurationProperties(prefix = "spring.datasource")
    public DatabaseProperties databaseProperties() {
        return new DatabaseProperties();
    }

    /**
     * Propiedades de la aplicación
     */
    public static class AppProperties {
        private String environment = EnvironmentUtils.getSystemEnv(APP_ENVIRONMENT, DEVELOPMENT);
        private String name = EnvironmentUtils.getSystemEnv("APP_NAME", "parking-api");
        private String version = EnvironmentUtils.getSystemEnv("APP_VERSION", "1.0.0");
        private String timezone = EnvironmentUtils.getSystemEnv("APP_TIMEZONE", "America/Bogota");
        private String locale = EnvironmentUtils.getSystemEnv("APP_LOCALE", "es_CO");

        // Getters y Setters
        public String getEnvironment() { return environment; }
        public void setEnvironment(String environment) { this.environment = environment; }
        
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        
        public String getVersion() { return version; }
        public void setVersion(String version) { this.version = version; }
        
        public String getTimezone() { return timezone; }
        public void setTimezone(String timezone) { this.timezone = timezone; }
        
        public String getLocale() { return locale; }
        public void setLocale(String locale) { this.locale = locale; }
    }

    /**
     * Propiedades de seguridad
     */
    public static class SecurityProperties {
        private String jwtSecret = EnvironmentUtils.getSystemEnv("JWT_SECRET", "ZmQ0ZGI5NjQ0MDQwY2I4MjMxY2Y3ZmI3MjdhN2ZmYTA=");
        private Long jwtExpiration = EnvironmentUtils.getSystemEnvAsInt("JWT_EXPIRATION", 21600000).longValue();
        private String jwtAlgorithm = EnvironmentUtils.getSystemEnv("JWT_ALGORITHM", "HS256");
        private Integer maxLoginAttempts = EnvironmentUtils.getSystemEnvAsInt("MAX_LOGIN_ATTEMPTS", 5);
        private Integer lockoutDuration = EnvironmentUtils.getSystemEnvAsInt("LOCKOUT_DURATION", 15);

        // Getters y Setters
        public String getJwtSecret() { return jwtSecret; }
        public void setJwtSecret(String jwtSecret) { this.jwtSecret = jwtSecret; }
        
        public Long getJwtExpiration() { return jwtExpiration; }
        public void setJwtExpiration(Long jwtExpiration) { this.jwtExpiration = jwtExpiration; }
        
        public String getJwtAlgorithm() { return jwtAlgorithm; }
        public void setJwtAlgorithm(String jwtAlgorithm) { this.jwtAlgorithm = jwtAlgorithm; }
        
        public Integer getMaxLoginAttempts() { return maxLoginAttempts; }
        public void setMaxLoginAttempts(Integer maxLoginAttempts) { this.maxLoginAttempts = maxLoginAttempts; }
        
        public Integer getLockoutDuration() { return lockoutDuration; }
        public void setLockoutDuration(Integer lockoutDuration) { this.lockoutDuration = lockoutDuration; }
    }

    /**
     * Propiedades de base de datos
     */
    public static class DatabaseProperties {
        private String url = EnvironmentUtils.getSystemEnv("DB_URL", "jdbc:postgresql://localhost:5434/parking");
        private String username = EnvironmentUtils.getSystemEnv("DB_USERNAME", "admin");
        private String password = EnvironmentUtils.getSystemEnv("DB_PASSWORD", "12345678");
        private String driverClassName = EnvironmentUtils.getSystemEnv("DB_DRIVER", "org.postgresql.Driver");
        private Integer maxPoolSize = EnvironmentUtils.getSystemEnvAsInt("DB_MAX_POOL_SIZE", 10);
        private Integer minIdle = EnvironmentUtils.getSystemEnvAsInt("DB_MIN_IDLE", 5);
        private Integer connectionTimeout = EnvironmentUtils.getSystemEnvAsInt("DB_CONNECTION_TIMEOUT", 30000);

        // Getters y Setters
        public String getUrl() { return url; }
        public void setUrl(String url) { this.url = url; }
        
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
        
        public String getDriverClassName() { return driverClassName; }
        public void setDriverClassName(String driverClassName) { this.driverClassName = driverClassName; }
        
        public Integer getMaxPoolSize() { return maxPoolSize; }
        public void setMaxPoolSize(Integer maxPoolSize) { this.maxPoolSize = maxPoolSize; }
        
        public Integer getMinIdle() { return minIdle; }
        public void setMinIdle(Integer minIdle) { this.minIdle = minIdle; }
        
        public Integer getConnectionTimeout() { return connectionTimeout; }
        public void setConnectionTimeout(Integer connectionTimeout) { this.connectionTimeout = connectionTimeout; }
    }
}
