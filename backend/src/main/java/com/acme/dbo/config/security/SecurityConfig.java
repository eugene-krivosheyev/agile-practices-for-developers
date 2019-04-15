package com.acme.dbo.config.security;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.AnonymousAuthenticationFilter;
import org.springframework.security.web.authentication.Http403ForbiddenEntryPoint;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.security.web.util.matcher.*;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import static lombok.AccessLevel.PRIVATE;
import static lombok.AccessLevel.PUBLIC;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
@ComponentScan("com.acme")
@AllArgsConstructor(access = PUBLIC)
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    private static final RequestMatcher PROTECTED_URLS = new OrRequestMatcher(
        new AndRequestMatcher(
            new RequestHeaderRequestMatcher("X-API-VERSION"),
            new AntPathRequestMatcher("/api/**")
        ),
        new AndRequestMatcher(
            new RequestHeaderRequestMatcher("X-API-VERSION"),
            new AntPathRequestMatcher("/api/authenticate/**", "POST")
        )
    );
    private static final RequestMatcher PUBLIC_URLS = new OrRequestMatcher(
        new AndRequestMatcher(
            new RequestHeaderRequestMatcher("X-API-VERSION"),
            new NegatedRequestMatcher(PROTECTED_URLS)
        ),
        new AndRequestMatcher(
                new RequestHeaderRequestMatcher("X-API-VERSION"),
                new AntPathRequestMatcher("/api/authenticate/login", "POST")
        ),
        new AndRequestMatcher(
            new RequestHeaderRequestMatcher("X-API-VERSION"),
            new AntPathRequestMatcher("/api/client", "POST")
        ),
        new AndRequestMatcher(
            new RequestHeaderRequestMatcher("X-API-VERSION"),
            new AntPathRequestMatcher("/api/client/getCode", "POST")
        ),
        new AndRequestMatcher(
            new RequestHeaderRequestMatcher("X-API-VERSION"),
            new AntPathRequestMatcher("/api/client/password/reset")
        ),
        new AndRequestMatcher(
            new RequestHeaderRequestMatcher("X-API-VERSION"),
            new AntPathRequestMatcher("/api/client/password/update")
        ),
        new AndRequestMatcher(
            new RequestHeaderRequestMatcher("X-API-VERSION"),
            new AntPathRequestMatcher("/api/client/recovery", "POST")
        ),
        new AndRequestMatcher(
            new RequestHeaderRequestMatcher("X-API-VERSION"),
            new AntPathRequestMatcher("/api/verification")
        )
    );

    @NonNull TokenAuthenticationProvider authenticationProvider;

    @Override
    public void configure(final WebSecurity web) {
        web.ignoring().requestMatchers(PUBLIC_URLS);
    }

    @Override
    protected void configure(final HttpSecurity http) throws Exception {
        http.cors()
            .and()
                .sessionManagement()
                .sessionCreationPolicy(STATELESS)
            .and()
                .exceptionHandling()
                .authenticationEntryPoint(new Http403ForbiddenEntryPoint())
                .defaultAuthenticationEntryPointFor(forbiddenEntryPoint(), PROTECTED_URLS)
            .and()
                .addFilterBefore(restAuthenticationFilter(), AnonymousAuthenticationFilter.class)//BasicAuthenticationFilter
                .authenticationProvider(authenticationProvider)
                .authorizeRequests()
                .requestMatchers(PROTECTED_URLS)
                .authenticated()
            .and()
                .csrf().disable()
                .formLogin().disable()
                .httpBasic().disable()
                .logout().disable();
    }

    /**
     * Known issue with redirect
     * https://stackoverflow.com/questions/31954292/spring-security-with-basic-auth-redirecting-to-error-for-invalid-credentials
     */
    @Bean
    TokenAuthenticationFilter restAuthenticationFilter() throws Exception {
        final TokenAuthenticationFilter filter = new TokenAuthenticationFilter(PROTECTED_URLS);
        filter.setAuthenticationManager(authenticationManager());
        filter.setAuthenticationSuccessHandler(successHandler());
        return filter;
    }

    @Bean
    SimpleUrlAuthenticationSuccessHandler successHandler() {
        final SimpleUrlAuthenticationSuccessHandler successHandler = new SimpleUrlAuthenticationSuccessHandler();
        successHandler.setRedirectStrategy(new NoRedirectStrategy());
        return successHandler;
    }

    /**
     * Disable Spring boot automatic filter registration.
     */
    @Bean
    FilterRegistrationBean disableAutoRegistration(final TokenAuthenticationFilter filter) {
        final FilterRegistrationBean registration = new FilterRegistrationBean<>(filter);
        registration.setEnabled(false);
        return registration;
    }

    @Bean
    AuthenticationEntryPoint forbiddenEntryPoint() {
        return new HttpStatusEntryPoint(FORBIDDEN);
    }

    @Bean //TODO remove?
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurerAdapter() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry
                    .addMapping("/**")
                    .allowedOrigins("*")
                    .allowedMethods("DELETE", "POST", "GET", "UPDATE", "PUT")
                    .allowCredentials(false).maxAge(3600);
            }
        };
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", new CorsConfiguration().applyPermitDefaultValues());
        return source;
    }
}
