
# Sistema Web de Gestión de Cartera

## Descripción

Aplicación web orientada a optimizar el registro,
consulta, seguimiento y validación de cobros en
una empresa del sector cosmético.

Proyecto académico de Ingeniería de Software,
desarrollado para la asignatura Desarrollo de
Aplicaciones Web.

## Funcionalidades

- Autenticación mediante JWT.
- Gestión de clientes.
- Registro y consulta de cobros.
- Validación y rechazo de cobros.
- Dashboard de indicadores.
- Administración de usuarios y roles.
- Control de acceso según el perfil del usuario.

## Tecnologías utilizadas

### Backend
- Java 21 LTS.
- Spring Boot.
- Spring Security.
- Spring Data JPA.
- Maven.
- Swagger / OpenAPI.

### Frontend
- React.
- Vite.
- Axios.
- React Router.
- Context API.
- Bootstrap 5.

### Base de datos
- PostgreSQL.

### Despliegue
- Docker.
- Render.
- GitHub.

## Estructura del proyecto

- backend/: API REST y lógica de negocio.
- frontend/: interfaz de usuario en React.

## Ejecución local

### Backend

Crear una base de datos PostgreSQL llamada cartera_db.

Configurar las variables de entorno:

- DB_URL
- DB_USERNAME
- DB_PASSWORD
- JWT_SECRET

Ejecutar desde la carpeta backend:

    .\mvnw.cmd spring-boot:run

### Frontend

Configurar VITE_API_URL en el archivo .env,
tomando como referencia .env.example.

Ejecutar desde la carpeta frontend:

    npm install
    npm run dev

## Documentación de la API

http://localhost:8080/swagger-ui.html

## Estado del proyecto

Proyecto académico con datos ficticios.
No procesa transacciones financieras reales.
