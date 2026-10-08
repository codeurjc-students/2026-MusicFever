package com.musicfever.music_fever.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.musicfever.music_fever.config.jwt.JwtRequestFilter;
import com.musicfever.music_fever.config.jwt.UnauthorizedHandlerJwt;

@Configuration
public class SecurityConfig {
    private final JwtRequestFilter jwtRequestFilter;
    private final UnauthorizedHandlerJwt unauthorizedHandlerJwt;
    private final SecurityUserDetails securityUserDetails;

    @Value("${spring.profiles.active:default}")
    private String activeProfile;

    public SecurityConfig(JwtRequestFilter jwtRequestFilter, UnauthorizedHandlerJwt unauthorizedHandlerJwt, 
        SecurityUserDetails securityUserDetails){
        this.jwtRequestFilter = jwtRequestFilter;
        this.unauthorizedHandlerJwt = unauthorizedHandlerJwt;
        this.securityUserDetails = securityUserDetails;
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder(); //actualmente es el codificador más seguro
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider(){
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider(this.securityUserDetails);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }
    
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }
    
    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authenticationProvider(authenticationProvider())

            .cors(Customizer.withDefaults())

            .csrf(csrf -> csrf.disable())

            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )

            .exceptionHandling(handling -> handling
                .authenticationEntryPoint(unauthorizedHandlerJwt)
            )

            .authorizeHttpRequests(auth -> {
                if ("dev".equals(activeProfile)) {
                    auth.requestMatchers("/h2-console/**").permitAll();
                }
                auth.requestMatchers("/api/v1/tracks/").permitAll()
                .requestMatchers(
                    "/swagger-ui/**",
                    "/v3/api-docs/**",
                    "/v3/api-docs",
                    "/swagger-ui.html"
                ).permitAll()
                .requestMatchers(
                    "/api/v1/auth/login",
                    "/api/v1/auth/register"
                ).permitAll()
                .anyRequest().authenticated();
            })

            .headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()))
            .formLogin(form -> form.disable())
            .httpBasic(basic -> basic.disable())
            .addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowedOrigins(
            List.of("http://localhost:4200")
        );

        configuration.setAllowedMethods(
            List.of("GET", "POST", "PUT", "DELETE", "OPTIONS")
        );

        configuration.setAllowedHeaders(
            List.of("*")
        );

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration("/**", configuration);

        return source;
    }
}
