package com.utp.tudentaria.repository;

import com.utp.tudentaria.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {
    Optional<Usuario> findByEmail(String email);

    Optional<Usuario> findByEmailIgnoreCase(String email);

    boolean existsByPacienteDni(String dni);

    /** Cuántos usuarios tienen un rol dado (para no dejar el sistema sin administradores). - validaciones */
    long countByRoles_Nombre(String nombreRol);
}