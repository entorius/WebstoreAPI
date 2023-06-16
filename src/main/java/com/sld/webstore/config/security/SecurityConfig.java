package com.sld.webstore.config.security;

import com.sld.webstore.config.ApplicationProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.security.oauth2.resource.OAuth2ResourceServerProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2ErrorCodes;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig implements WebMvcConfigurer {

    private final AuthenticationErrorHandler authenticationErrorHandler;

    private final OAuth2ResourceServerProperties resourceServerProps;

    private final ApplicationProperties applicationProps;

    @Bean
    public WebSecurityCustomizer webSecurity() {
        final var exclusionRegex = "^(?!%s|%s).*$".formatted(
                "/api/messages/protected",
                "/api/messages/admin"
        );

        return web ->
                web.ignoring()
                        .requestMatchers(exclusionRegex);
    }

    @Bean
    public SecurityFilterChain httpSecurity(final HttpSecurity http) throws Exception {
        return http.authorizeHttpRequests()
                .requestMatchers("/api/messages/protected", "/api/messages/admin")
                .authenticated()
                .anyRequest()
                .permitAll()
                .and()
                .cors()
                .and()
                .csrf()
                .disable()
                .oauth2ResourceServer()
                .authenticationEntryPoint(authenticationErrorHandler)
                .jwt()
                .decoder(makeJwtDecoder())
                .jwtAuthenticationConverter(makePermissionsConverter())
                .and()
                .and()
                .build();
    }

    @Override
    public void addCorsMappings(final CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins(applicationProps.getClientOriginUrl())
                .allowedHeaders(HttpHeaders.AUTHORIZATION, HttpHeaders.CONTENT_TYPE)
                .allowedMethods(
                        HttpMethod.GET.name(),
                        HttpMethod.POST.name(),
                        HttpMethod.OPTIONS.name(),
                        HttpMethod.PUT.name()
                )
                .maxAge(86400);
    }

    private JwtDecoder makeJwtDecoder() {
        final var issuer = resourceServerProps.getJwt().getIssuerUri();
        final var decoder = JwtDecoders.<NimbusJwtDecoder>fromIssuerLocation(issuer);
        final var withIssuer = JwtValidators.createDefaultWithIssuer(issuer);
        final var tokenValidator = new DelegatingOAuth2TokenValidator<>(withIssuer, this::withAudience);

        decoder.setJwtValidator(tokenValidator);
        return decoder;
    }

    private OAuth2TokenValidatorResult withAudience(final Jwt token) {
        final var audienceError = new OAuth2Error(
                OAuth2ErrorCodes.INVALID_TOKEN,
                "The token was not issued for the given audience",
                "https://datatracker.ietf.org/doc/html/rfc6750#section-3.1"
        );

        return token.getAudience().contains(applicationProps.getAudience())
                ? OAuth2TokenValidatorResult.success()
                : OAuth2TokenValidatorResult.failure(audienceError);
    }

    private JwtAuthenticationConverter makePermissionsConverter() {
        final var jwtAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
        jwtAuthoritiesConverter.setAuthoritiesClaimName("permissions");
        jwtAuthoritiesConverter.setAuthorityPrefix("");

        final var jwtAuthConverter = new JwtAuthenticationConverter();
        jwtAuthConverter.setJwtGrantedAuthoritiesConverter(jwtAuthoritiesConverter);

        return jwtAuthConverter;
    }
}

