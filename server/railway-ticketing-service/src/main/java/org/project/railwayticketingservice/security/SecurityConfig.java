package org.project.railwayticketingservice.security;

import lombok.RequiredArgsConstructor;
import org.project.railwayticketingservice.service.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomUserDetailsService customUserDetailsService;
    private final JwtFilter jwtFilter;
    private final CustomCorsConfiguration customCorsConfiguration;
    private final AuthenticationEntryPoint authEntryPoint;
    private final LogoutHandler logoutHandler;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(request -> request.requestMatchers(
                        "/api/v1/rts/auth/**",   // all login and register endpoints

                                /* Swagger Documentation URLs */
                                "/swagger-ui/**",               // Swagger UI static resources
                                "/v3/api-docs",
                                "/v3/api-docs/**",              // OpenAPI spec
                                "/swagger-ui.html",             // Swagger main page
                                "/webjars/**"
                ).permitAll()
                        .requestMatchers(
                                "/api/v1/rts/app/schedule/new", // adding a new schedule
                                "/api/v1/rts/app/schedule/edit/{id}",   //  editing a schedule
                                "/api/v1/rts/app/train/new", // adding a new train
                                "/api/v1/rts/app/train/{id}"    //  get train information
                        ).hasRole("ADMIN")
                        .anyRequest().authenticated())
                .httpBasic(Customizer.withDefaults())
                .formLogin(AbstractHttpConfigurer::disable)
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .cors(c -> c.configurationSource(customCorsConfiguration))
                .exceptionHandling(ex -> ex.authenticationEntryPoint(authEntryPoint))
                .logout(logout -> logout.addLogoutHandler(logoutHandler))
                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }

    @Bean
    public AuthenticationManager authManager() {
        return authentication -> {
            UserDetails userDetails = customUserDetailsService.loadUserByUsername(authentication.getName());
            if (passwordEncoder().matches(userDetails.getPassword(), authentication.getCredentials().toString())) {
                throw new BadCredentialsException("Incorrect password!");
            }

            // if the above checks...
            return new UsernamePasswordAuthenticationToken(userDetails, authentication.getCredentials(), userDetails.getAuthorities());
        };
    }
}
