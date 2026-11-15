# Plan de Desarrollo de Software — TuDentaria
## Versión intermedia: migración de Thymeleaf a Angular

**Fecha de verificación:** 11 de noviembre de 2026, hora de Lima.  
**Rama de trabajo:** `Cesar`.  
**Estado:** implementación local y verificación intermedia; sin despliegue.  
**Integrantes, curso y docente:** completar con los datos del equipo.

## 1. Descripción del proyecto

TuDentaria es una aplicación para una clínica dental que presenta sus servicios y especialistas, registra usuarios, recibe solicitudes de citas y permite administrar doctores, tratamientos, usuarios y estados de atención. El proyecto original utilizaba Spring Boot, Spring Security, JPA y vistas Thymeleaf con HTML, CSS, JavaScript y Bootstrap.

La versión intermedia incorpora Angular como frontend separado, conectado por HTTP/JSON al mismo backend. La migración reutiliza los servicios de negocio y los recursos visuales existentes. Las vistas Thymeleaf se mantienen para permitir una transición gradual.

## 2. Objetivos

**Objetivo general:** incorporar un frontend Angular funcional que consuma una API Spring Boot y cubra los criterios de organización, rutas, autenticación, servicios, reutilización, validaciones y documentación de la rúbrica.

**Objetivos específicos:**

1. Mantener entidades, repositorios y reglas de negocio del backend existente.
2. Agregar endpoints JSON para registro, autenticación, perfil, citas y administración.
3. Autenticar las peticiones protegidas mediante JWT y validar permisos en el servidor.
4. Organizar Angular por funcionalidades, con componentes compartidos y rutas privadas.
5. Conservar Bootstrap, el contenido y la identidad visual de TuDentaria.
6. Implementar formularios reactivos con errores por campo y manejo de fallos HTTP.
7. Registrar evidencias reales del funcionamiento del frontend y de los endpoints.

## 3. Alcance de esta versión

### Implementado

- Páginas de inicio, nosotros, servicios, blog y contacto.
- Registro y login con respuesta JWT desde Spring Boot.
- Perfil editable y consulta de citas del paciente autenticado.
- Solicitud de citas, incluyendo selección opcional de doctor y tratamiento.
- Panel de resumen administrativo, gestión de doctores y fotografías, tratamientos, usuarios y citas.
- Rutas 403/404, guards y navegación reutilizable.
- Formularios reactivos, mensajes de validación, estados de carga y respuestas de error.
- Plan intermedio, guía de ejecución, nueva colección Postman y evidencias.
- Configuración opcional de contenedores para demostración local.

### Fuera del alcance verificado

- Despliegue productivo, migración de datos reales y validación con la base MySQL del equipo.
- Pagos, envío automático de correo o SMS y confirmaciones externas.
- Refresh tokens y revocación centralizada inmediata al cerrar sesión.
- Editor de publicaciones: el blog conserva contenido estático de la plantilla original.
- Prueba de ejecución de los contenedores: únicamente se validó la configuración Compose.

## 4. Tecnologías y herramientas

| Capa | Tecnología |
|---|---|
| Frontend | Angular 21.2, TypeScript 5.9, componentes standalone y Router |
| HTTP y estado | HttpClient, RxJS, signals y servicios con `inject()` |
| Formularios | ReactiveFormsModule y Validators |
| Diseño | Bootstrap 5.3.8, Font Awesome y CSS original adaptado |
| Backend | Spring Boot 3.3.5, Java 17 como versión objetivo |
| Seguridad | Spring Security, Resource Server y Nimbus JWT HS256 |
| Persistencia | Spring Data JPA; H2 en el perfil local; configuración MySQL conservada |
| Pruebas | JUnit, MockMvc, Mockito y casos Playwright preparados |
| Contenedores | Dockerfile Spring, Dockerfile Angular/Nginx y Compose de demostración |

Se verificó localmente con JDK 21 y Node 24. El `package-lock.json` permite instalar las mismas dependencias mediante `npm ci`.

## 5. Arquitectura y organización

