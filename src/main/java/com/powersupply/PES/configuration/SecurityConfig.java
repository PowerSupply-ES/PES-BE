package com.powersupply.PES.configuration;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${admin.username}")
    private String adminUsername;

    @Value("${admin.password}")
    private String adminPassword;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                .httpBasic().and() // 기본 인증 활성화
                .csrf().disable() // CSRF 비활성화
                .cors().configurationSource(corsConfigurationSource()).and() // CORS 설정
                .authorizeRequests()
                .antMatchers("/api/signin", "/api/signup").permitAll() // 기본 요청 허용
                .antMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-resources/**", "/webjars/**").authenticated() // Swagger 경로에 대한 접근 제한
                .antMatchers(HttpMethod.GET, "/api/problemlist", "/api/answer/**", "/api/comment/**", "/api/answerlist/**", "/api/rank/**", "/api/notice/**", "/api/admin/**").permitAll()
                .antMatchers(HttpMethod.POST, "/api/comment/**").hasRole("REGULAR_STUDENT")
                .antMatchers(HttpMethod.POST, "/api/notice/**").hasRole("MANAGER")
                .antMatchers(HttpMethod.PATCH, "/api/notice/**").hasRole("MANAGER")
                .anyRequest().hasRole("USER")
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .addFilterBefore(new HeaderLoggingFilter(), UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(new JwtFilter(secretKey), UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("https://www.pes23.com", "https://pes23.com", "http://localhost:8080"));
        configuration.setAllowedMethods(Arrays.asList("POST", "GET", "OPTIONS", "PUT", "DELETE", "PATCH"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Cache-Control", "Content-Type"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }

    @Bean
    public UserDetailsService userDetailsService() {
        InMemoryUserDetailsManager manager = new InMemoryUserDetailsManager();
        manager.createUser(User.withUsername(adminUsername).password("{noop}" + adminPassword).roles("ADMIN").build());
        return manager;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new NoOpPasswordEncoder();
    }
}

class NoOpPasswordEncoder implements PasswordEncoder {
    @Override
    public String encode(CharSequence rawPassword) {
        return rawPassword.toString();
    }

    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        return rawPassword.toString().equals(encodedPassword);
    }
}
