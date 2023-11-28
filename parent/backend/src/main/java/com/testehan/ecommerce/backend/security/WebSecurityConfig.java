package com.testehan.ecommerce.backend.security;

import com.testehan.ecommerce.backend.user.RoleRepository;
import com.testehan.ecommerce.common.entity.Role;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import java.util.List;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

    private RoleRepository roleRepository;
    public WebSecurityConfig(RoleRepository roleRepository){
        this.roleRepository = roleRepository;
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        AuthenticationManagerBuilder authenticationManagerBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder.userDetailsService(getUserDetailsService());
        authenticationManagerBuilder.authenticationProvider(getAuthenticationProvider());
        AuthenticationManager authenticationManager = authenticationManagerBuilder.build();
// TODO list of roles and patterns from below will be updated in the future on a needed basis
        http.authorizeRequests()
            .requestMatchers("/images/**","/css/**","/webjars/**","/js/**")// to access these patterns a user can be NOT authenticated; ex in login page
                .permitAll()
            .requestMatchers("/users/**","/settings/**").hasAnyAuthority("admin")
            .requestMatchers("/categories/**","/brands/**","/menus/**", "/articles/**").hasAnyAuthority("admin","Editor")
            .anyRequest()
                .authenticated()
            .and()
                .authenticationManager(authenticationManager)
                .formLogin(form -> form
                    .loginPage("/login")
                    .usernameParameter("email")     // because in spring security, the default login input parameter name is "username" and we use email
                    .permitAll())
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .permitAll())
                .rememberMe(rememberMe -> rememberMe.key("123456789"));

        return http.build();
    }

    @Bean
    public UserDetailsService getUserDetailsService(){
        return new ShopUserDetailsService();
    }

    public DaoAuthenticationProvider getAuthenticationProvider(){
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
        daoAuthenticationProvider.setUserDetailsService(getUserDetailsService());
        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder());
        return daoAuthenticationProvider;
    }


    @Bean
    CommandLineRunner setupInitialRoles(){
        return args -> {
            if (roleRepository.findAll().size()==0) {

                var roleAdmin = new Role("admin", "he is the one that can do anything in the app");
                var roleSalesperson = new Role("Salesperson", "manage product price, customers, shipping, orders and sales report");
                var roleEditor = new Role("Editor", "manage categories, brands, products, articles and menus");
                var roleShipper = new Role("Shipper", "view products, view orders and update order status");
                var roleAssistant = new Role("Assistant", "manage questions and reviews");

                roleRepository.saveAll(List.of(roleAdmin, roleSalesperson, roleEditor, roleShipper, roleAssistant));
            }
        };
    }

}
