package com.utp.autopro.security;

import com.utp.autopro.model.Usuario;
import com.utp.autopro.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;
import java.util.Collections;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + username));

        return new User(
                usuario.getUsername(),
                usuario.getPassword(),
                usuario.isActivo(),
                true, true, true,
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + usuario.getRol().name()))
        );
    }
}
