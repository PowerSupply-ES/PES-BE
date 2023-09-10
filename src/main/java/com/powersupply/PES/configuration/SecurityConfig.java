package com.powersupply.PES.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception{
        return httpSecurity
                .httpBasic().disable() // UI쪽 들어오는거 disable
                .csrf().disable() // cross site 기능
                .cors().and() // cross site 도메인 다른 경우 허용
                .authorizeRequests()
                .antMatchers("/","/signin","/signup").permitAll() // main 페이지는 언제나 접근 가능
                .antMatchers("/css/**", "/js/**", "/img/**").permitAll()  // 정적 리소스에 대한 접근 허용
                .anyRequest().authenticated()
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .build();
    }
}
