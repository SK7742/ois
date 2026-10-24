package com.oxford.csms.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .cors(Customizer.withDefaults())
            .csrf(AbstractHttpConfigurer::disable)
            .headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable))
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                    "/",
                    "/index.html",
                    "/favicon.ico",
                    "/*.js",
                    "/*.css",
                    "/assets/**",
                    "/api/auth/**",
                    "/h2-console/**",
                    "/actuator/**"
                ).permitAll()
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                // Student & Teacher profile access
                .requestMatchers(HttpMethod.GET, "/api/admin/students", "/api/admin/students/**").hasAnyRole("ADMIN", "MANAGEMENT", "TEACHER", "STUDENT")
                .requestMatchers(HttpMethod.PATCH, "/api/admin/students/*/attendance", "/api/admin/students/*/notes").hasAnyRole("ADMIN", "MANAGEMENT", "TEACHER")
                .requestMatchers("/api/admin/work-items", "/api/admin/work-items/**").hasAnyRole("ADMIN", "MANAGEMENT", "TEACHER", "STAFF")
                .requestMatchers("/api/admin/**").hasAnyRole("ADMIN", "MANAGEMENT")

                // Finance: Fees, Invoices, Transit, Leaves
                .requestMatchers(HttpMethod.GET, "/api/finance/fees/invoices/**").hasAnyRole("ADMIN", "MANAGEMENT", "TEACHER", "STAFF", "STUDENT")
                .requestMatchers(HttpMethod.POST, "/api/finance/fees/invoices/*/pay").hasAnyRole("ADMIN", "MANAGEMENT", "STAFF", "STUDENT")
                .requestMatchers("/api/finance/leaves/**").hasAnyRole("ADMIN", "MANAGEMENT", "TEACHER", "STAFF", "STUDENT")
                .requestMatchers(HttpMethod.GET, "/api/finance/transportation/**").hasAnyRole("ADMIN", "MANAGEMENT", "TEACHER", "STAFF", "STUDENT")
                .requestMatchers("/api/finance/**").hasAnyRole("ADMIN", "MANAGEMENT", "STAFF")

                // Academic: Gradebook, Timetable, Activities, Report Cards
                .requestMatchers("/api/academic/**").hasAnyRole("ADMIN", "MANAGEMENT", "TEACHER", "STUDENT")

                // Communication: Campus Tickets & WhatsApp Alerts
                .requestMatchers(HttpMethod.POST, "/api/communication/tickets/**").hasAnyRole("ADMIN", "MANAGEMENT", "TEACHER", "STAFF", "STUDENT")
                .requestMatchers(HttpMethod.GET, "/api/communication/tickets/**").hasAnyRole("ADMIN", "MANAGEMENT", "TEACHER", "STAFF", "STUDENT")
                .requestMatchers("/api/communication/**").hasAnyRole("ADMIN", "MANAGEMENT", "TEACHER", "STAFF")
                .anyRequest().authenticated()
            );

        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(List.of("*"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "X-Requested-With", "Accept"));
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
