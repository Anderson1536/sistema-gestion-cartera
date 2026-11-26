
# Sistema Web de Gestión de Cartera

## Descripción del proyecto

Aplicación web desarrollada como parte de la asignatura Desarrollo de Aplicaciones Web del programa de Ingeniería de Software.

El sistema tiene como propósito optimizar el registro, consulta, seguimiento y validación de cobros en una empresa del sector cosmético, contribuyendo a la reducción de errores de digitación y reprocesos administrativos.

## Funcionalidades principales

- Autenticación de usuarios mediante JWT.
- Control de acceso basado en roles.
- Gestión de clientes.
- Registro, consulta y actualización de cobros.
- Validación y rechazo de cobros.
- Panel de indicadores de cartera.
- Administración de usuarios.
- Validación de datos y gestión de errores.

## Tecnologías utilizadas

### Back-end

- Java 21
- Spring Boot
- Spring Data JPA
- Spring Security
- JWT
- Maven
- Swagger / OpenAPI

### Front-end

- ReactJS
- Vite
- Bootstrap 5
- Bootstrap Icons
- Axios
- React Router
- Context API

### Base de datos

- PostgreSQL

### Herramientas adicionales

- Visual Studio Code
- DBeaver
- Git y GitHub
- Docker (preparación para futuro despliegue)

## Arquitectura de la aplicación

El sistema utiliza una arquitectura que separa la interfaz
gráfica, la lógica de negocio y la persistencia de datos.

React → Axios → API REST → Spring Boot → PostgreSQL

## Estructura del repositorio

    sistema-gestion-cartera/
    ├── backend/
    ├── frontend/
    ├── .gitignore
    └── README.md

## Requisitos previos

- Java JDK 21.
- Node.js 24 LTS.
- PostgreSQL.
- Git.

## Instalación y ejecución local

Las siguientes instrucciones están orientadas a Windows
mediante PowerShell.

### 1. Clonar el repositorio

Copiar la URL del repositorio desde GitHub y ejecutar:

    git clone https://github.com/Anderson1536/sistema-gestion-cartera.git
    cd sistema-gestion-cartera

### 2. Configurar la base de datos

Crear una base de datos vacía en PostgreSQL con el nombre:

    cartera_db

Las tablas serán generadas automáticamente por Hibernate
al iniciar el back-end.

### 3. Configurar el back-end

Desde la carpeta principal del proyecto:

    cd backend

Configurar las variables de entorno en PowerShell:

    $env:DB_URL = "jdbc:postgresql://localhost:5432/cartera_db"
    $env:DB_USERNAME = "postgres"

Solicitar la contraseña de PostgreSQL:

    $credencial = Read-Host "Contraseña PostgreSQL" -AsSecureString
    $env:DB_PASSWORD = [System.Net.NetworkCredential]::new("", $credencial).Password

Generar una clave JWT:

    $bytes = New-Object byte[] 32
    $rng = [System.Security.Cryptography.RandomNumberGenerator]::Create()
    $rng.GetBytes($bytes)
    $env:JWT_SECRET = [Convert]::ToBase64String($bytes)
    $rng.Dispose()

### 4. Inicializar el usuario administrador

Para la primera ejecución, configurar:

    $env:SEED_ENABLED = "true"
    $env:ADMIN_NAME = "Administrador de cartera"
    $env:ADMIN_EMAIL = "admin@ejemplo.com"

Asignar una contraseña segura al administrador:

    $credencialAdmin = Read-Host "Contraseña administrador" -AsSecureString
    $env:ADMIN_PASSWORD = [System.Net.NetworkCredential]::new("", $credencialAdmin).Password

La contraseña debe tener como mínimo ocho caracteres y
no superar los 72 bytes en UTF-8.

Ejecutar el back-end:

    .\mvnw.cmd spring-boot:run

El sistema creará automáticamente los roles y el usuario
administrador inicial.

Después de la primera ejecución exitosa, detener el servidor
y desactivar la inicialización:

    $env:SEED_ENABLED = "false"
    Remove-Item Env:ADMIN_PASSWORD

Iniciar nuevamente el back-end:

    .\mvnw.cmd spring-boot:run

Mantener esta terminal abierta durante el uso del sistema.

### 5. Configurar y ejecutar el front-end

Abrir una segunda terminal desde la carpeta principal:

    cd frontend

Instalar las dependencias:

    npm.cmd ci

Crear el archivo de configuración local:

    Copy-Item .env.example .env

Verificar que el archivo .env contenga:

    VITE_API_URL=http://localhost:8080/api

Ejecutar el front-end:

    npm.cmd run dev

### 6. Acceder a la aplicación

Abrir en el navegador:

http://localhost:5173

Iniciar sesión utilizando el correo y la contraseña del
administrador configurado durante la inicialización.

Desde el módulo de usuarios se pueden crear cuentas de
analistas de cartera y representantes de ventas.

Los clientes y cobros de prueba pueden registrarse desde
la interfaz gráfica.

## Documentación de la API REST

La documentación de los servicios REST se encuentra
disponible mediante Swagger:

http://localhost:8080/swagger-ui.html

## Roles del sistema

| Rol | Funcionalidades |
|---|---|
| Administrador | Gestión de usuarios, clientes, cobros e indicadores. |
| Analista | Gestión de clientes, validación de cobros e indicadores. |
| Representante | Consulta de clientes, registro y seguimiento de sus propios cobros. |

## Seguridad

- Autenticación mediante JWT.
- Contraseñas protegidas mediante BCrypt.
- Autorización basada en roles.
- Validación de información.
- Gestión centralizada de excepciones.
- Configuración mediante variables de entorno.

## Estado del proyecto

La aplicación fue desarrollada y validada funcionalmente
en un entorno local.

Se realizaron configuraciones preliminares para facilitar
su futuro despliegue mediante Docker y servicios en la nube.

El despliegue efectivo en Render queda pendiente para una
fase posterior del proyecto.

## Finalidad académica

Proyecto desarrollado con fines académicos para la
asignatura Desarrollo de Aplicaciones Web.

La información utilizada durante las pruebas corresponde
a datos ficticios.
