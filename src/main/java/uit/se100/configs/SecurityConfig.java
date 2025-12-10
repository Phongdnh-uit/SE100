package uit.se100.configs;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import uit.se100.constants.SecurityConstant;
import uit.se100.securities.jwt.CustomJwtConverter;

@RequiredArgsConstructor
@Configuration
@EnableMethodSecurity
@EnableWebSecurity
public class SecurityConfig implements WebMvcConfigurer {

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http, CustomJwtConverter jwtConverter)
            throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults())
                .authorizeHttpRequests(
                        auth ->
                                auth.requestMatchers(SecurityConstant.PUBLIC_URLS).permitAll()
                                        .requestMatchers(HttpMethod.GET, SecurityConstant.PUBLIC_GET_URLS).permitAll()
                                        .requestMatchers(HttpMethod.POST, SecurityConstant.PUBLIC_POST_URLS).permitAll()
                                        .requestMatchers(HttpMethod.PUT, SecurityConstant.PUBLIC_PUT_URLS).permitAll()
                                        .requestMatchers(HttpMethod.PATCH, SecurityConstant.PUBLIC_PATCH_URLS).permitAll()
                                        .requestMatchers(HttpMethod.DELETE, SecurityConstant.PUBLIC_DELETE_URLS).permitAll()
                                        .anyRequest()
                                        .authenticated())
                .oauth2ResourceServer(
                        oauth2 -> oauth2.jwt(jwt -> jwt.jwtAuthenticationConverter(jwtConverter)))
                .formLogin(AbstractHttpConfigurer::disable)
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        return http.build();
    }
}
