package org.efrei.argosbackend.adapters.in.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.efrei.argosbackend.domain.model.enums.ProfileType;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

@Configuration
public class SecurityConfig {

    private static final String LOGIN_URL = "/auth/login";
    private static final String LOGOUT_URL = "/auth/logout";
    private static final String SESSION_COOKIE = "JSESSIONID";
    private static final String OPERATIONS_MANAGER = ProfileType.OPERATIONS_MANAGER.name();
    private static final String[] API_DOCUMENTATION_URLS = {"/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**"};

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/actuator/health", "/error").permitAll()
                        .requestMatchers(API_DOCUMENTATION_URLS).permitAll()
                        .requestMatchers(HttpMethod.POST, "/users").hasRole(OPERATIONS_MANAGER)
                        .requestMatchers(HttpMethod.PUT, "/incidents/*/provider").hasRole(OPERATIONS_MANAGER)
                        .requestMatchers(HttpMethod.POST, "/incidents/*/resolution").hasRole(OPERATIONS_MANAGER)
                        .requestMatchers(HttpMethod.POST, "/incidents/*/evaluation").hasRole(OPERATIONS_MANAGER)
                        .anyRequest().authenticated())
                .formLogin(form -> form
                        .loginProcessingUrl(LOGIN_URL)
                        .successHandler(this::respondNoContent)
                        .failureHandler(this::respondUnauthorized)
                        .permitAll())
                .logout(logout -> logout
                        .logoutUrl(LOGOUT_URL)
                        .deleteCookies(SESSION_COOKIE)
                        .logoutSuccessHandler(new HttpStatusReturningLogoutSuccessHandler()))
                .exceptionHandling(exceptions -> exceptions
                        .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)))
                .csrf(csrf -> csrf
                        .spa()
                        .csrfTokenRepository(csrfTokenRepository())
                        .ignoringRequestMatchers(LOGIN_URL))
                .addFilterAfter(new CsrfCookieFilter(), BasicAuthenticationFilter.class)
                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    private CookieCsrfTokenRepository csrfTokenRepository() {
        CookieCsrfTokenRepository repository = CookieCsrfTokenRepository.withHttpOnlyFalse();
        repository.setCookiePath("/");
        return repository;
    }

    private void respondNoContent(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        response.setStatus(HttpStatus.NO_CONTENT.value());
    }

    private void respondUnauthorized(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) {
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
    }
}
