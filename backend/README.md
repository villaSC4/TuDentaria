# Backend de TuDentaria

Esta carpeta contiene la aplicación Spring Boot y su compilación Maven. Abrir una terminal **dentro de backend/** para ejecutar:

```sh
./mvnw spring-boot:run -Dspring-boot.run.profiles=local
```

Puerto predeterminado: 8080. La configuración está en `src/main/resources/application.properties` y `application-local.properties`. Los endpoints y la autenticación no cambiaron con la separación de carpetas.

## Localizar el código

- `src/main/java/com/utp/tudentaria/controller/`: endpoints REST y controladores de las vistas antiguas.
- `src/main/java/com/utp/tudentaria/service/`: reglas de negocio y operaciones.
- `src/main/java/com/utp/tudentaria/repository/`: acceso a datos.
- `src/main/java/com/utp/tudentaria/model/`: entidades JPA.
- `src/main/java/com/utp/tudentaria/config/`: seguridad, JWT y recursos estáticos.
- `src/test/java/`: pruebas del backend.
- `uploads/`: fotografías cargadas por los usuarios.
- `data/`: archivos locales conservados; el perfil local actual usa H2 en memoria.

Las carpetas `src/main/resources/templates/` y `static/` pertenecen a la versión Thymeleaf conservada. El frontend Angular que se modifica actualmente está en `../frontend/`; no se deben confundir ambas interfaces.

No mover las fotografías a `frontend/public/`: `/uploads/` es servido por Spring y reenviado por el proxy de Angular. Ejecutar Spring desde esta carpeta mantiene la resolución correcta de los archivos.

Para usar el puerto alternativo 8081:

```sh
./mvnw spring-boot:run -Dspring-boot.run.profiles=local -Dspring-boot.run.arguments=--server.port=8081
```

En ese caso iniciar Angular con `API_TARGET=http://127.0.0.1:8081 npm start` desde `../frontend/`.

El perfil `local` usa H2 en memoria con `create-drop`; no usarlo con datos reales. La configuración MySQL existente se conserva. El script SQL se mantiene como referencia y no se ejecutó durante esta reorganización.
