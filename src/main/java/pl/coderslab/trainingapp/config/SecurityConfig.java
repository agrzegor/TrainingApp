package pl.coderslab.trainingapp.config;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

@Configuration
@EnableWebSecurity
@AllArgsConstructor
public class SecurityConfig {


    private final UserDetailsService userDetailsService;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(10);
    }

    /**
     *
     * @param httpSecurity
     * @return Baza -> User -> SecurityUser (UserDetails) -> getAuthorities() -> Authentication -> handler
     * <p>
     * Spring Security wymaga tokenu CSRF przy wszystkich żądaniach typu POST/PUT/DELETE.
     * Wyłączenie CSRF jest typowe dla REST API, bo nie używamy formularzy z tokenami.
     * Bez tego, każde POST bez tokenu dawałoby 403 Forbidden.
     * <p>
     * .permitAll()) - kazdy moze wejsc na dany endopint, inne wymagaja autoryzacji
     * <p>
     * .build () Tworzy obiekt SecurityFilterChain, który Spring używa w filtrach HTTP, żeby sprawdzać autoryzację,
     * uwierzytelnianie i inne reguły bezpieczeństwa.
     * Bez tego chain nie istnieje i Spring nie wie, jak filtrować żądania.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/signup", "/api/login").permitAll()
                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider(passwordEncoder()))
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    /**
     * Tworzy bean, który Spring Security może użyć po udanym logowaniu.
     * Interfejs AuthenticationSuccessHandler definiuje jedną metodę:
     * Spring wywołuje tę metodę automatycznie, gdy logowanie się powiedzie.
     * Authentication przechowuje dane zalogowanego użytkownika (UserDetails w Spring).
     * getAuthorities() zwraca wszystkie role/uprawnienia użytkownika
     */
    @Bean
    public AuthenticationSuccessHandler authenticationSuccessHandler() {
        return (request, response, authentication) -> {

            Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
            boolean isTrainer = authorities.stream()
                    .anyMatch(a -> Objects.equals(a.getAuthority(), "TRAINER"));
            if (isTrainer) {
                response.sendRedirect("/api/trainers/me");
            } else {
                response.sendRedirect("/api/customers/me");
            }
        };
    }


    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowedOrigins(List.of("http://localhost:8080"));
        configuration.setAllowedMethods(List.of("GET","POST"));
        configuration.setAllowedHeaders(List.of("Authorization","Content-Type"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration("/**",configuration);

        return source;
    }

    /**
     *
     * Tworzymy bean typu DaoAuthenticationProvider.
     * Spring Security użyje go do obsługi logowania username/password.
     * PasswordEncoder jest wstrzykiwany, żeby Spring mógł poprawnie sprawdzić hasło.
     * userDetailsService to  serwis, który implementuje UserDetailsService.
     * To on odpowiada za pobranie użytkownika z bazy i zwrócenie obiektu UserDetails.
     * Dzięki temu Spring sprawdzi, czy podane przez użytkownika hasło pasuje do tego w bazie
     *
     */
    @Bean
    public DaoAuthenticationProvider authenticationProvider(
            PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder);
        return provider;
    }

//    @Bean
//   public UserDetailsService userDetailsService() {
//        return username -> userRepository.findByEmail(username)
//                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
//    }
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
