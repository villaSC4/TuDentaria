import { test, expect } from "@playwright/test";
import fs from "node:fs";
import path from "node:path";
const evidencia = path.resolve("../docs/evidencias/frontend");
fs.mkdirSync(evidencia, { recursive: true });
async function captura(page: any, name: string) {
  await page.screenshot({
    path: path.join(evidencia, name + ".png"),
    fullPage: true,
  });
}
async function login(page: any, email: string, password: string) {
  await page.goto("/login");
  await page.getByLabel("Correo Electrónico", { exact: true }).fill(email);
  await page.getByLabel("Contraseña", { exact: true }).fill(password);
  await page.getByRole("button", { name: "Ingresar", exact: true }).click();
}
test("páginas públicas, navegación, modales y 404", async ({ page }) => {
  const errors: string[] = [];
  page.on("pageerror", (e) => errors.push(e.message));
  await page.goto("/");
  await expect(
    page.getByRole("heading", {
      name: "Una Sonrisa Saludable, Una Vida Feliz",
    }),
  ).toBeVisible();
  await captura(page, "01-inicio");
  await page
    .getByRole("button", { name: "Leer Más", exact: true })
    .first()
    .click();
  await expect(page.locator("#modalEmergencia")).toBeVisible();
  await page.locator("#modalEmergencia button.btn-close").click();
  for (const [route, title] of [
    ["/nosotros", "Nosotros"],
    ["/servicios", "Servicios"],
    ["/blog", "Blog"],
    ["/contacto", "Contacto"],
  ]) {
    await page.goto(route);
    await expect(page.locator("main")).toContainText(title);
    if (route === "/nosotros")
      await expect(page.locator("app-equipo")).toContainText(
        "Dra. Raquel Villa",
      );
    if (route === "/servicios")
      await expect(page.locator("main")).toContainText("Rehabilitación");
    await captura(page, route.slice(1));
  }
  await page.goto("/no-existe");
  await expect(
    page.getByRole("heading", { name: "¡Ups! Página no encontrada" }),
  ).toBeVisible();
  await captura(page, "404");
  expect(errors).toEqual([]);
});
test("formulario reactivo bloquea envío inválido", async ({ page }) => {
  await page.goto("/registro");
  let calls = 0;
  page.on("request", (r) => {
    if (r.url().includes("/api/auth/registro")) calls++;
  });
  await page.getByRole("button", { name: "Registrarme", exact: true }).click();
  await expect(
    page.getByText("Este campo es obligatorio.").first(),
  ).toBeVisible();
  expect(calls).toBe(0);
  await captura(page, "validacion-registro");
  await page.goto("/contacto");
  await page.getByRole("button", { name: /Solicitar cita/i }).click();
  await expect(
    page.getByText("Este campo es obligatorio.").first(),
  ).toBeVisible();
  await captura(page, "validacion-cita");
});
test("registro, login JWT, cita y perfil", async ({ page }) => {
  const unique = Date.now().toString().slice(-8);
  const email = "ui" + unique + "@example.com";
  await page.goto("/registro");
  await page.getByLabel("Nombre", { exact: true }).fill("Paciente");
  await page.getByLabel("Apellido", { exact: true }).fill("Prueba Angular");
  await page.getByLabel("DNI (8 dígitos)").fill(unique);
  await page.getByLabel("Celular (9 dígitos)").fill("987654321");
  await page.getByLabel("Correo Electrónico", { exact: true }).fill(email);
  await page.getByLabel("Contraseña").fill("Paciente123!");
  await page.getByLabel("Confirmar Contraseña").fill("Paciente123!");
  await page.getByRole("button", { name: "Registrarme", exact: true }).click();
  await expect(page).toHaveURL(/login/);
  await login(page, email, "Paciente123!");
  await expect(page).toHaveURL(/perfil/);
  await expect(page.getByLabel("Nombre", { exact: true })).toHaveValue(
    "Paciente",
  );
  await expect(
    page.getByRole("button", { name: /Guardar Cambios/i }),
  ).toBeEnabled();
  await captura(page, "perfil");
  const token = await page.evaluate(
    () => JSON.parse(sessionStorage.getItem("tudentaria.auth")!).accessToken,
  );
  expect(token.split(".")).toHaveLength(3);
  await page.goto("/contacto");
  await expect(page.getByLabel("Nombre", { exact: true })).toHaveValue(
    "Paciente",
  );
  const date = new Date();
  date.setDate(date.getDate() + 3);
  if (date.getDay() === 0) date.setDate(date.getDate() + 1);
  const day =
    date.getFullYear() +
    "-" +
    String(date.getMonth() + 1).padStart(2, "0") +
    "-" +
    String(date.getDate()).padStart(2, "0");
  await page.getByLabel("Fecha de Cita", { exact: true }).fill(day);
  await page
    .getByLabel("Hora (Citas en punto)", { exact: true })
    .selectOption("10:00");
  await page
    .getByLabel("Motivo de tu consulta")
    .fill("Evaluación de prueba Angular");
  await page.getByRole("button", { name: /Solicitar cita/i }).click();
  await expect(page.getByRole("status")).toContainText("enviada");
  await captura(page, "cita-enviada");
  await page.goto("/mis-citas");
  await expect(page.locator("main")).toContainText(
    "Evaluación de prueba Angular",
  );
  await captura(page, "mis-citas");
  await page.goto("/admin");
  await expect(
    page.getByRole("heading", { name: "Acceso Restringido" }),
  ).toBeVisible();
  await page.goto("/perfil");
  await expect(
    page.getByRole("button", { name: /Guardar Cambios/i }),
  ).toBeEnabled();
  await page.getByLabel("Nombre", { exact: true }).fill("Paciente Editado");
  await page.getByRole("button", { name: /Guardar Cambios/i }).click();
  await expect(page.getByRole("status")).toContainText("Perfil actualizado");
  await page.reload();
  await expect(page.getByLabel("Nombre", { exact: true })).toHaveValue(
    "Paciente Editado",
  );
  await page.goto("/");
  await page.locator("#userDropdown").click();
  await page.getByRole("button", { name: /Cerrar Sesión/ }).click();
  await page.goto("/perfil");
  await expect(page).toHaveURL(/login/);
});
test("administrador gestiona catálogo y abre sus paneles", async ({ page }) => {
  await login(page, "admin@tudentaria.com", "Admin123!");
  await expect(page).toHaveURL(/admin$/);
  await expect(page.locator("app-dashboard")).toContainText("Citas Pendientes");
  await captura(page, "admin-resumen");
  for (const route of ["doctores", "usuarios", "citas"]) {
    await page.goto("/admin/" + route);
    await expect(page.locator("main")).not.toContainText("No se pudo");
    await expect(page.getByRole("table")).toBeVisible();
    await captura(page, "admin-" + route);
  }
  await page.goto("/admin/tratamientos");
  await page.getByRole("button", { name: /Agregar Tratamiento/ }).click();
  await page
    .getByLabel("Nombre del Tratamiento", { exact: true })
    .fill("Tratamiento UI temporal");
  await page
    .getByLabel("Descripción", { exact: true })
    .fill("Comprobación del CRUD Angular");
  await page.getByLabel("Precio (S/)").fill("99.50");
  await page.getByLabel("Duración en minutos").fill("30");
  await page.getByRole("button", { name: /Guardar Tratamiento/i }).click();
  await expect(page.getByRole("status")).toContainText("guardado");
  let row = page
    .getByRole("row")
    .filter({ hasText: "Tratamiento UI temporal" });
  await expect(row).toBeVisible();
  await row.getByRole("button", { name: "Editar Tratamiento" }).click();
  await page.getByLabel("Precio (S/)").fill("120.00");
  await page.getByRole("button", { name: /Guardar Tratamiento/i }).click();
  await expect(row).toContainText("120");
  await captura(page, "admin-tratamientos");
  page.once("dialog", (d) => d.accept());
  await row.getByRole("button", { name: "Eliminar Tratamiento" }).click();
  await expect(row).toHaveCount(0);
});
test("navegación en móvil y retorno a login", async ({ page }) => {
  await page.setViewportSize({ width: 390, height: 844 });
  await page.goto("/");
  await page.getByRole("button", { name: "Abrir menú" }).click();
  await page
    .locator("app-header")
    .getByRole("link", { name: "Contacto", exact: true })
    .click();
  await expect(page).toHaveURL(/contacto$/);
  await captura(page, "contacto-movil");
  expect(
    await page.evaluate(
      () => document.documentElement.scrollWidth <= window.innerWidth,
    ),
  ).toBeTruthy();
  await page.goto("/admin/usuarios");
  await expect(page).toHaveURL(/login\?returnUrl/);
});
