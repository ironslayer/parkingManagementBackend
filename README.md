# Spring Boot Web Template 🚀

[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://openjdk.java.net/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.4.7-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![License](https://img.shields.io/badge/License-Commercial-red.svg)](LICENSE.txt)


### Descripción General 🌟

Una plantilla completa de Spring Boot que implementa patrones de arquitectura modernos y mejores prácticas. Esta
plantilla proporciona una base sólida para construir microservicios escalables y mantenibles.

Además, incluye una aplicación de línea de comandos para que puedas tener preconfiguradas dependencias esenciales a tus
proyectos
existentes fácilmente.

### Características Principales 🔑

- Arquitectura Hexagonal
- Autenticación JWT
- Documentación API con OpenAPI/Swagger
- Containerización con Docker Compose
- Stack de Observabilidad con Dashboard preconfigurado (Prometheus, Grafana, Loki, Tempo)
- Pruebas de Integración Modulares
- Caché con Redis
- PostgresSQL con Soporte Vector
- Aplicación de línea de comandos

### Tecnologías & Dependencias 🛠️

- Java 21
- Spring Boot 3.4.7
- Spring Security
- Spring Data JPA
- PostgresSQL
- Redis
- Docker & Docker Compose
- Prometheus
- Grafana
- Loki
- Tempo
- JUnit 5
- OpenAPI/Swagger

### Instalación & Puesta en Marcha 🚀

1. Clona el repositorio:
   ```bash
   git clone https://github.com/ironslayer/spring-boot-starter-template.git
   cd spring-boot-web-starter-template
   ```
2. Configura las variables de entorno si es necesario.
3. Instala las dependencias
   ```bash
   mvn clean install
   ```
4. Inicia los servicios:
   ```bash
   docker-compose up -d
   ```
5. Ejecuta la aplicación:
   ```bash
   ./mvnw spring-boot:run
   ```

### Arquitectura del proyecto

Este proyecto utiliza el patrón Mediator para una separación clara de responsabilidades y facilitar la extensibilidad.

Cada operación se representa por una combinación de:

Request → Objeto que representa la entrada del usuario.

Handler → Lógica de negocio que procesa el Request.

Response → Resultado devuelto por el handler.

Este patrón se implementa gracias a una capa personalizada que enruta cada request a su handler correspondiente.

Esto permite tener controladores extremadamente delgados y lógica desacoplada.

### Tests de integración

Cada funcionalidad (por ejemplo, usuarios, productos...) tiene su propia clase de tests de integración, con un test por
método expuesto (GET, POST, etc.).

Los tests se apoyan en la anotación @Sql para preparar el estado de la base de datos antes y después de cada test.

Esto asegura independencia total entre los tests y resultados reproducibles.

   ```
@Sql(scripts = "/it/user/delete/data.sql", executionPhase = BEFORE_TEST_METHOD)
@Sql(scripts = "/it/data-cleanup.sql", executionPhase = AFTER_TEST_METHOD)
   ```

### Interfaces disponibles

- OpenAPI (documentación de la API): http://localhost:8080/swagger-ui.html
- Grafana (monitorización): http://localhost:3000
- Loki (logs centralizados): http://localhost:3100
- Prometheus (métricas): http://localhost:9090




