# Sistema de Gestión de Incidencias (SGI)

Sistema web desarrollado en Java con Spring Boot para la gestión de incidencias de docentes y soporte TI.

## Características

- Inicio de sesión de usuarios
- Registro de incidencias
- Validación de incidencias duplicadas
- Asignación de prioridad
- Panel para docentes
- Panel para personal TI
- Panel de administración
- Gestión del estado de incidencias
- Generación de reportes en Excel

---

## Tecnologías utilizadas

- Java 17+
- Spring Boot
- Maven
- Thymeleaf
- MySQL
- HTML, CSS y JavaScript

---

## Estructura del proyecto

```bash
Proyecto-SGI-main/
│
├── src/main/java/com/sgi/
│   ├── controller/
│   ├── model/
│   ├── repository/
│   ├── service/
│   └── SistemaGestionIncidencias.java
│
├── src/main/resources/
│   ├── static/
│   ├── templates/
│   └── application.properties
│
└── pom.xml
```

---

## Requisitos

Antes de ejecutar el proyecto asegúrate de tener instalado:

- Java JDK 17 o superior
- Maven
- MySQL Server
- MySQL Workbench (opcional)
- Visual Studio Code o NetBeans

---

## Configuración de la base de datos

### 1. Crear la base de datos en MySQL

```sql
CREATE DATABASE sgi;
```

### 2. Configurar el archivo application.properties

Ubicación:

```bash
src/main/resources/application.properties
```

Ejemplo:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/sgi
spring.datasource.username=root
spring.datasource.password=TU_PASSWORD

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
```

---

## Cómo ejecutar el proyecto

### Opción 1: Desde Visual Studio Code

1. Abrir la carpeta del proyecto
2. Instalar las extensiones de Java
3. Esperar que Maven cargue las dependencias
4. Ejecutar la clase:

```bash
SistemaGestionIncidencias.java
```

---

### Opción 2: Desde terminal

Abrir una terminal en la carpeta del proyecto y ejecutar:

```bash
mvn spring-boot:run
```

---

## Acceso al sistema

Abrir en el navegador:

```bash
http://localhost:8080
```

---

## Plantillas disponibles

- login.html
- nueva-incidencia.html
- panel-admin.html
- panel-docente.html
- panel-ti.html

---

## Funcionalidad del sistema

### Docente
- Registrar incidencias
- Consultar estado de incidencias

### Personal TI
- Ver incidencias asignadas
- Cambiar estado:
  - Resuelto
  - No resuelto

### Administrador
- Ver todas las incidencias
- Gestionar reportes

---

## Generación de reportes

El sistema incluye generación de reportes Excel mediante:

```java
ExcelReportService.java
```

---

## Autor

Proyecto académico desarrollado por:

- Hilasaca Torres, Kriss Angela
- Huerta Tejada, Adrian Jorge
- Peralta Caceres, Edward Robert
- Sotelo Caceres, Diego Luis

---

## Licencia

Proyecto desarrollado con fines educativos.
