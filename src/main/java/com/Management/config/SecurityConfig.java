// package com.Management.config;

// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.Configuration;
// import org.springframework.security.authentication.AuthenticationProvider;
// import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
// import org.springframework.security.config.annotation.web.builders.HttpSecurity;
// import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
// import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
// import org.springframework.security.crypto.password.PasswordEncoder;
// import org.springframework.security.web.SecurityFilterChain;

// import com.Management.service.MyUserDetailsService;

// @Configuration
// @EnableWebSecurity
// public class SecurityConfig {

//     @Autowired
//     private MyUserDetailsService userDetailsService;

//     @Bean
//     public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//         http
//                 // Disable CSRF for simplicity (evaluate security implications carefully)
//                 .csrf(csrf -> csrf.disable())

//                 // Configure authorization rules
//                 .authorizeHttpRequests(auth -> auth
//                         .requestMatchers("/login").permitAll() // Allow unrestricted access to login page
//                         .requestMatchers("/admin/**").hasRole("ADMIN") // Restrict admin paths to ADMIN role
//                         .anyRequest().authenticated() // All other requests require authentication
//                 )

//                 // Configure form-based login
//                 .formLogin(form -> form
//                         .loginPage("/login") // Custom login page
//                         .usernameParameter("email") // Use email as username
//                         .passwordParameter("password") // Password field
//                         .loginProcessingUrl("/login") // Form submission endpoint
//                         .defaultSuccessUrl("/dashboard") // Redirect after login success
//                         .permitAll() // Allow public access to login endpoints
//                 )

//                 // Configure logout
//                 .logout(logout -> logout
//                         .logoutUrl("/logout") // Logout endpoint
//                         .logoutSuccessUrl("/login?logout") // Redirect after logout
//                         .permitAll() // Allow public access to logout
//                 );

//         // Configure OAuth2 login (if applicable)
//         // .oauth2Login(Customizer.withDefaults())

//         // Configure OAuth2 client support (if applicable)
//         // .oauth2Client(Customizer.withDefaults());

//         return http.build();
//     }

//     @Bean
//     public PasswordEncoder passwordEncoder() {
//         return new BCryptPasswordEncoder();
//     }

//     @Bean
//     AuthenticationProvider authenticationProvider() {
//         DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
//         provider.setUserDetailsService(userDetailsService);
//         provider.setPasswordEncoder(passwordEncoder());
//         return provider;
//     }

// }
