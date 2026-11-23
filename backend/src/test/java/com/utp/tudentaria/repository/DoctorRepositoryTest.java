package com.utp.tudentaria.repository;

import com.utp.tudentaria.model.Doctor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class DoctorRepositoryTest {

    @Autowired
    private DoctorRepository doctorRepository;

    private Doctor doctorPrueba;

    @BeforeEach
    void setUp() {
        doctorRepository.deleteAll();
        doctorPrueba = new Doctor("Dr. Roberto Silva", "Endodoncia", "doc-test.jpg");
    }

    @Test
    @DisplayName("TDD 1: Guardar doctor en repositorio")
    void testGuardarDoctor() {
        Doctor guardado = doctorRepository.save(doctorPrueba);

        assertNotNull(guardado.getId(), "El ID del doctor no debe ser nulo tras guardarse");
        assertEquals("Dr. Roberto Silva", guardado.getNombre());
        assertEquals("Endodoncia", guardado.getEspecialidad());
    }

    @Test
    @DisplayName("TDD 2: Buscar doctor por ID")
    void testBuscarPorId() {
        Doctor guardado = doctorRepository.save(doctorPrueba);

        Optional<Doctor> encontrado = doctorRepository.findById(guardado.getId());

        assertTrue(encontrado.isPresent(), "El doctor debe encontrarse por su ID");
        assertEquals("Dr. Roberto Silva", encontrado.get().getNombre());
    }

    @Test
    @DisplayName("TDD 3: Listar todos los doctores")
    void testListarTodos() {
        doctorRepository.save(doctorPrueba);
        doctorRepository.save(new Doctor("Dra. Ana Castro", "Odontopediatría", "doc2.jpg"));

        List<Doctor> lista = doctorRepository.findAll();

        assertEquals(2, lista.size(), "La lista debe contener 2 doctores");
    }

    @Test
    @DisplayName("TDD 4: Actualizar doctor existente")
    void testActualizarDoctor() {
        Doctor guardado = doctorRepository.save(doctorPrueba);

        guardado.setNombre("Dr. Roberto Silva Morales");
        guardado.setEspecialidad("Periodoncia e Implantes");
        Doctor actualizado = doctorRepository.save(guardado);

        assertEquals("Dr. Roberto Silva Morales", actualizado.getNombre());
        assertEquals("Periodoncia e Implantes", actualizado.getEspecialidad());
    }

    @Test
    @DisplayName("TDD 5: Eliminar doctor por ID")
    void testEliminarDoctor() {
        Doctor guardado = doctorRepository.save(doctorPrueba);
        Integer id = guardado.getId();

        doctorRepository.deleteById(id);

        Optional<Doctor> eliminado = doctorRepository.findById(id);
        assertFalse(eliminado.isPresent(), "El doctor ya no debe existir tras ser eliminado");
    }
}
