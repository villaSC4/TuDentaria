package com.utp.tudentaria.repository;

import com.utp.tudentaria.model.Tratamiento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface TratamientoRepository extends JpaRepository<Tratamiento, Integer> {
    Optional<Tratamiento> findByNombre(String nombre);
}
