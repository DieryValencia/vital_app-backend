# VitalApp Backend

## 📋 Descripción del Proyecto

VitalApp es una aplicación de salud digital diseñada para facilitar el proceso de triaje médico pre-hospitalario. El sistema permite gestionar pacientes, realizar triajes médicos, programar citas y enviar notificaciones, optimizando el flujo de trabajo en centros de atención médica.

### 🎯 Objetivo Principal

Proporcionar una plataforma digital que mejore la eficiencia del proceso de triaje médico, permitiendo una evaluación inicial rápida y precisa de los pacientes antes de su atención médica especializada.

## ✨ Características Principales

### 👥 Gestión de Pacientes
- Registro completo de información de pacientes
- Búsqueda y filtrado avanzado
- Gestión de estados activos/inactivos
- Información de contacto de emergencia

### 🏥 Sistema de Triaje Médico
- Evaluación de signos vitales (presión arterial, frecuencia cardíaca, temperatura, saturación de oxígeno)
- Clasificación por niveles de severidad
- Recomendaciones de acción médica
- Seguimiento del estado del triaje

### 📅 Gestión de Citas
- Programación de citas médicas
- Seguimiento de estados de citas
- Cancelación y reagendamiento
- Visualización de citas próximas

### 🔔 Sistema de Notificaciones
- Notificaciones en tiempo real
- Marcado de lecturas
- Gestión de prioridades
- Eliminación automática de notificaciones expiradas

### 🔐 Autenticación y Seguridad
- Autenticación JWT
- Roles de usuario
- Endpoints protegidos
- Refresh tokens

## 🛠️ Tecnologías Utilizadas

### Backend
- **Java 17**
- **Spring Boot 3.5.5**
- **Spring Security** - Autenticación y autorización
- **Spring Data JPA** - Acceso a datos
- **PostgreSQL** - Base de datos
- **JWT** - Tokens de autenticación
- **Lombok** - Reducción de código boilerplate

### Documentación y Testing
- **SpringDoc OpenAPI** - Documentación Swagger
- **JUnit** - Testing unitario
- **JaCoCo** - Cobertura de código
- **H2 Database** - Base de datos para tests

### Dependencias Principales
```xml
- spring-boot-starter-data-jpa
- spring-boot-starter-security
- spring-boot-starter-validation
- spring-boot-starter-web
- postgresql
- jjwt-api (0.12.3)
- springdoc-openapi-starter-webmvc-ui (2.7.0)
```

## 🏗️ Arquitectura del Sistema

### Estructura del Proyecto
```
src/main/java/com/vitalapp/vital_app_backend/
├── config/          # Configuraciones de Spring
├── controller/      # Controladores REST
├── dto/            # Objetos de Transferencia de Datos
├── event/          # Sistema de eventos
├── exception/      # Manejo de excepciones
├── mapper/         # Mapeadores de entidades
├── model/          # Entidades JPA
├── repository/     # Repositorios de datos
└── service/        # Lógica de negocio
```

### Patrones de Diseño Implementados
- **MVC (Model-View-Controller)** - Separación de responsabilidades
- **Repository Pattern** - Abstracción del acceso a datos
- **Service Layer** - Lógica de negocio centralizada
- **DTO Pattern** - Transferencia segura de datos
- **Observer Pattern** - Sistema de eventos para notificaciones

### Módulos Principales

#### 1. Autenticación (`/api/auth`)
- `POST /api/auth/register` - Registro de usuarios
- `POST /api/auth/login` - Inicio de sesión
- `POST /api/auth/refresh` - Renovación de tokens

#### 2. Pacientes (`/api/patients`)
- Gestión completa de CRUD
- Búsqueda y filtrado avanzado
- Paginación y ordenamiento

#### 3. Triajes (`/api/triages`)
- Creación y actualización de triajes
- Clasificación por severidad
- Seguimiento por paciente

#### 4. Citas (`/api/appointments`)
- Programación y gestión de citas
- Estados de citas (programada, completada, cancelada)

#### 5. Notificaciones (`/api/notifications`)
- Sistema de notificaciones push
- Gestión de lecturas y prioridades

## 🚀 Instalación y Configuración

### Prerrequisitos
- **Java 17** o superior
- **Maven 3.6+**
- **PostgreSQL 12+**

### Configuración de la Base de Datos
1. Crear base de datos PostgreSQL:
```sql
CREATE DATABASE vitalapp;
```

2. Configurar credenciales en `application.properties`:
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/vitalapp
spring.datasource.username=tu_usuario
spring.datasource.password=tu_contraseña
```

### Instalación
1. Clonar el repositorio:
```bash
git clone https://github.com/tu-usuario/vitalapp-backend.git
cd vitalapp-backend
```

2. Compilar el proyecto:
```bash
mvn clean compile
```

3. Ejecutar la aplicación:
```bash
mvn spring-boot:run
```

La aplicación estará disponible en `http://localhost:8080`

## 📖 Uso de la API

### Autenticación
Para acceder a los endpoints protegidos, primero obtén un token JWT:

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "tu_usuario",
    "password": "tu_contraseña"
  }'
```

### Ejemplos de Uso

#### Crear un Paciente
```bash
curl -X POST http://localhost:8080/api/patients \
  -H "Authorization: Bearer TU_TOKEN_JWT" \
  -H "Content-Type: application/json" \
  -d '{
    "fullName": "Diery Valencia",
    "documentNumber": "12345678",
    "birthDate": "1990-01-15",
    "phone": "+1234567890",
    "gender": "MALE"
  }'
```

#### Realizar un Triaje
```bash
curl -X POST http://localhost:8080/api/triages \
  -H "Authorization: Bearer TU_TOKEN_JWT" \
  -H "Content-Type: application/json" \
  -d '{
    "patientId": 1,
    "symptoms": "Dolor de cabeza intenso, náuseas",
    "bloodPressure": "140/90",
    "heartRate": 85,
    "temperature": 37.2,
    "oxygenSaturation": 98,
    "severityLevel": 3,
    "recommendedAction": "Consulta inmediata con médico general"
  }'
```

## 📚 Documentación de la API

La documentación completa de la API está disponible a través de Swagger UI:

- **URL**: `http://localhost:8080/swagger-ui.html`
- **Especificación OpenAPI**: `http://localhost:8080/api-docs`

### Características de la Documentación
- Descripciones detalladas de todos los endpoints
- Ejemplos de requests y responses
- Modelos de datos interactivos
- Posibilidad de probar los endpoints directamente desde el navegador

## 🧪 Testing

### Ejecutar Tests
```bash
mvn test
```

### Cobertura de Código
```bash
mvn jacoco:report
```
Los reportes de cobertura estarán disponibles en `target/site/jacoco/index.html`

### Tests Incluidos
- Tests unitarios para servicios
- Tests de integración para controladores
- Tests de repositorios con base de datos H2


### Estándares de Código
- Seguir las convenciones de Java
- Usar Lombok para reducir boilerplate
- Mantener cobertura de tests > 30%
- Documentar endpoints con OpenAPI annotations



## 🔄 Versiones

- **v1.0.0** - Primera versión estable con funcionalidades básicas de triaje médico

---

*VitalApp - Salud Digital para un Triaje Eficiente*