# Guía de organización y ejecución de TuDentaria

El código se organiza en dos aplicaciones principales: `backend/` para Java y Spring Boot y `frontend/` para Angular. `docs/`, `postman/` y la configuración de la raíz se comparten entre ambas.

## Ejecutar desde Visual Studio Code

Abrir la carpeta raíz TuDentaria. En la primera terminal:

```sh
cd backend
./mvnw spring-boot:run -Dspring-boot.run.profiles=local
```

En Windows utilizar `mvnw.cmd`. Esperar a que Tomcat indique el puerto 8080 y aparezca `Started TudentariaApplication`.

En la segunda terminal, desde la raíz:

```sh
cd frontend
npm ci
npm start
```

Entrar a <http://localhost:4200>. El backend se encuentra en <http://localhost:8080>. Las tareas de VS Code **TuDentaria Backend**, **TuDentaria Frontend** y **TuDentaria Iniciar ambos** realizan estos mismos pasos desde sus carpetas correspondientes.

## Puertos

8080 es el valor predeterminado de Spring Boot; 4200 es el de Angular. Si 8080 está ocupado, elegir 8081 explícitamente:

```sh
# Desde backend/
./mvnw spring-boot:run -Dspring-boot.run.profiles=local -Dspring-boot.run.arguments=--server.port=8081
```

```sh
# Desde frontend/
API_TARGET=http://127.0.0.1:8081 npm start
```

En PowerShell establecer `$env:API_TARGET="http://127.0.0.1:8081"` antes de `npm start`. Postman debe usar el mismo puerto que indique Tomcat. No escribir el método HTTP dentro del campo URL.

## Datos y autenticación

El perfil local utiliza H2 en memoria con `create-drop`. Los datos se reinician al detener Spring. Administrador de demostración: `admin@tudentaria.com` / `Admin123!`.

La cuenta se crea mediante el inicializador del backend; en una base persistente, si ya existe, su contraseña se conserva. El hash del script SQL de referencia no se modificó. No se accedió ni se alteró MySQL al separar las carpetas.

La API utiliza JWT. Su clave es efímera si no se establece `JWT_SECRET`; por eso los tokens dejan de funcionar al reiniciar el backend. El manejo de endpoints se mantiene y puede revisarse después de esta reorganización.

## Fotografías y vistas anteriores

Las fotos cargadas permanecen en `backend/uploads/` y se sirven mediante `/uploads/`. Iniciar Spring desde `backend/` o mediante la configuración actualizada de VS Code garantiza que se busquen en el directorio correcto.

Las plantillas y recursos originales Thymeleaf permanecen en `backend/src/main/resources/`. La interfaz Angular está en `frontend/src/` y sus recursos en `frontend/public/`. Las vistas antiguas se conservaron para la transición; no deben confundirse con los componentes Angular.

## Pruebas y contenedores

Ejecutar `./mvnw test` desde `backend/` y `npm run build` desde `frontend/`.

Compose se ejecuta desde la raíz y construye las dos carpetas de forma independiente. Copiar `.env.example` como `.env`, completar sus valores y ejecutar `docker compose up --build`. Detener los procesos que ocupen 8080/4200 antes de iniciar los contenedores. La composición es para demostración con H2 en memoria; solo las fotografías tienen volumen persistente.

La configuración de depuración Java de VS Code utiliza `backend/` como directorio de trabajo y conserva la referencia a `.env` en la raíz para quien configure allí sus variables. Maven ejecutado desde terminal no carga ese archivo automáticamente; exportar las variables necesarias en la terminal o usar la configuración correspondiente.

## Documentación intermedia

El plan y sus evidencias conservan los resultados originales de la migración. Las capturas previas pueden mostrar 8081; ese puerto fue una alternativa cuando 8080 estaba ocupado. Para la estructura actual consultar los README de la raíz, backend y frontend.
