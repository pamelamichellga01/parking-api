package com.nelumbo.parking.utils;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Utilidad para manejar variables de entorno y configuración
 */
@Slf4j
@Component
@Getter
public class EnvironmentUtils {

    @Value("${spring.profiles.active:default}")
    private String activeProfile;

    @Value("${app.environment:development}")
    private String environment;

    /**
     * Obtiene una variable de entorno del sistema con valor por defecto
     * @param key nombre de la variable
     * @param defaultValue valor por defecto si no existe
     * @return valor de la variable o el valor por defecto
     */
    public static String getSystemEnv(String key, String defaultValue) {
        String value = System.getenv(key);
        return value != null ? value : defaultValue;
    }

    /**
     * Obtiene una variable de entorno del sistema como entero
     * @param key nombre de la variable
     * @param defaultValue valor por defecto si no existe o no es un número
     * @return valor de la variable como entero o el valor por defecto
     */
    public static Integer getSystemEnvAsInt(String key, Integer defaultValue) {
        try {
            String value = System.getenv(key);
            return value != null ? Integer.parseInt(value) : defaultValue;
        } catch (NumberFormatException e) {
            log.warn("No se pudo convertir la variable de entorno {} a entero: {}", key, e.getMessage());
            return defaultValue;
        }
    }

    /**
     * Verifica si una variable de entorno existe
     * @param key nombre de la variable
     * @return true si existe, false en caso contrario
     */
    public static boolean hasSystemEnv(String key) {
        return System.getenv(key) != null;
    }

    /**
     * Clase interna para configuración de base de datos
     */
    public static class DatabaseConfig {
        private final String url;
        private final String username;
        private final String password;
        private final String driverClassName;

        private DatabaseConfig(String url, String username, String password, String driverClassName) {
            this.url = url;
            this.username = username;
            this.password = password;
            this.driverClassName = driverClassName;
        }

        public static Builder builder() {
            return new Builder();
        }

        public String getUrl() { return url; }
        public String getUsername() { return username; }
        public String getPassword() { return password; }
        public String getDriverClassName() { return driverClassName; }

        public static class Builder {
            private String url;
            private String username;
            private String password;
            private String driverClassName;

            public Builder url(String url) {
                this.url = url;
                return this;
            }

            public Builder username(String username) {
                this.username = username;
                return this;
            }

            public Builder password(String password) {
                this.password = password;
                return this;
            }

            public Builder driverClassName(String driverClassName) {
                this.driverClassName = driverClassName;
                return this;
            }

            public DatabaseConfig build() {
                return new DatabaseConfig(url, username, password, driverClassName);
            }
        }
    }
}
