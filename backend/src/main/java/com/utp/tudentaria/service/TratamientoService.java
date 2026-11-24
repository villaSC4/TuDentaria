package com.utp.tudentaria.service;

import com.utp.tudentaria.model.Tratamiento;
import java.util.List;
import java.util.Optional;

public interface TratamientoService {
    List<Tratamiento> listarTodos();
    Optional<Tratamiento> buscarPorId(Integer id);
    Tratamiento guardar(Tratamiento tratamiento);
    void eliminarPorId(Integer id);
    boolean existePorId(Integer id);
}
