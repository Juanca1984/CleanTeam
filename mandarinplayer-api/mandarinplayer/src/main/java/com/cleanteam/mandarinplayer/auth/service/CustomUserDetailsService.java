package com.cleanteam.mandarinplayer.auth.service;

import java.util.Collections;
import java.util.Optional;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.cleanteam.mandarinplayer.auth.model.AuthUser;
import com.cleanteam.mandarinplayer.auth.repository.AuthUserRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final AuthUserRepository userRepository;

    public CustomUserDetailsService(AuthUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String input) throws UsernameNotFoundException {
        System.out.println("--- INTENTO DE LOGIN ---");
        System.out.println("Buscando usuario o correo: " + input);

        Optional<AuthUser> userOpt = userRepository.findByUsername(input);
        
        if (userOpt.isPresent()) {
            System.out.println(">> Encontrado por USERNAME: " + userOpt.get().getUsername());
        } else {
            System.out.println(">> No encontrado por username. Buscando por EMAIL...");
            userOpt = userRepository.findByEmail(input);
        }

        if (userOpt.isPresent()) {
             System.out.println(">> USUARIO ENCONTRADO FINALMENTE: " + userOpt.get().getEmail());
        } else {
             System.out.println(">> ERROR: Usuario NO existe en la BD.");
        }

        AuthUser u = userOpt.orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado con: " + input));

        return org.springframework.security.core.userdetails.User.builder()
                .username(u.getUsername()) 
                .password(u.getPassword())
                .authorities(Collections.emptyList())
                .build();
    }
}