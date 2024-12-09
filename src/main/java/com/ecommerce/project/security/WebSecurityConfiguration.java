package com.ecommerce.project.security;

import com.ecommerce.project.model.AppRoles;
import com.ecommerce.project.model.Role;
import com.ecommerce.project.model.User;
import com.ecommerce.project.repositories.RoleRepository;
import com.ecommerce.project.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.ecommerce.project.security.jwt.AuthEntryPointJwt;
import com.ecommerce.project.security.jwt.AuthTokenFilter;
import com.ecommerce.project.security.services.UserDetailsServiceImpl;

import java.util.Set;

@Configuration
@EnableWebSecurity
//@EnableMethodSecurity
public class WebSecurityConfiguration {
    @Autowired
    UserDetailsServiceImpl userDetailsService;


    @Autowired
    private AuthEntryPointJwt unauthorizedHandler;

    // returning new instance
    @Bean
    public AuthTokenFilter authenticationJwtTokenFilter() {
        return new AuthTokenFilter();
    }

    //configures and provides us with DaoAuthenticationProvider which is used to authenticate users
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();

        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());

        return authProvider;
    }


    // responsible for managing the authentication
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }



    // configuring the filter chain
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
                .exceptionHandling(exception -> exception.authenticationEntryPoint(unauthorizedHandler))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth ->     // configuring request based authorization
                        auth.requestMatchers("/api/auth/**").permitAll()
                                .requestMatchers("/h2-console/**").permitAll()
                                //.requestMatchers("/api/admin/**").permitAll()
                                //.requestMatchers("/api/public/**").permitAll()
                                .requestMatchers("/swagger-ui/**").permitAll()
                                .requestMatchers("/api/test/**").permitAll()
                                .requestMatchers("/images/**").permitAll()
                                .anyRequest().authenticated()
                );

        http.authenticationProvider(authenticationProvider());

        http.addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);
        http.headers(headers -> headers.frameOptions(
                HeadersConfigurer.FrameOptionsConfig::sameOrigin
        )) ;
        return http.build();
    }

    // excluding these endpoints entirely at a global level
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web -> web.ignoring().requestMatchers("/v2/api-docs",
                "/configuration/ui",
                "/swagger-resources/**",
                "/configuration/security",
                "/swagger-ui.html",
                "/webjars/**"));
    }

    @Bean
    public CommandLineRunner initData(RoleRepository roleRepository , UserRepository userRepository){
        return args -> {

            // getting the roles , if the role does not exist we are creating one
            Role userRole = roleRepository.findByRoleName(AppRoles.ROLE_USER)
                    .orElseGet( () -> {
                        Role newUserRole = new Role(AppRoles.ROLE_USER);
                        return roleRepository.save(newUserRole);
                    }) ;
            Role sellerRole = roleRepository.findByRoleName(AppRoles.ROLE_SELLER)
                    .orElseGet(() -> {
                        Role newSellerRole = new Role(AppRoles.ROLE_SELLER) ;
                        return roleRepository.save(newSellerRole) ;
                    }) ;
            Role adminRole = roleRepository.findByRoleName(AppRoles.ROLE_ADMIN)
                    .orElseGet(() -> {
                        Role newAdminRole = new Role(AppRoles.ROLE_ADMIN) ;
                        return roleRepository.save(newAdminRole) ;
                    }) ;

            // creating a set of all the roles we have got

            Set<Role> userRoles = Set.of(userRole) ;
            Set<Role> sellerRoles = Set.of(sellerRole) ;
            Set<Role> adminRoles = Set.of(userRole,adminRole,sellerRole) ;

            // creating users if not already present

            if(!userRepository.existsByUserName("user1")){
                User user1 = new User("user1" , "user1090@example.com",passwordEncoder().encode("user123")) ;
                userRepository.save(user1);
            }
            if(!userRepository.existsByUserName("seller1")){
                User seller1 = new User("seller1" , "seller10@example.com",passwordEncoder().encode("seller123")) ;
                userRepository.save(seller1);
            }
            if(!userRepository.existsByUserName("admin")){
                User admin = new User("admin" , "admin121@example.com",passwordEncoder().encode("admin123")) ;
                userRepository.save(admin);
            }

            // update the users with the corresponding roles

            userRepository.findByUserName("user1").ifPresent(
                    user -> {
                        user.setRoles(userRoles);
                        userRepository.save(user);
                    }
            );

            userRepository.findByUserName("seller1").ifPresent(
                    seller -> {
                        seller.setRoles(sellerRoles);
                        userRepository.save(seller);
                    }
            );
            userRepository.findByUserName("admin").ifPresent(
                    admin -> {
                        admin.setRoles(adminRoles);
                        userRepository.save(admin);
                    }
            );
        } ;
    }
}