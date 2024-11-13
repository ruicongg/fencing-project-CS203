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
    // CorsConfiguration config = new CorsConfiguration();
    // config.setAllowedOrigins(Arrays.asList("http://localhost:3000")); // Allow
    // frontend origin
    // config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE",
    // "OPTIONS"));
    // config.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type"));
    // config.setAllowCredentials(true);

    // UrlBasedCorsConfigurationSource source = new
    // UrlBasedCorsConfigurationSource();
    // source.registerCorsConfiguration("/**", config);
    // return new CorsFilter(source);
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
                        .requestMatchers("/api/v1/auth/**").permitAll()
                        .requestMatchers("/error").permitAll()
                        // Specific event endpoints first
                        .requestMatchers(HttpMethod.POST, "/tournaments/{tournamentId}/events/{eventId}/players/{username}").hasAnyRole("USER", "ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/tournaments/{tournamentId}/events/{eventId}/players").hasAnyRole("USER", "ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/tournaments/{tournamentId}/events/{eventId}/players/{username}").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/tournaments/{tournamentId}/events").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/tournaments/{tournamentId}/events/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/tournaments/{tournamentId}/events/**").hasRole("ADMIN")
                        // Then more general tournament endpoints
                        .requestMatchers(HttpMethod.GET, "/tournaments/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/tournaments/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/tournaments/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/tournaments/**").hasRole("ADMIN")
                        // Then to get all matches
                        .requestMatchers(HttpMethod.GET, "/matches").permitAll()
                        // Then user and player endpoints
                        .requestMatchers(HttpMethod.GET, "/users/id").hasRole("USER")
                        .requestMatchers(HttpMethod.GET, "/users/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/users/*").hasAnyRole("USER", "ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/users/*").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/player/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/players/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/players/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/players/*").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/players/*").hasRole("ADMIN")
                        // Finally, the catch-all rule
                        .anyRequest().authenticated()

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
