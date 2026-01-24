package bd.edu.seu.pulse.config;

import bd.edu.seu.pulse.service.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration

public class SecurityConfig {

        @Autowired
        private CustomUserDetailsService userDetailsService;

        @Autowired
        private RoleBasedAuthenticationSuccessHandler authenticationSuccessHandler;

        @Bean
        public BCryptPasswordEncoder passwordEncoder() {
                return new BCryptPasswordEncoder();
        }

        @Bean
        public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
                http
                                .userDetailsService(userDetailsService)
                                .authorizeHttpRequests(authz -> authz
                                                .requestMatchers("/css/**", "/images/**", "/uploads/**").permitAll()
                                                .requestMatchers("/landing", "/login/reader", "/login/writer",
                                                                "/login/admin",
                                                                "/register", "/", "/login/process")
                                                .permitAll()
                                                .requestMatchers("/reader/**").hasRole("READER")
                                                .requestMatchers("/writer/**").hasRole("WRITER")
                                                .requestMatchers("/admin/**").hasRole("ADMIN")
                                                .anyRequest().authenticated())
                                .formLogin(form -> form
                                                .loginPage("/landing")
                                                .loginProcessingUrl("/login/process")
                                                .usernameParameter("username")
                                                .passwordParameter("password")
                                                .successHandler(authenticationSuccessHandler)
                                                .failureHandler((request, response, exception) -> {
                                                        String expectedRole = request.getParameter("expectedRole");
                                                        String targetUrl = "/landing?error=true";
                                                        if ("READER".equalsIgnoreCase(expectedRole)) {
                                                                targetUrl = "/login/reader?error=true";
                                                        } else if ("WRITER".equalsIgnoreCase(expectedRole)) {
                                                                targetUrl = "/login/writer?error=true";
                                                        }
                                                        response.sendRedirect(targetUrl);
                                                })
                                                .permitAll())
                                .logout(logout -> logout
                                                .logoutUrl("/logout")
                                                .logoutSuccessUrl("/landing?logout=true")
                                                .invalidateHttpSession(true)
                                                .deleteCookies("JSESSIONID")
                                                .permitAll());

                return http.build();
        }

        @Bean
        public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration)
                        throws Exception {
                return authenticationConfiguration.getAuthenticationManager();
        }
}