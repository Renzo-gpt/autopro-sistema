package com.utp.autopro.repository;

import com.utp.autopro.model.Rol;
import com.utp.autopro.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByUsername(String username);
    List<Usuario> findByRolAndActivoTrue(Rol rol);
}
