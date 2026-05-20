package auth2.auth2;

import auth2.auth2.Jwt.JwtFilter;
import auth2.auth2.users.UserDetailsServices;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.client.RestClient;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors. UrlBasedCorsConfigurationSource;

import java.util.List;

@EnableWebSecurity
@Configuration
public class Configurations {

    @Autowired
    UserDetailsService userDetailsService;
    @Autowired
    JwtFilter jwtFilter;
    @Bean
 public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception{
        return httpSecurity.
                csrf(AbstractHttpConfigurer::disable)
                .  cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .authorizeHttpRequests
                        (request->
                        request.requestMatchers(
                                "/User/login",
                                        "/User/register",
                                        "/ping",
                                        "/oauth2/callback/linkedin",
                                        "/oauth2/callback/**",
                                       "/Post/create",
                                "/Post/getAll",
                                "/Post/Publish",
                                "/oauth/meta/login",
                                "/oauth/meta/callback",
                                "/oauth/mastodon/login",
                                "/oauth/mastodon/callback"
                                )
                                .permitAll()
                                .anyRequest()
                                .authenticated()
               )

                .exceptionHandling(exception ->  // Add this for proper error handling
                        exception.authenticationEntryPoint((request, response, authException) -> {
                            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);  // 401 instead of 403
                            response.getWriter().write("Unauthorized: " + authException.getMessage());
                        })
                ).sessionManagement(session->
                        session.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration config = new CorsConfiguration();

        config.setAllowedOrigins(List.of(
                "http://127.0.0.1:5500",
                "http://localhost:5500"
        ));

        config.setAllowedMethods(List.of(
                "GET", "POST", "PUT", "DELETE", "OPTIONS"
        ));

        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration("/**", config);
        return (CorsConfigurationSource) source;
    }


    @Bean
  public AuthenticationManager authenticationManager(AuthenticationConfiguration config){
        return config.getAuthenticationManager();
  }


 @Bean
 public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
 }

 @Bean
 public AuthenticationProvider authenticationProvider(){
     DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService);
     provider.setPasswordEncoder(passwordEncoder());
     return provider;
 }


 @Bean
    public RestClient restClient(){
        return RestClient.builder().build();
 }


}
