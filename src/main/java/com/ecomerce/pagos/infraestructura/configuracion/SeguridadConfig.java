package com.ecomerce.pagos.infraestructura.configuracion;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Sección 9.2: la API se autentica con OAuth2/JWT y scopes separados.
 * El webhook no usa JWT: su autenticidad se verifica con la firma HMAC (sección 9.1).
 */
@Configuration
public class SeguridadConfig {

    @Bean
    public SecurityFilterChain cadenaSeguridad(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/webhooks/**").permitAll()
                        .requestMatchers("/actuator/health/**", "/actuator/info").permitAll()
                        .requestMatchers("/actuator/**").hasAuthority("SCOPE_pagos.admin")
                        .requestMatchers("/admin/**").hasAuthority("SCOPE_pagos.admin")
                        .requestMatchers(HttpMethod.GET, "/api/**").hasAuthority("SCOPE_pagos.lectura")
                        .requestMatchers("/api/**").hasAuthority("SCOPE_pagos.escritura")
                        .anyRequest().denyAll())
                .oauth2ResourceServer(o -> o.jwt(Customizer.withDefaults()));
        return http.build();
    }
}
