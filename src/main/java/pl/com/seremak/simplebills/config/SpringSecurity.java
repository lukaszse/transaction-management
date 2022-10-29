package pl.com.seremak.simplebills.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.server.resource.web.server.ServerBearerTokenAuthenticationConverter;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebFluxSecurity
public class SpringSecurity {

    @Value("${simpleBillsGuiAppUrl}")
    private String simpleBillsGuiApp;

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(
            ServerHttpSecurity http) {
        return http
                .csrf()
                .disable()
//                .csrfTokenRepository(CookieServerCsrfTokenRepository.withHttpOnlyFalse())
//                .and()
                .authorizeExchange()
                .pathMatchers("/users/admin", "/users/admin/*").hasAuthority("ROLE_ADMIN")
                .anyExchange().permitAll()
                .and()
                .oauth2ResourceServer().bearerTokenConverter(bearerTokenConverter())
                .jwt()
                .and().and()
                .build();
    }

    @Bean
    public CorsConfigurationSource corsConfiguration() {
        final CorsConfiguration corsConfig = new CorsConfiguration();
        corsConfig.applyPermitDefaultValues();
        corsConfig.addAllowedMethod(HttpMethod.PUT);
        corsConfig.addAllowedMethod(HttpMethod.DELETE);
        corsConfig.setAllowedOrigins(List.of(simpleBillsGuiApp));
        final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfig);
        return source;
    }

    private ServerAuthenticationConverter bearerTokenConverter() {
        ServerBearerTokenAuthenticationConverter authenticationConverter = new ServerBearerTokenAuthenticationConverter();
        authenticationConverter.setAllowUriQueryParameter(true);
        return authenticationConverter;
    }

}
