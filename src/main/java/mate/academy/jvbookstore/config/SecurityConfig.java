package mate.academy.jvbookstore.config;

import lombok.RequiredArgsConstructor;
import mate.academy.jvbookstore.exception.CustomAccessDeniedHandler;
import mate.academy.jvbookstore.exception.CustomAuthenticationEntryPoint;
import mate.academy.jvbookstore.model.Role.RoleName;
import mate.academy.jvbookstore.security.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@RequiredArgsConstructor
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    private final CustomAccessDeniedHandler accessDeniedHandler;

    private final CustomAuthenticationEntryPoint entryPoint;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .cors(cors -> cors.disable())
                .csrf(csrf -> csrf.disable())
                .exceptionHandling(exc -> exc
                    .accessDeniedHandler(accessDeniedHandler)
                    .authenticationEntryPoint(entryPoint)
                )
                .authorizeHttpRequests(auth -> auth
                    .requestMatchers("/auth/**", 
                                    "/v3/api-docs/**",
                                    "/swagger-ui/**",
                                    "/v2/api-docs/**",
                                    "/swagger-resources/**")
                    .permitAll()
                    .requestMatchers(HttpMethod.POST, "/books")
                    .hasRole(RoleName.ADMIN.toString())
                    .requestMatchers(HttpMethod.DELETE, "/books/**")
                    .hasRole(RoleName.ADMIN.toString())
                    .requestMatchers(HttpMethod.PUT, "/books/**")
                    .hasRole(RoleName.ADMIN.toString())
                    .requestMatchers("/users/**")
                    .hasRole(RoleName.ADMIN.toString())
                    .anyRequest()
                    .authenticated()
                )
                .sessionManagement(session -> session
                    .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtAuthenticationFilter, 
                    UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration authConfiguration) throws Exception {
        return authConfiguration.getAuthenticationManager();
    }
}
