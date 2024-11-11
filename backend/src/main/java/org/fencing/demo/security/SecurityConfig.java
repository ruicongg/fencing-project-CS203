package org.fencing.demo.security;

import java.util.Arrays;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final AuthenticationProvider authProvider;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthFilter, AuthenticationProvider authProvider) {
        this.jwtAuthFilter = jwtAuthFilter;
        this.authProvider = authProvider;
    }

    // @Bean
    // public CorsFilter corsFilter() {
    //     CorsConfiguration config = new CorsConfiguration();
    //     config.setAllowedOrigins(Arrays.asList("http://localhost:3000")); // Allow frontend origin
    //     config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
    //     config.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type"));
    //     config.setAllowCredentials(true);

    //     UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    //     source.registerCorsConfiguration("/**", config);
    //     return new CorsFilter(source);
    // }
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(Arrays.asList("http://localhost:3000")); // Set frontend origin
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type"));
        config.setAllowCredentials(true);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors((cors -> cors.configurationSource(corsConfigurationSource())))
                .csrf(csrf -> csrf.disable()) // Disable CSRF protection
                .authorizeHttpRequests((authz) -> authz
                        // .requestMatchers(HttpMethod.POST, "/tournaments/{tournamentId}/events/{eventId}/addPlayer/{playerId}").hasAnyRole("ADMIN", "USER")
                        .requestMatchers("/api/v1/auth/**").permitAll() // Allow all requests to /api/v1/auth
                        .requestMatchers("/error").permitAll() // Allow all requests to /error
                        .requestMatchers(HttpMethod.GET, "/tournaments").permitAll() // Allow all GET requests to tournaments
                        .requestMatchers(HttpMethod.GET, "/tournaments/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/tournaments", "/tournaments/**").permitAll() // Allow all GET requests to tournaments
                        .requestMatchers(HttpMethod.POST, "/tournaments").hasRole("ADMIN") // Only admins can POST
                        .requestMatchers(HttpMethod.POST, "/tournaments/**").hasRole("ADMIN") // Only admins can POST
                        .requestMatchers(HttpMethod.PUT, "/tournaments", "/tournaments/**").hasRole("ADMIN") // Only admins can PUT
                        .requestMatchers(HttpMethod.DELETE, "/tournaments", "/tournaments/**").hasRole("ADMIN") // Only admins can
                        .requestMatchers(HttpMethod.GET, "/users", "/users/**").hasRole("ADMIN") // Only admins can GET users
                        .requestMatchers(HttpMethod.PUT, "/users/*").hasRole("ADMIN") // Only admins can PUT users (next time users should be able to update their own stuff)
                        .requestMatchers(HttpMethod.DELETE, "/users/*").hasRole("ADMIN") // Only admins can DELETE users
                        .requestMatchers(HttpMethod.GET, "/players", "/players/**").permitAll() // Allow all GET requests to players
                        .requestMatchers(HttpMethod.POST, "/players", "/players/**").hasRole("ADMIN") // Only admins can POST players
                        .requestMatchers(HttpMethod.PUT, "/players/*").hasRole("ADMIN") // Only admins can PUT players
                        .requestMatchers(HttpMethod.DELETE, "/players/*").hasRole("ADMIN") // Only admins can DELETE players
                        .anyRequest().authenticated() // All other requests require authentication

                )

                // ensure that the application won’t create any session in our stateless REST
                // APIs
                .sessionManagement(configurer -> configurer.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .httpBasic(Customizer.withDefaults())
                .formLogin(form -> form.disable())
                .headers(header -> header.disable()) // disable the security headers, as we do not return HTML in our
                                                     // APIs
                .authenticationProvider(authProvider)
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

}
