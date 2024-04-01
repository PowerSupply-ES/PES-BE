package com.powersupply.PES.configuration;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.access.expression.SecurityExpressionHandler;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.expression.DefaultWebSecurityExpressionHandler;
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

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception{
        return httpSecurity
                .httpBasic().disable() // UI쪽 들어오는거 disable
                .csrf().disable() // cross site 기능
                .cors().configurationSource(corsConfigurationSource()).and() // cross site 도메인 다른 경우 허용
                .authorizeRequests()
                .antMatchers("/api/signin","/api/signup").permitAll() // 기본 요청 언제나 접근 가능
                .antMatchers(HttpMethod.GET, "/api/problemlist" , "/api/answer/**", "/api/comment/**", "/api/answerlist/**", "/api/rank").permitAll()
                .antMatchers(HttpMethod.POST,"/api/comment/**").hasRole("REGULAR_STUDENT")
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
        configuration.setAllowedOrigins(Arrays.asList("https://www.pes23.com", "https://pes23.com")); // 여기에 IP 추가
        configuration.setAllowedMethods(Arrays.asList("POST", "GET", "OPTIONS", "PUT", "DELETE"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Cache-Control", "Content-Type"));
        configuration.setAllowCredentials(true); // 필요한 경우, 쿠키와 함께 요청을 보내는 것을 허용

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration); // 모든 URL에 대해 설정 적용

        return source;
    }

    @Bean
    public SecurityExpressionHandler customWebSecurityExpressionHandler() {
        DefaultWebSecurityExpressionHandler defaultWebSecurityExpressionHandler = new DefaultWebSecurityExpressionHandler();
        defaultWebSecurityExpressionHandler.setRoleHierarchy(roleHierarchy());
        return defaultWebSecurityExpressionHandler;
    }

    @Bean
    public RoleHierarchy roleHierarchy() {
        RoleHierarchyImpl roleHierarchy = new RoleHierarchyImpl();
        //roleHierarchy.setHierarchy("ROLE_MANAGER > ROLE_REGULAR_STUDENT > ROLE_USER and ROLE_NEW_STUDENT > ROLE_USER");
        String hierarchy =  "ROLE_MANAGER > ROLE_REGULAR_STUDENT > ROLE_USER\n" +
                "ROLE_NEW_STUDENT > ROLE_USER";
        roleHierarchy.setHierarchy(hierarchy);
        return roleHierarchy;
    }
}
