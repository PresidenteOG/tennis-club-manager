package Tennis_ERP.TennisErp.springsecurity;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .headers(headers -> headers.frameOptions(frame -> frame.disable()))
            .authorizeHttpRequests(auth -> auth
                // 1. PÚBLICO
                .requestMatchers("/login", "/css/**", "/js/**", "/images/**", "/layout/**", "/vendor/**", "/uploads/**", "/h2-console/**", "/403", "/404").permitAll()

                // 2. RUTAS COMPARTIDAS (JUGADOR Y ADMIN)
                .requestMatchers("/menu_principal", "/calendario", "/perfil/**", "/equipos", "/api/dashboard/**").hasAnyAuthority("ROLE_ADMIN", "ROLE_JUGADOR")

                // 3. RUTAS EXCLUSIVAS DEL ADMIN
                .requestMatchers("/menu_admin/**", "/usuarios/**", "/jugadores/**", "/trabajadores/**", "/ligas/**", "/categorias/**", "/equipos/**", "/adminpistas/**", "/competicion/**", "/mail").hasAuthority("ROLE_ADMIN")

                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .successHandler((request, response, authentication) -> {
                    // Redirección inteligente al iniciar sesión
                    if (authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
                        response.sendRedirect("/menu_admin");
                    } else {
                        response.sendRedirect("/menu_principal");
                    }
                })
                .permitAll()
            )
            .logout(logout -> logout
                .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                .logoutSuccessUrl("/login?logout")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .permitAll()
            )
            .exceptionHandling(exception -> exception
                .accessDeniedPage("/403")
            );

        return http.build();
    }
}