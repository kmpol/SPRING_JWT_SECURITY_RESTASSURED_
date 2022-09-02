package pl.karol.jwt_based_app.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import pl.karol.jwt_based_app.filter.CustomAuthFilter;
import pl.karol.jwt_based_app.filter.CustomAuthorizationFilter;
import pl.karol.jwt_based_app.user.UserService;

@Configuration
@EnableWebSecurity
@Lazy
public class SecurityConfig {

    private final UserDetailsService userDetailsService;
    private final UserService userService;

    public SecurityConfig(UserDetailsService userDetailsService, UserService userService) {
        this.userDetailsService = userDetailsService;
        this.userService = userService;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf().disable();
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        http.authorizeRequests(requests -> requests
                .antMatchers("/api/tokens/refresh/**").permitAll()
                .antMatchers("/api/register").permitAll()
                .antMatchers(HttpMethod.GET,"/api/users/**").hasAnyAuthority("USER")
                .antMatchers(HttpMethod.POST, "/api/users/save/**").hasAnyAuthority("USER")
                .antMatchers("/api/users/tasks/**").authenticated()
                .anyRequest().authenticated()
        );

        final AuthenticationConfiguration configuration = http.getSharedObject(AuthenticationConfiguration.class);
        http.addFilter(new CustomAuthFilter(authenticationManager(configuration)));
        http.addFilterBefore(new CustomAuthorizationFilter(userService), CustomAuthFilter.class);
        return http.build();
    }

    @Bean
    public void configure (AuthenticationManagerBuilder auth) throws Exception{
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception{
        return authenticationConfiguration.getAuthenticationManager();
    }
    @Bean
    PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }
}
