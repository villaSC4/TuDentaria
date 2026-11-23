# TuDentaria

Proyecto dividido en dos aplicaciones: **backend/** (Spring Boot) y **frontend/** (Angular).

```text
TuDentaria/
├── backend/
│   ├── src/main/java/        Controladores, servicios, seguridad y entidades
│   ├── src/main/resources/  Configuración Spring y vistas Thymeleaf conservadas
│   ├── src/test/            Pruebas Java
│   ├── pom.xml              Dependencias y compilación Maven
│   ├── mvnw / mvnw.cmd      Maven Wrapper
│   ├── uploads/             Fotografías subidas
│   ├── data/                Archivos de base local conservados
│   ├── tudentaria_schema_complete.sql
│   └── Dockerfile
├── frontend/
│   ├── src/app/core/        Servicios, JWT, interceptor y guards
│   ├── src/app/shared/      Encabezado, pie y errores de formularios
│   ├── src/app/features/    Páginas por funcionalidad
│   ├── public/              Imágenes y video de Angular
│   ├── e2e/                 Casos de navegador
│   ├── package.json         Dependencias y comandos npm
│   └── Dockerfile
├── docs/                    Documentación y evidencias
├── postman/                 Recursos de Postman existentes
├── .vscode/                 Ejecución y depuración de ambas aplicaciones
└── compose.yaml             Ejecución conjunta opcional
```

## Ejecutar en desarrollo

Requisitos: JDK 17 o 21 y Node compatible con Angular 21 (20.19+, 22.12+ o 24).

**Terminal 1, desde la raíz:**

```sh
cd backend
./mvnw spring-boot:run -Dspring-boot.run.profiles=local
```

En Windows utilizar `mvnw.cmd` en lugar de `./mvnw`.

**Terminal 2, desde la raíz:**

```sh
cd frontend
npm ci
npm start
```

- Angular: <http://localhost:4200>
- Spring Boot: <http://localhost:8080>
- El proxy de Angular apunta a 8080 por defecto. Confirmar el puerto de Tomcat en la terminal.

El perfil `local` utiliza **H2 en memoria** con datos de demostración; reiniciar elimina los cambios de esa base. El administrador de ese perfil es `admin@tudentaria.com` / `Admin123!`.

No es necesario instalar Tomcat aparte: Spring Boot incluye su servidor embebido. `npm start` inicia únicamente Angular.

## Visual Studio Code

Abrir la raíz `TuDentaria/`. En **Terminal → Ejecutar tarea**, elegir **TuDentaria Iniciar ambos**, o ejecutar por separado **TuDentaria Backend** y **TuDentaria Frontend**. La configuración Java de depuración utiliza `backend/` como directorio de trabajo para localizar correctamente las fotografías.

## Pruebas y compilación

Desde la raíz, en terminales separadas:

```sh
cd backend
./mvnw test
```

```sh
cd frontend
npm run build
```

El JAR se genera en `backend/target/` al ejecutar `./mvnw package`. Angular genera su compilación en `frontend/dist/tudentaria/browser/`.

## Contenedores

Desde la raíz, copiar `.env.example` a `.env`, completar sus valores y ejecutar `docker compose up --build`. Compose construye `backend/` y `frontend/` por separado. Los puertos de acceso son 8080 para Spring y 4200 para Angular. Detener previamente los servidores de desarrollo que utilicen esos puertos.

Es un entorno de demostración con H2 en memoria y volumen para las fotografías, no una configuración de persistencia productiva. Si el despliegue externo necesita un directorio raíz de servicio, seleccionar `backend` o `frontend` según la aplicación.

## Documentación

- [Guía de ejecución y organización](docs/GUIA_MIGRACION_ANGULAR.md)
- [Plan de desarrollo intermedio y evidencias](docs/PLAN_DE_DESARROLLO_INTERMEDIO.md)
- [Colección de referencia Postman](docs/TuDentaria-Angular.postman_collection.json)

Las capturas y ejemplos de la documentación intermedia corresponden a la ejecución original; algunas utilizan el puerto alternativo 8081. Adaptar la URL al puerto efectivo del backend.