```text
TuDentaria/
├── src/main/java/com/utp/tudentaria/
│   ├── config/         seguridad web, JWT y CORS
│   ├── controller/     controladores MVC conservados y endpoints REST
│   ├── dto/            entrada validada y respuestas sin contraseñas
│   ├── service/        reglas de usuarios, citas y catálogos
│   ├── repository/     acceso a datos con JPA
│   └── model/          entidades existentes
├── src/main/resources/
│   ├── templates/      vistas Thymeleaf originales
│   └── static/         recursos originales
├── frontend/
│   ├── src/app/core/   servicios API/auth, interceptor, guards y modelos
│   ├── src/app/shared/ encabezado, pie de página y errores de campos
│   ├── src/app/features/
│   │   ├── public/     inicio, nosotros, servicios, blog, 403 y 404
│   │   ├── auth/       login y registro
│   │   ├── citas/      contacto, formulario y mis citas
│   │   ├── perfil/     actualización de datos
│   │   └── admin/      paneles de gestión
│   ├── src/app/app.routes.ts
│   ├── public/         imágenes y video reutilizados
│   └── e2e/            casos de prueba de navegador
└── docs/               plan, guía, colección y evidencias
```

Flujo principal: **componente Angular → servicio con `inject(HttpClient)` → API Spring Boot → servicio de negocio → repositorio JPA**. Las pantallas no ejecutan peticiones HTTP directamente. El interceptor añade Bearer a la API; los guards controlan la navegación y Spring Security valida la autorización efectiva.

## 6. Rutas y componentes reutilizables

| Ruta Angular | Acceso | Función |
|---|---|---|
| `/` | Público | Inicio |
| `/nosotros` | Público | Presentación y equipo desde la API |
| `/servicios` | Público | Servicios y catálogo desde la API |
| `/blog` | Público | Artículos y modales informativos |
| `/contacto` | Público | Contacto y solicitud de cita |
| `/login` | Público | Inicio de sesión |
| `/registro` | Público | Alta de paciente |
| `/perfil` | Autenticado | Perfil propio |
| `/mis-citas` | Autenticado | Historial propio |
| `/admin` | Administrador | Resumen |
| `/admin/doctores` | Administrador | Doctores y fotografías |
| `/admin/tratamientos` | Administrador | Tratamientos |
| `/admin/usuarios` | Administrador | Cuentas y roles |
| `/admin/citas` | Administrador | Estados y gestión de citas |
| `/403` | Público | Acceso restringido |
| `**` | Público | Página 404 |

El encabezado y pie de página se renderizan una sola vez desde el componente raíz. `FieldErrorComponent` se utiliza en los formularios para mantener mensajes coherentes. El equipo y el catálogo encapsulan la consulta y presentación de datos.

## 7. Autenticación y permisos

1. Angular envía correo y contraseña a `POST /api/auth/login`.
2. Spring valida las credenciales contra el usuario y la contraseña BCrypt existentes.
3. El servidor emite un JWT firmado HS256 con emisor `tudentaria`, sujeto, fecha de emisión, expiración y roles.
4. La respuesta incluye `accessToken`, `tokenType: Bearer`, `expiresIn: 3600` y los datos públicos del usuario.
5. Angular conserva el token en `sessionStorage`; el interceptor lo adjunta a las peticiones protegidas.
6. Spring verifica firma, emisor y vigencia, y consulta los roles actuales del usuario en la base.
7. Las lecturas privadas requieren autenticación; las operaciones administrativas requieren `ROLE_ADMIN`.

Las respuestas de usuarios usan DTO y no incluyen el hash de contraseña. Cambiar el correo obliga a reingresar. El historial se busca por ID de paciente para no perderlo al cambiar de correo. Si no se configura `JWT_SECRET`, la clave es efímera y reiniciar el backend invalida los tokens. El logout local elimina el token del navegador; un token copiado sigue siendo válido hasta su expiración.

La API no utiliza sesiones ni autenticación Basic. El sitio Thymeleaf conserva su cadena de seguridad por sesión y protección CSRF. En desarrollo, el proxy Angular permite usar URLs relativas; CORS está limitado a los orígenes locales configurados.

## 8. Formularios y reglas de validación

| Formulario | Validaciones principales |
|---|---|
| Login | Correo válido, contraseña requerida, longitud máxima y credenciales inválidas |
| Registro | Nombres y apellidos de 2–50 caracteres; DNI de 8 dígitos; celular de 9; correo válido; contraseña de 6–72; confirmación coincidente |
| Cita | Identidad y contacto válidos; fecha desde hoy; domingo no permitido; hora y motivo requeridos; notas hasta 1000 caracteres |
| Perfil | Nombres y apellidos de 2–50; correo válido; celular opcional con 9 dígitos |
| Doctor | Nombre requerido; especialidad requerida; foto opcional JPG/PNG/WEBP de hasta 5 MB |
| Tratamiento | Nombre requerido; precio positivo con máximo 2 decimales; duración positiva |
| Usuario administrativo | Nombres, correo y rol; contraseña requerida al crear y opcional al editar |
| Estado de cita | Estado requerido y notas hasta 1000 caracteres |

