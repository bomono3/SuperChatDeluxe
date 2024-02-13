package SuperChatDeluxe.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf().disable()
        .authorizeHttpRequests()
        .requestMatchers(AUTH_WHITE_LIST).permitAll()
        .requestMatchers(HttpMethod.POST,"/api/admin/").hasRole("ADMIN")
        .requestMatchers(HttpMethod.PATCH,"/api/admin/").hasRole("ADMIN")
        .requestMatchers(HttpMethod.POST,"/api/register/").permitAll()
        .requestMatchers(HttpMethod.PATCH,"/api/registration/").permitAll()
        .requestMatchers(HttpMethod.GET,"/api/users/search/").permitAll()
        .requestMatchers(HttpMethod.GET,"/api/recipes/favorites/users/").permitAll()
        .requestMatchers(HttpMethod.GET,"/api/recipes/search/").permitAll()
        .requestMatchers(HttpMethod.GET,"/api/users").permitAll()
        .requestMatchers(HttpMethod.GET,"/api/users/").permitAll()
        .requestMatchers("/authenticate").permitAll()
        .anyRequest().authenticated()
        .and()
        .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
