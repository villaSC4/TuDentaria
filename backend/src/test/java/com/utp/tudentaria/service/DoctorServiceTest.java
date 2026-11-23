package com.utp.tudentaria.service;

import com.utp.tudentaria.model.Doctor;
import com.utp.tudentaria.repository.DoctorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DoctorServiceTest {

    @Mock
    private DoctorRepository doctorRepository;

    @InjectMocks
    private DoctorServiceImpl doctorService;

    private Doctor doctorPrueba;

    @BeforeEach
    void setUp() {
        doctorPrueba = new Doctor("Dra. Pamela Vega", "Ortodoncia", "pamela.jpg");
        doctorPrueba.setId(1);
    }

    @Test
    @DisplayName("TDD Servicio: Listar todos los doctores con mock")
    void testListarTodos() {
        when(doctorRepository.findAll()).thenReturn(Arrays.asList(doctorPrueba));

        List<Doctor> resultado = doctorService.listarTodos();

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals("Dra. Pamela Vega", resultado.get(0).getNombre());
        verify(doctorRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("TDD Servicio: Buscar por ID exitoso")
    void testBuscarPorId() {
        when(doctorRepository.findById(1)).thenReturn(Optional.of(doctorPrueba));

        Optional<Doctor> resultado = doctorService.buscarPorId(1);

        assertTrue(resultado.isPresent());
        assertEquals("Dra. Pamela Vega", resultado.get().getNombre());
        verify(doctorRepository, times(1)).findById(1);
    }

    @Test
    @DisplayName("TDD Servicio: Guardar doctor correctamente")
    void testGuardar() {
        when(doctorRepository.save(any(Doctor.class))).thenReturn(doctorPrueba);

        Doctor guardado = doctorService.guardar(doctorPrueba);

        assertNotNull(guardado);
        assertEquals("Dra. Pamela Vega", guardado.getNombre());
        verify(doctorRepository, times(1)).save(doctorPrueba);
    }

    @Test
    @DisplayName("TDD Servicio: Eliminar doctor por ID")
    void testEliminarPorId() {
        doNothing().when(doctorRepository).deleteById(1);

        doctorService.eliminarPorId(1);

        verify(doctorRepository, times(1)).deleteById(1);
    }
}
