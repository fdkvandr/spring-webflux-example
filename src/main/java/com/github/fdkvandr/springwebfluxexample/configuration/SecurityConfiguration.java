package com.github.fdkvandr.springwebfluxexample.configuration;

import com.github.fdkvandr.springwebfluxexample.service.UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UserDetailsRepositoryReactiveAuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
public class SecurityConfiguration {

    @Bean
        public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
            return http
                    .csrf(ServerHttpSecurity.CsrfSpec::disable)
                    .authorizeExchange(authorizeExchangeSpec -> authorizeExchangeSpec
                            .pathMatchers(HttpMethod.POST, "/animes/**").hasRole("ADMIN")
                            .pathMatchers(HttpMethod.PUT, "/animes/**").hasRole("ADMIN")
                            .pathMatchers(HttpMethod.DELETE, "/animes/**").hasRole("ADMIN")
                            .pathMatchers(HttpMethod.GET, "/animes/**").hasRole("USER")
                            .anyExchange().authenticated())
                    .formLogin(Customizer.withDefaults())
                    .httpBasic(Customizer.withDefaults())
                    .build();
    }

    @Bean
    public ReactiveAuthenticationManager authenticationManager(UserService userService) {
        return new UserDetailsRepositoryReactiveAuthenticationManager(userService);
    }
}
