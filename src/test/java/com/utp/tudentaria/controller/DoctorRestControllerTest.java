package com.utp.tudentaria.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.utp.tudentaria.model.Doctor;
import com.utp.tudentaria.service.DoctorService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(DoctorRestController.class)
@AutoConfigureMockMvc(addFilters = false)
class DoctorRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DoctorService doctorService;

    @Autowired
    private ObjectMapper objectMapper;

    private Doctor doctorEjemplo;

    @BeforeEach
    void setUp() {
        doctorEjemplo = new Doctor("Dr. Andrés Salazar", "Cirugía Oral", "andres.jpg");
        doctorEjemplo.setId(10);
    }

    @Test
    @DisplayName("REST TDD: GET /api/doctores retorna lista con status 200")
    void testListarDoctoresEndpoint() throws Exception {
        when(doctorService.listarTodos()).thenReturn(Arrays.asList(doctorEjemplo));

        mockMvc.perform(get("/api/doctores")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(10))
                .andExpect(jsonPath("$[0].nombre").value("Dr. Andrés Salazar"))
                .andExpect(jsonPath("$[0].especialidad").value("Cirugía Oral"));
    }

    @Test
    @DisplayName("REST TDD: GET /api/doctores/{id} retorna doctor si existe")
    void testBuscarDoctorPorIdEndpoint() throws Exception {
        when(doctorService.buscarPorId(10)).thenReturn(Optional.of(doctorEjemplo));

        mockMvc.perform(get("/api/doctores/10")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Dr. Andrés Salazar"));
    }

    @Test
    @DisplayName("REST TDD: POST /api/doctores crea nuevo doctor con status 201")
    void testCrearDoctorEndpoint() throws Exception {
        when(doctorService.guardar(any(Doctor.class))).thenReturn(doctorEjemplo);

        mockMvc.perform(post("/api/doctores")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(doctorEjemplo)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.nombre").value("Dr. Andrés Salazar"));
    }

    @Test
    @DisplayName("REST TDD: PUT /api/doctores/{id} actualiza doctor con status 200")
    void testActualizarDoctorEndpoint() throws Exception {
        when(doctorService.buscarPorId(10)).thenReturn(Optional.of(doctorEjemplo));
        when(doctorService.guardar(any(Doctor.class))).thenReturn(doctorEjemplo);

        mockMvc.perform(put("/api/doctores/10")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(doctorEjemplo)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Dr. Andrés Salazar"));
    }

    @Test
    @DisplayName("REST TDD: DELETE /api/doctores/{id} elimina doctor con status 200")
    void testEliminarDoctorEndpoint() throws Exception {
        when(doctorService.existePorId(10)).thenReturn(true);

        mockMvc.perform(delete("/api/doctores/10")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mensaje").value("Doctor eliminado exitosamente"));
    }
}
