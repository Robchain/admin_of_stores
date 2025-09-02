# Store Admin System

Sistema de administración de tiendas desarrollado con **React + TypeScript** en el frontend y **Spring Boot + Java 17** en el backend, con base de datos **SQL Server**.

## 🏗️ Arquitectura del Proyecto

```
├── store_admin_backend/     # Backend - Spring Boot (Java 17)
└── store_admin_frontend/    # Frontend - React + TypeScript + Vite
```

## 🚀 Características Principales

### Backend (Spring Boot)
- **Java 17** con Spring Boot 3.5.5
- **Spring Security** con autenticación JWT
- **JPA/Hibernate** para manejo de datos
- **SQL Server** como base de datos
- **BCrypt** para encriptación de contraseñas
- API REST completa para:
  - Autenticación de usuarios
  - Gestión de locales/tiendas
  - Administración de productos
  - Control de inventario (stock)
  - Sistema de ventas
  - Dashboard con estadísticas

### Frontend (React)
- **React 19** con **TypeScript**
- **Vite** como bundler
- **Redux Toolkit** para manejo de estado
- **React Router** para navegación
- **Tailwind CSS v4** para estilos
- **Axios** para peticiones HTTP

## 📋 Requisitos Previos

### Para el Backend:
- **Java 17** (JDK 17+)
- **Maven 3.6+**
- **SQL Server** (local o remoto)

### Para el Frontend:
- **Node.js 18+**
- **npm** o **yarn**

## ⚙️ Configuración y Instalación

### 1. Configuración del Backend

#### Paso 1: Clonar y acceder al proyecto
```bash
git clone <tu-repositorio>
cd store_admin_backend
```

#### Paso 2: Configurar la base de datos
Crear un archivo `application.properties` en `src/main/resources/`:

```properties
# Configuración de la base de datos SQL Server
spring.datasource.url=jdbc:sqlserver://localhost:1433;databaseName=store_admin_db;encrypt=true;trustServerCertificate=true
spring.datasource.username=tu_usuario
spring.datasource.password=tu_contraseña
spring.datasource.driver-class-name=com.microsoft.sqlserver.jdbc.SQLServerDriver

# JPA/Hibernate
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.SQLServerDialect

# JWT Configuration
jwt.secret=tu_jwt_secret_key_muy_segura_de_al_menos_32_caracteres
jwt.expiration=86400000

# Server
server.port=8080

# CORS (para desarrollo)
spring.web.cors.allowed-origins=http://localhost:5173
```

#### Paso 3: Instalar dependencias y ejecutar
```bash
# Con Maven Wrapper (recomendado)
./mvnw clean install
./mvnw spring-boot:run

# O con Maven instalado globalmente
mvn clean install
mvn spring-boot:run
```

El backend estará disponible en: `http://localhost:8080`

### 2. Configuración del Frontend

#### Paso 1: Acceder al directorio del frontend
```bash
cd store_admin_frontend
```

#### Paso 2: Instalar dependencias
```bash
npm install
# o
yarn install
```

#### Paso 3: Configurar variables de entorno (opcional)
Crear un archivo `.env` en la raíz del frontend:

```env
VITE_API_BASE_URL=http://localhost:8080/api
```

#### Paso 4: Ejecutar el proyecto
```bash
npm run dev
# o
yarn dev
```

El frontend estará disponible en: `http://localhost:5173`

## 🗄️ Configuración de Base de Datos

### SQL Server Setup
1. Instalar SQL Server (Express es suficiente)
2. Crear una base de datos llamada `store_admin_db`
3. El proyecto creará automáticamente las tablas necesarias al ejecutarse por primera vez

### Estructura de tablas principales:
- `usuarios` - Gestión de usuarios y autenticación
- `locales` - Información de tiendas/locales
- `productos` - Catálogo de productos
- `productos_locales` - Inventario por local
- `ventas` - Registro de ventas
- `detalles_venta` - Detalles de cada venta

## 🚀 Scripts Disponibles

### Backend
```bash
./mvnw spring-boot:run    # Ejecutar aplicación
./mvnw clean install     # Compilar y instalar dependencias
./mvnw test              # Ejecutar pruebas
```

### Frontend
```bash
npm run dev              # Servidor de desarrollo
npm run build            # Construir para producción
npm run preview          # Previsualizar build de producción
npm run lint             # Ejecutar ESLint
```

## 🔧 Tecnologías Utilizadas

### Backend
- Java 17
- Spring Boot 3.5.5
- Spring Security
- Spring Data JPA
- JWT (JSON Web Tokens)
- SQL Server
- Maven

### Frontend
- React 19
- TypeScript
- Vite
- Redux Toolkit
- React Router DOM
- Tailwind CSS v4
- Axios

## 📝 Notas de Desarrollo

- El proyecto usa **JWT** para autenticación
- **CORS** está configurado para desarrollo local
- Las contraseñas se encriptan con **BCrypt**
- El frontend usa **Redux Toolkit** para manejo de estado global
- **Tailwind CSS v4** para estilos modernos y responsivos

**Desarrollado por:** Robert Román  
**Version:** 0.0.1-SNAPSHOT