Todos utilizan formularios reactivos. Se marcan los campos al enviar un formulario inválido, se presentan mensajes y se incorporan errores por campo devueltos por la API. Las reglas de negocio del servidor siguen siendo obligatorias: horario de atención, conflictos de agenda y restricciones de cuentas administrativas.

## 9. Matriz de cumplimiento de la rúbrica

| Criterio | Puntos de la rúbrica | Evidencia de implementación |
|---|---:|---|
| Organización del proyecto | 2 | `frontend/src/app/features`, `core` y `shared` |
| Sistema de rutas | 3 | `app.routes.ts`, inicio, navegación, guards y wildcard 404 |
| Diseño homogéneo | 3 | Bootstrap instalado, CSS común y componentes de layout |
| Operación de inicio de sesión | 2 | Login Angular → API Spring → JWT; capturas 01 y 04 de API |
| Uso de servicios | 2 | ApiService/AuthService con `inject(HttpClient)` y consumidores con `inject()` |
| Extensión del proyecto | 2 | Más de 5 páginas y encabezado/pie reutilizables |
| Formularios reactivos y validación | 3 | Formularios de autenticación, citas, perfil y administración; captura de errores |
| Plan de desarrollo intermedio | 3 | Este documento, capturas de frontend y 32 respuestas API, incluido JWT |

**Total de la rúbrica: 20 puntos.** La tabla relaciona requisitos con implementación; la calificación corresponde al docente. Los datos institucionales del documento deben completarse antes de entregar.

## 10. Plan de trabajo y estado

| Etapa | Resultado esperado | Estado intermedio |
|---|---|---|
| Diagnóstico | Inventario del sistema y APIs reutilizables | Completado |
| Backend | JWT, DTO y endpoints adicionales | Implementado y probado localmente |
| Estructura Angular | Organización, rutas y layout | Implementado y compilado |
| Integración | Login, registro, citas, perfil y administración | Flujos principales verificados en navegador |
| Evidencias | Capturas y documento intermedio | Incluidos |
| Cierre académico | Datos del equipo y formato exigido por el docente | Pendiente del equipo |
| Entorno final | MySQL, ejecución Docker y despliegue si corresponde | Pendiente de validación del entorno |

## 11. Verificación realizada

- **24 pruebas Java aprobadas:** 15 existentes y 9 nuevas de integración para login/JWT, permisos, validación, aislamiento de citas, cambio de correo, expiración, CORS y compatibilidad Thymeleaf.
- **Compilación Angular de producción aprobada**, con comprobación estricta de plantillas y TypeScript.
- **32 peticiones HTTP verificadas** contra el backend local de demostración; las respuestas y capturas están en `evidencias/api/`.
- **Navegador integrado:** registro de paciente ficticio, login, perfil, edición persistente del perfil, cita, historial, creación/edición de tratamiento, confirmación de cita por administrador, rutas 403/404, equipo/catálogo y modal del blog.
- **Docker Compose:** configuración validada mediante `docker compose --env-file .env.example config --quiet`; no se ejecutaron las imágenes.
- **Cinco casos Playwright preparados, sin resultado funcional en esta máquina:** Chromium fue bloqueado antes de abrir las páginas por una restricción del sistema. No se cuentan como pruebas aprobadas. Se conservan para ejecutar desde una terminal con acceso normal al navegador.

La verificación utilizó H2 en memoria y datos ficticios. No se accedió a la base MySQL del equipo ni se publicó el proyecto. Las capturas de API muestran respuestas HTTP reales presentadas en un informe local, no una interfaz simulada de Postman. Se omitieron contraseñas y la firma completa del token.

## 12. Evidencias del frontend

### Inicio y diseño conservado

![Inicio y diseño conservado](evidencias/frontend/inicio.jpg)

### Equipo consultado desde la API

![Equipo consultado desde la API](evidencias/frontend/nosotros.jpg)

### Servicios y catálogo

![Servicios y catálogo](evidencias/frontend/servicios.jpg)

### Blog migrado

![Blog migrado](evidencias/frontend/blog.jpg)

### Errores del formulario reactivo

![Errores del formulario reactivo](evidencias/frontend/validacion-registro.jpg)

