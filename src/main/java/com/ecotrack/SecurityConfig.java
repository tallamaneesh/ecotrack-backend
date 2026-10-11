package com.ecotrack;
import com.ecotrack.repo.UserRepo;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.*;
@Configuration
public class SecurityConfig {
  @Bean PasswordEncoder encoder() { return new BCryptPasswordEncoder(); }
  @Bean UserDetailsService uds(UserRepo r) {
    return email -> r.findByEmail(email)
      .map(u -> User.withUsername(u.email).password(u.password).roles(u.role).build())
      .orElseThrow(() -> new UsernameNotFoundException("User not found"));
  }
  @Bean SecurityFilterChain chain(HttpSecurity h) throws Exception {
    h.csrf(c -> c.disable()).cors(Customizer.withDefaults()).httpBasic(Customizer.withDefaults())
     .authorizeHttpRequests(a -> a
       .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
       .requestMatchers("/api/auth/register", "/").permitAll()
       .requestMatchers("/api/users/**").hasRole("ADMIN")
       .requestMatchers(HttpMethod.POST, "/api/categories").hasRole("ADMIN")
       .requestMatchers(HttpMethod.PUT, "/api/categories/**").hasRole("ADMIN")
       .requestMatchers(HttpMethod.DELETE, "/api/categories/**").hasRole("ADMIN")
       .anyRequest().authenticated());
    return h.build();
  }
  @Bean CorsConfigurationSource corsConfigurationSource(@Value("${app.cors.origin}") String origin) {
    CorsConfiguration c = new CorsConfiguration();
    c.setAllowedOrigins(List.of(origin)); c.setAllowedMethods(List.of("*")); c.setAllowedHeaders(List.of("*"));
    UrlBasedCorsConfigurationSource s = new UrlBasedCorsConfigurationSource();
    s.registerCorsConfiguration("/**", c); return s;
  }
}
