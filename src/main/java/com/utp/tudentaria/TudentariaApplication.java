package com.utp.tudentaria;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.utp.tudentaria.model.Rol;
import com.utp.tudentaria.model.Usuario;
import com.utp.tudentaria.repository.RolRepository;
import com.utp.tudentaria.repository.UsuarioRepository;

import java.util.Set;

@SpringBootApplication
public class TudentariaApplication {

	public static void main(String[] args) {
		SpringApplication.run(TudentariaApplication.class, args);
	}

@Bean
    public CommandLineRunner initAdmin(UsuarioRepository usuarioRepository, 
                                       RolRepository rolRepository, 
                                       PasswordEncoder passwordEncoder) {
        return args -> {
            // Verifica si el correo ya existe en la base de datos
            if (usuarioRepository.findByEmail("admin@tudentaria.com").isEmpty()) {
                
                // 1. Crear o buscar el rol de ADMIN
                Rol rolAdmin = rolRepository.findByNombre("ROLE_ADMIN")
                    .orElseGet(() -> {
                        Rol nuevoRol = new Rol();
                        nuevoRol.setNombre("ROLE_ADMIN");
                        return rolRepository.save(nuevoRol);
                    });

                // 2. Crear el usuario
                Usuario admin = new Usuario();
                admin.setNombre("Admin");
                admin.setApellido("Sistema");
                admin.setEmail("admin@tudentaria.com");
                admin.setPassword(passwordEncoder.encode("Admin123!")); 
                
                // 3. Asignar el rol (Si tu entidad Usuario usa List en vez de Set, cambia Set.of por List.of)
                admin.setRoles(Set.of(rolAdmin)); 
                
                // 4. Guardar en BD
                usuarioRepository.save(admin);
                System.out.println("✅ Usuario administrador creado exitosamente en la BD.");
            }
        };
    }
}