### Registro exitoso

![Registro exitoso](evidencias/frontend/registro-exitoso.jpg)

### Inicio de sesión

![Inicio de sesión](evidencias/frontend/login.jpg)

### Perfil autenticado

![Perfil autenticado](evidencias/frontend/perfil.jpg)

### Perfil actualizado y conservado tras recargar

![Perfil actualizado y conservado tras recargar](evidencias/frontend/perfil-actualizado.jpg)

### Solicitud recibida por Spring Boot

![Solicitud recibida por Spring Boot](evidencias/frontend/cita-enviada.jpg)

### Historial del paciente

![Historial del paciente](evidencias/frontend/mis-citas.jpg)

### Resumen administrativo

![Resumen administrativo](evidencias/frontend/admin-resumen.jpg)

### Creación de tratamiento

![Creación de tratamiento](evidencias/frontend/admin-tratamientos.jpg)

### Gestión de doctores

![Gestión de doctores](evidencias/frontend/admin-doctores.jpg)

### Gestión de usuarios

![Gestión de usuarios](evidencias/frontend/admin-usuarios.jpg)

### Administración de citas

![Administración de citas](evidencias/frontend/admin-citas.jpg)

### Confirmación de cita

![Confirmación de cita](evidencias/frontend/admin-cita-confirmada.jpg)

### Ruta sin permiso

![Ruta sin permiso](evidencias/frontend/403.jpg)

### Página no encontrada

![Página no encontrada](evidencias/frontend/404.jpg)

## 13. Evidencias de endpoints y JWT

Cada imagen presenta método, ruta, estado HTTP, petición y respuesta recibida. El login incluye las partes públicas del JWT y sus claims; la firma se omitió. Los IDs corresponden a la ejecución de demostración y no son valores fijos del sistema.

### 01. POST /api/auth/login — HTTP 200

![01. POST /api/auth/login — HTTP 200](evidencias/api/01-post-api-auth-login.jpg)

### 02. POST /api/auth/login — HTTP 401

![02. POST /api/auth/login — HTTP 401](evidencias/api/02-post-api-auth-login.jpg)

### 03. POST /api/auth/registro — HTTP 201

![03. POST /api/auth/registro — HTTP 201](evidencias/api/03-post-api-auth-registro.jpg)

### 04. POST /api/auth/login — HTTP 200

![04. POST /api/auth/login — HTTP 200](evidencias/api/04-post-api-auth-login.jpg)

### 05. GET /api/perfil — HTTP 200

![05. GET /api/perfil — HTTP 200](evidencias/api/05-get-api-perfil.jpg)

### 06. PUT /api/perfil — HTTP 200

![06. PUT /api/perfil — HTTP 200](evidencias/api/06-put-api-perfil.jpg)

### 07. GET /api/admin/resumen — HTTP 403

![07. GET /api/admin/resumen — HTTP 403](evidencias/api/07-get-api-admin-resumen.jpg)

### 08. GET /api/admin/usuarios — HTTP 401

![08. GET /api/admin/usuarios — HTTP 401](evidencias/api/08-get-api-admin-usuarios.jpg)

### 09. GET /api/doctores — HTTP 200

![09. GET /api/doctores — HTTP 200](evidencias/api/09-get-api-doctores.jpg)

### 10. GET /api/tratamientos — HTTP 200

![10. GET /api/tratamientos — HTTP 200](evidencias/api/10-get-api-tratamientos.jpg)

### 11. GET /api/especialidades — HTTP 200

![11. GET /api/especialidades — HTTP 200](evidencias/api/11-get-api-especialidades.jpg)

### 12. POST /api/doctores — HTTP 201

![12. POST /api/doctores — HTTP 201](evidencias/api/12-post-api-doctores.jpg)

### 13. GET /api/doctores/3 — HTTP 200

![13. GET /api/doctores/3 — HTTP 200](evidencias/api/13-get-api-doctores-3.jpg)

### 14. PUT /api/doctores/3 — HTTP 200

![14. PUT /api/doctores/3 — HTTP 200](evidencias/api/14-put-api-doctores-3.jpg)

### 15. POST /api/admin/doctores/3/foto — HTTP 200

![15. POST /api/admin/doctores/3/foto — HTTP 200](evidencias/api/15-post-api-admin-doctores-3-foto.jpg)

### 16. DELETE /api/doctores/3 — HTTP 200

