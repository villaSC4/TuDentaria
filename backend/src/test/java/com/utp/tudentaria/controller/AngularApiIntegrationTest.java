package com.utp.tudentaria.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.utp.tudentaria.repository.UsuarioRepository;
import com.utp.tudentaria.repository.RolRepository;
import java.time.*;
import java.util.*;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(properties={"app.admin.email=admin.integration@example.com","app.admin.password=AdminTest123!","spring.datasource.url=jdbc:h2:mem:angular_api;DB_CLOSE_DELAY=-1"})
@AutoConfigureMockMvc
class AngularApiIntegrationTest {
    @Autowired MockMvc mvc;
    @Autowired ObjectMapper json;
    @Autowired JwtDecoder decoder;
    @Autowired JwtEncoder encoder;
    @Autowired UsuarioRepository usuarios;
    @Autowired RolRepository roles;

    private String register(String suffix) throws Exception {
        String digits = String.format("%08d", Math.abs(suffix.hashCode()) % 100000000);
        String email="test"+suffix+"@example.com";
        mvc.perform(post("/api/auth/registro").contentType(MediaType.APPLICATION_JSON).content(json.writeValueAsString(Map.of(
            "nombre","Prueba","apellido","Angular","dni",digits,"telefono","987654321","email",email,
            "password","Prueba123!","confirmarPassword","Prueba123!"))))
            .andExpect(status().isCreated()).andExpect(jsonPath("$.password").doesNotExist()).andExpect(jsonPath("$.roles[0]").value("ROLE_USER"));
        return email;
    }
    private String login(String email,String password) throws Exception {
        var response=mvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON)
            .content(json.writeValueAsString(Map.of("email",email,"password",password))))
            .andExpect(status().isOk()).andExpect(jsonPath("$.tokenType").value("Bearer"))
            .andExpect(jsonPath("$.usuario.password").doesNotExist()).andReturn().getResponse().getContentAsString();
        return json.readTree(response).get("accessToken").asText();
    }
    @Test void loginEmiteJwtFirmadoYPermitePerfil() throws Exception {
        String email=register("jwt");String token=login(email,"Prueba123!");
        assertEquals(email,decoder.decode(token).getSubject());
        mvc.perform(get("/api/perfil").header("Authorization","Bearer "+token)).andExpect(status().isOk()).andExpect(jsonPath("$.email").value(email)).andExpect(jsonPath("$.password").doesNotExist());
    }
    @Test void rechazaCredencialesYJwtAlterado() throws Exception {
        mvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON).content("{\"email\":\"nadie@example.com\",\"password\":\"incorrecta\"}")).andExpect(status().isUnauthorized());
        mvc.perform(get("/api/perfil").header("Authorization","Bearer abc.def.ghi")).andExpect(status().isUnauthorized());
    }
    @Test void protegeLecturasPrivadasYEscritura() throws Exception {
        mvc.perform(get("/api/admin/usuarios")).andExpect(status().isUnauthorized());
        mvc.perform(get("/api/citas/mias")).andExpect(status().isUnauthorized());
        mvc.perform(post("/api/doctores").contentType(MediaType.APPLICATION_JSON).content("{}")).andExpect(status().isUnauthorized());
        mvc.perform(get("/api/doctores")).andExpect(status().isOk());
        String token=login(register("permisos"),"Prueba123!");
        mvc.perform(get("/api/admin/resumen").header("Authorization","Bearer "+token)).andExpect(status().isForbidden());
        mvc.perform(post("/api/tratamientos").header("Authorization","Bearer "+token).contentType(MediaType.APPLICATION_JSON).content("{}")).andExpect(status().isForbidden());
    }
    @Test void adminAccedeSinExponerHashes() throws Exception {
        String token=login("admin.integration@example.com","AdminTest123!");
        mvc.perform(get("/api/admin/resumen").header("Authorization","Bearer "+token)).andExpect(status().isOk());
        mvc.perform(get("/api/admin/usuarios").header("Authorization","Bearer "+token)).andExpect(status().isOk()).andExpect(jsonPath("$[*].password").isEmpty());
    }
    @Test void validaCamposYContrasenas() throws Exception {
        mvc.perform(post("/api/auth/registro").contentType(MediaType.APPLICATION_JSON).content("{}")).andExpect(status().isBadRequest()).andExpect(jsonPath("$.errores.email").exists());
        mvc.perform(post("/api/auth/registro").contentType(MediaType.APPLICATION_JSON).content(json.writeValueAsString(Map.of(
          "nombre","Prueba","apellido","Angular","dni","90909090","telefono","987654321","email","mismatch@example.com","password","Prueba123!","confirmarPassword","Otra123!"))))
          .andExpect(status().isBadRequest()).andExpect(jsonPath("$.errores.confirmarPassword").exists());
    }
    @Test void historialSeAislaPorPacienteYSeConservaAlCambiarEmail() throws Exception {
        String email=register("citas");String token=login(email,"Prueba123!");
        LocalDate fecha=LocalDate.now().plusDays(3);while(fecha.getDayOfWeek()==DayOfWeek.SUNDAY)fecha=fecha.plusDays(1);
        var body=Map.of("nombre","Prueba","apellido","Angular","dni","10101010","telefono","987654321","email",email,"fecha",fecha.toString(),"hora","10:00","motivo","Revisión");
        mvc.perform(post("/api/citas").header("Authorization","Bearer "+token).contentType(MediaType.APPLICATION_JSON).content(json.writeValueAsString(body))).andExpect(status().isCreated());
        mvc.perform(get("/api/citas/mias").header("Authorization","Bearer "+token)).andExpect(status().isOk()).andExpect(jsonPath("$.length()").value(1));
        String otro=login(register("otro"),"Prueba123!");
        mvc.perform(get("/api/citas/mias").header("Authorization","Bearer "+otro)).andExpect(status().isOk()).andExpect(jsonPath("$.length()").value(0));
        mvc.perform(put("/api/perfil").header("Authorization","Bearer "+token).contentType(MediaType.APPLICATION_JSON).content("{\"nombre\":\"Prueba\",\"apellido\":\"Angular\",\"email\":\"nuevo.citas@example.com\",\"telefono\":\"987654321\"}"))
            .andExpect(status().isOk()).andExpect(jsonPath("$.requiereLogin").value(true));
        mvc.perform(get("/api/perfil").header("Authorization","Bearer "+token)).andExpect(status().isUnauthorized());
        String nuevo=login("nuevo.citas@example.com","Prueba123!");
        mvc.perform(get("/api/citas/mias").header("Authorization","Bearer "+nuevo)).andExpect(status().isOk()).andExpect(jsonPath("$.length()").value(1));
    }
    @Test void rechazaJwtExpirado() throws Exception {
        Instant now=Instant.now();var claims=JwtClaimsSet.builder().issuer("tudentaria").subject("admin.integration@example.com").issuedAt(now.minusSeconds(600)).expiresAt(now.minusSeconds(300)).build();
        String token=encoder.encode(JwtEncoderParameters.from(JwsHeader.with(MacAlgorithm.HS256).build(),claims)).getTokenValue();
        mvc.perform(get("/api/admin/resumen").header("Authorization","Bearer "+token)).andExpect(status().isUnauthorized());
    }
    @Test void conservaSitioThymeleaf() throws Exception {
        mvc.perform(get("/")).andExpect(status().isOk()).andExpect(view().name("index"));
        mvc.perform(get("/login")).andExpect(status().isOk()).andExpect(view().name("pages/login"));
    }
    @Test void permitePreflightAngular() throws Exception {
        mvc.perform(options("/api/perfil").header("Origin","http://localhost:4200").header("Access-Control-Request-Method","PUT").header("Access-Control-Request-Headers","authorization,content-type"))
            .andExpect(status().isOk()).andExpect(header().string("Access-Control-Allow-Origin","http://localhost:4200"));
    }
}
