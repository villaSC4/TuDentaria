# Frontend de TuDentaria

Aplicación Angular. Abrir una terminal **dentro de frontend/**:

```sh
npm ci
npm start
```

La aplicación se abre en <http://localhost:4200>. Iniciar también Spring Boot desde `../backend/` para que las operaciones con datos funcionen.

## Si el puerto 4200 está ocupado

`npm start` utiliza siempre el puerto 4200. Si ya hay una instancia, se detiene con un mensaje en lugar de seleccionar otro puerto que Spring rechazaría por CORS. Usa la instancia existente o detén su terminal con `Ctrl+C` antes de reiniciarla. Mantén el backend ejecutándose en 8080 y abre <http://127.0.0.1:4200/login>.

## Localizar el código

- `src/app/features/public/`: inicio, nosotros, servicios, blog y errores.
- `src/app/features/auth/`: login y registro.
- `src/app/features/citas/`: formulario de cita, contacto e historial.
- `src/app/features/perfil/`: datos del usuario.
- `src/app/features/admin/`: dashboard y mantenimientos.
- `src/app/core/`: servicios HTTP, autenticación, interceptor, guards y modelos.
- `src/app/shared/`: componentes reutilizables.
- `src/app/app.routes.ts`: rutas Angular.
- `src/styles.css`: estilos globales.
- `public/`: recursos visuales de Angular.

`proxy.conf.cjs` reenvía `/api/` y `/uploads/` a <http://127.0.0.1:8080> por defecto. Si el backend usa otro puerto, configurar `API_TARGET` al iniciar:

```sh
API_TARGET=http://127.0.0.1:8081 npm start
```

En PowerShell usar `$env:API_TARGET="http://127.0.0.1:8081"` y luego `npm start`.

Compilar con `npm run build`. Los casos de navegador están en `e2e/` y necesitan ambas aplicaciones en ejecución. Las rutas Java y los archivos `pom.xml` pertenecen exclusivamente a `../backend/`.
