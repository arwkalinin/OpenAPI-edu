package com.arwka.openapiedu.config;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;


@Configuration
public class SecurityConfiguration {

  @Value("${admin.login}")
  private String adminLogin;

  @Value("${admin.pass}")
  private String adminPass;

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

    http
        .csrf().disable()
        .authorizeRequests().antMatchers("/orders/*", "/orders/*/*").authenticated()
        .and()
        .httpBasic();

    return http.build();
  }

  @Bean
  public InMemoryUserDetailsManager userDetailsService() {

    UserDetails user = User
        .withUsername(adminLogin)
        .password(passwordEncoder().encode(adminPass))
        .roles("ADMIN")
        .build();

    return new InMemoryUserDetailsManager(user);
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

}
