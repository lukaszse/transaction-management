package pl.com.seremak.simplebills.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity
public class SpringSecurity {

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(
            ServerHttpSecurity http) {
        return http.authorizeExchange()
                .pathMatchers("/users/admin", "/users/admin/*").hasAuthority("ROLE_ADMIN")
                .anyExchange().permitAll()
                .and()
                .oauth2ResourceServer().jwt()
                .and().and()
                .cors().disable()
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .build();

//        return http.authorizeExchange()
//                .pathMatchers("/users/admin", "/users/admin/*").hasAuthority("ROLE_ADMIN")
//                .anyExchange().hasAuthority("SCOPE_write")
//                .and()
//                .cors()
//                .and()
//                .csrf(ServerHttpSecurity.CsrfSpec::disable)
//                .oauth2ResourceServer()
//                .jwt()
//                .and()
//                .and()
//                .build();
    }
}
