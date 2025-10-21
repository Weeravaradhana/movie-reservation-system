package com.group8.movie_reservation_system.config;

import com.group8.movie_reservation_system.exception.EntryNotFoundException;
import com.group8.movie_reservation_system.repo.UserRepository;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final UserRepository userRepo;

    @Bean
    public UserDetailsService userDetailsService() {
        return username -> userRepo.findByUsername(username)
                .map(u -> User.withUsername(u.getUsername())
                        .password(u.getPasswordHash())
                        .roles(u.getRoles().stream()
                                .map(r -> r.getRoleName().replace("ROLE_", ""))
                                .toArray(String[]::new))
                        .build())
                .orElseThrow(() -> new EntryNotFoundException("User not found"));
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/login", "/signup", "/check","/css/**", "/js/**").permitAll()
                        .requestMatchers("/user-management-service/api/v1/users/**").permitAll()
                        .requestMatchers("/deal-management-service/api/v1/**").permitAll()
                        .requestMatchers("/seat-management-service/api/v1/**").permitAll()
                        .requestMatchers("/booking-management-service/api/v1/**").permitAll()
                        .requestMatchers("/movie-management-service/api/v1/**").permitAll()
                        .requestMatchers("/payment-management-service/api/v1/**").permitAll()
                        .requestMatchers("/review-management-service/api/v1/**").permitAll()
                        .requestMatchers("/log-management-service/api/v1/**").permitAll()
                        .requestMatchers("/ticket-management-service/api/v1/**").permitAll()
                        .requestMatchers("/admin-management-service/api/v1/**").permitAll()
                        .requestMatchers("/admin/dashboard/**").permitAll()
                        .requestMatchers("/customer/dashboard/**").permitAll()
                        .requestMatchers("/**").permitAll()
                        .requestMatchers("/dashboard", "/booking/**").hasAnyRole("USER", "ADMIN")
                        .requestMatchers("/", "/login", "/signup", "/movies", "/movies/**", "/css/**", "/js/**", "/images/**").permitAll()
                        .requestMatchers("/dashboard").authenticated()
                        .anyRequest().authenticated()
                )

                .formLogin(form -> form
                        .loginPage("/login")
                        .successHandler((request, response, authentication) -> {
                            response.setStatus(HttpServletResponse.SC_OK);
                        })
                        .failureHandler((request, response, exception) -> {
                            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid credentials");
                        })
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout=true")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                        .permitAll()
                )

                .exceptionHandling(e -> e
                        .authenticationEntryPoint((request, response, authException) ->
                                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized access"))
                        .accessDeniedHandler((request, response, accessDeniedException) ->
                                response.sendError(HttpServletResponse.SC_FORBIDDEN, "Forbidden access"))
                );

        return http.build();
    }



//    @Bean
//    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//        http
//            .csrf(csrf -> csrf.disable())
//            .authorizeHttpRequests(authz -> authz
//                // Public pages
//                .requestMatchers("/", "/login", "/signup", "/movies", "/movies/**", "/css/**", "/js/**", "/images/**").permitAll()
//                // User pages
//                .requestMatchers("/dashboard", "/booking/**").hasAnyRole("USER", "ADMIN")
//                // Admin pages
//                .requestMatchers("/admin/**").hasRole("ADMIN")
//                // API endpoints
//                .requestMatchers("/api/**").permitAll()
//                .anyRequest().authenticated()
//            )
//            .formLogin(form -> form
//                .loginPage("/login")
//                .defaultSuccessUrl("/dashboard", true)
//                .failureUrl("/login?error=true")
//                .permitAll()
//            )
//            .logout(logout -> logout
//                .logoutUrl("/logout")
//                .logoutSuccessUrl("/")
//                .invalidateHttpSession(true)
//                .deleteCookies("JSESSIONID")
//                .permitAll()
//            )
//            .sessionManagement(session -> session
//                .maximumSessions(1)
//                .maxSessionsPreventsLogin(false)
//            );
//
//        return http.build();
//    }
}