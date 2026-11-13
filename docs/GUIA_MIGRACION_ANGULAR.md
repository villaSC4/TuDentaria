# TuDentaria: guía de la migración a Angular

Los cambios están en la rama `Cesar`, dentro de `/Users/cesarvillanueva/TuDentaria`. No se creó ningún commit ni se publicó nada. Las carpetas `.postman/` y `postman/` existentes se conservaron.

## Qué se implementó

- Angular 21 con componentes standalone, organización por funcionalidades y carga diferida de rutas.
- Inicio, nosotros, servicios, blog, contacto, login, registro, perfil, mis citas, administración y páginas 403/404.
- Componentes compartidos de encabezado, pie de página y errores de campos; equipo y catálogo alimentados por la API.
- Bootstrap 5.3.8 y estilos, imágenes, videos y contenido reutilizados de Thymeleaf.
- Formularios reactivos para login, registro, solicitud de cita, perfil y administración de doctores, tratamientos, usuarios y citas.
- Servicios con `inject()`, interceptor Bearer y guards de autenticación/administración. El servidor valida los permisos independientemente del navegador.
- Spring Boot conservado; API JSON adicional y JWT firmado con HS256, válido una hora.
- Plan de desarrollo intermedio, capturas reales y colección Postman nueva en `docs/`.
- Configuración opcional Docker Compose para frontend y backend de demostración.

## Abrir la versión que está ejecutándose

Frontend: http://127.0.0.1:4200

Backend nuevo: http://127.0.0.1:8081

Se usó 8081 porque 8080 estaba ocupado. La aplicación anterior no se detuvo.

Administrador **exclusivo del perfil local de demostración**, ya existente en el proyecto:

- Correo: `admin@tudentaria.com`
- Contraseña: `Admin123!`

También se creó durante la verificación un paciente ficticio: `paciente.angular@example.com`, contraseña `Paciente123!`. La cuenta de paciente y las citas de prueba se eliminaron al reiniciar el backend para cargar la versión final. Puedes registrarte con datos ficticios para probar. El administrador se crea nuevamente en cada inicio del perfil local.

## Iniciar desde Visual Studio Code

Requisitos: JDK 17 o 21, Node.js compatible con Angular 21 (20.19+, 22.12+ o 24), npm. Se verificó con JDK 21 y Node 24.

Terminal 1, en la raíz del proyecto:

```sh
./mvnw spring-boot:run -Dspring-boot.run.profiles=local -Dspring-boot.run.arguments=--server.port=8081
```

Si macOS indica que no tiene permiso de ejecución, ejecutar `chmod +x mvnw` una vez, o usar `sh mvnw`.

Terminal 2:

```sh
cd frontend
npm ci
API_TARGET=http://127.0.0.1:8081 npm start
```

En PowerShell, establecer `$env:API_TARGET="http://127.0.0.1:8081"` antes de `npm start`; para Maven usar `mvnw.cmd`.

Si utilizas el backend en 8080, basta con `npm start`: es el destino predeterminado del proxy. No hace falta modificar el código del servicio Angular. Detén cada terminal con Ctrl+C antes de volver a iniciar sus procesos.

El perfil `local` usa **H2 en memoria con `create-drop`**: los datos de prueba no son persistentes. No debe apuntarse este perfil a una base con datos reales. La configuración MySQL existente se conserva para una validación posterior en el entorno correspondiente; no se conectó ni modificó una base MySQL durante este trabajo.

## Probar y compilar

```sh
./mvnw test
cd frontend
npm run build
```

El resultado Angular queda en `frontend/dist/tudentaria/browser`.

Las pruebas de navegador reproducibles están en `frontend/e2e/migracion.spec.ts`. Con backend local y frontend ya levantados:

```sh
cd frontend
npx playwright install chromium
npm run test:e2e
```

Ejecutarlas únicamente contra la base de demostración: crean cuentas, citas y un tratamiento temporal. En esta sesión Chromium automático no pudo arrancar por la restricción del sistema `bootstrap_check_in … Permission denied`; por eso esos cinco casos no se reportan como aprobados. Sus flujos principales se verificaron con el navegador integrado y se guardaron capturas.

## Docker, opcional

```sh
cp .env.example .env
# Completar ADMIN_PASSWORD y JWT_SECRET en .env
docker compose up --build
```

Detener antes los servidores de desarrollo que ocupen 4200/8081. Esta composición usa H2 en memoria para demostración; solo las fotos tienen volumen persistente. Nginx sirve Angular, reenvía `/api/` y `/uploads/`, y resuelve las rutas internas de la SPA. Los archivos de contenedores están preparados pero no fueron ejecutados en esta sesión.

## Autenticación

`POST /api/auth/login` recibe `{ "email": "...", "password": "..." }` y devuelve `accessToken`, `tokenType`, `expiresIn` y los datos públicos del usuario. Las peticiones protegidas envían `Authorization: Bearer <token>`.

Sin `JWT_SECRET`, Spring genera una clave temporal: reiniciar invalida los tokens existentes. Para una ejecución persistente, configurar una clave aleatoria de al menos 32 bytes. El token se guarda en `sessionStorage`, se elimina al salir y expira a la hora; el logout del navegador no revoca un token ya copiado antes de su vencimiento. No se añadió refresh token.

La API obtiene los roles actuales desde la base en cada petición: retirar un rol no conserva privilegios antiguos. Cambiar el correo exige volver a ingresar; el historial de citas se consulta por ID del paciente y se conserva.

La seguridad del sitio Thymeleaf se mantiene separada (sesión y CSRF). La API pasó de Basic Auth a Bearer JWT; las peticiones antiguas de Postman que usan Basic deben adaptarse o usar la colección nueva.

## Documentación para entregar

Abrir `docs/PLAN_DE_DESARROLLO_INTERMEDIO.md` en la vista previa de Markdown de VS Code. Incluye la matriz de la rúbrica, arquitectura, alcance, pruebas y capturas. Las evidencias de API son vistas de respuestas HTTP reales, no capturas de la interfaz de Postman; credenciales y firma del JWT se omitieron.

Antes de entregar, completar los integrantes, curso, docente y fechas institucionales, y confirmar si el docente exige Word o PDF. No se inventaron esos datos. La versión intermedia describe lo implementado y lo pendiente; no equivale a una certificación de nota.
