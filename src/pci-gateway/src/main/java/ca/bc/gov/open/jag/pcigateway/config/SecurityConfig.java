package ca.bc.gov.open.jag.pcigateway.config;

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
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // PCI Gateway is stateless, no session management needed
            .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
            
            // CSRF not needed for stateless REST APIs
            .csrf().disable()
            
            // Authorization rules - using antMatchers for Spring Security 5.x (Spring Boot 2.7)
            .authorizeRequests(authz -> authz
                // Allow legacy redirect endpoints (authenticated via hash)
                .antMatchers("/pcigw/scripts/**").permitAll()
                
                // REST API endpoints require authentication (handled by passcode)
                .antMatchers("/pcigw/v1/**").permitAll()
                
                // Actuator endpoints - only expose health and info
                .antMatchers("/actuator/health", "/actuator/info").permitAll()
                .antMatchers("/actuator/**").denyAll()
                
                // Deny all other requests
                .anyRequest().authenticated()
            )
            
            // Add security headers for PCI compliance
            .headers(headers -> headers
                .frameOptions().deny()
                .contentTypeOptions().and()
                .xssProtection().and()
                .httpStrictTransportSecurity()
                    .includeSubDomains(true)
                    .maxAgeInSeconds(31536000)
            );

        return http.build();
    }
}