![16. DELETE /api/doctores/3 — HTTP 200](evidencias/api/16-delete-api-doctores-3.jpg)

### 17. POST /api/tratamientos — HTTP 201

![17. POST /api/tratamientos — HTTP 201](evidencias/api/17-post-api-tratamientos.jpg)

### 18. GET /api/tratamientos/6 — HTTP 200

![18. GET /api/tratamientos/6 — HTTP 200](evidencias/api/18-get-api-tratamientos-6.jpg)

### 19. PUT /api/tratamientos/6 — HTTP 200

![19. PUT /api/tratamientos/6 — HTTP 200](evidencias/api/19-put-api-tratamientos-6.jpg)

### 20. DELETE /api/tratamientos/6 — HTTP 200

![20. DELETE /api/tratamientos/6 — HTTP 200](evidencias/api/20-delete-api-tratamientos-6.jpg)

### 21. POST /api/citas — HTTP 201

![21. POST /api/citas — HTTP 201](evidencias/api/21-post-api-citas.jpg)

### 22. GET /api/citas/mias — HTTP 200

![22. GET /api/citas/mias — HTTP 200](evidencias/api/22-get-api-citas-mias.jpg)

### 23. POST /api/citas — HTTP 400

![23. POST /api/citas — HTTP 400](evidencias/api/23-post-api-citas.jpg)

### 24. GET /api/admin/resumen — HTTP 200

![24. GET /api/admin/resumen — HTTP 200](evidencias/api/24-get-api-admin-resumen.jpg)

### 25. GET /api/admin/usuarios — HTTP 200

![25. GET /api/admin/usuarios — HTTP 200](evidencias/api/25-get-api-admin-usuarios.jpg)

### 26. GET /api/admin/roles — HTTP 200

![26. GET /api/admin/roles — HTTP 200](evidencias/api/26-get-api-admin-roles.jpg)

### 27. POST /api/admin/usuarios — HTTP 201

![27. POST /api/admin/usuarios — HTTP 201](evidencias/api/27-post-api-admin-usuarios.jpg)

### 28. PUT /api/admin/usuarios/4 — HTTP 200

![28. PUT /api/admin/usuarios/4 — HTTP 200](evidencias/api/28-put-api-admin-usuarios-4.jpg)

### 29. DELETE /api/admin/usuarios/4 — HTTP 200

![29. DELETE /api/admin/usuarios/4 — HTTP 200](evidencias/api/29-delete-api-admin-usuarios-4.jpg)

### 30. GET /api/admin/citas — HTTP 200

![30. GET /api/admin/citas — HTTP 200](evidencias/api/30-get-api-admin-citas.jpg)

### 31. PATCH /api/admin/citas/2 — HTTP 200

![31. PATCH /api/admin/citas/2 — HTTP 200](evidencias/api/31-patch-api-admin-citas-2.jpg)

### 32. DELETE /api/admin/citas/2 — HTTP 200

![32. DELETE /api/admin/citas/2 — HTTP 200](evidencias/api/32-delete-api-admin-citas-2.jpg)

## 14. Riesgos, pendientes y criterios de cierre

- Validar con la base MySQL de trabajo antes de retirar Thymeleaf o cambiar el despliegue.
- El perfil local usa H2 `create-drop`: reiniciar elimina los datos de demostración.
- Configurar una clave JWT persistente y credenciales propias en el entorno final.
- Las peticiones Postman antiguas con Basic Auth necesitan Bearer; se agregó una colección nueva sin modificar las carpetas existentes.
- Conservar el contenido estático del blog y tarifas de referencia no equivale a un CMS: cualquier ampliación debe definirse por separado.
- Completar integrantes, curso, docente y formato institucional del documento. Si se solicita Word/PDF, convertir y revisar visualmente antes de entregar.
- Ejecutar los cinco casos de navegador y los contenedores en un entorno que los permita, y registrar los resultados adicionales sin sustituir los de esta versión.

## 15. Referencias técnicas

- [Compatibilidad oficial de versiones Angular](https://angular.dev/reference/versions).
- [Formularios reactivos de Angular](https://angular.dev/guide/forms/reactive-forms).
- [Inyección con inject()](https://angular.dev/api/core/inject).
- [JWT en Spring Security Resource Server](https://docs.spring.io/spring-security/reference/servlet/oauth2/resource-server/jwt.html).

Para instrucciones de ejecución, configuración de puertos y datos de demostración, consultar `GUIA_MIGRACION_ANGULAR.md`.
