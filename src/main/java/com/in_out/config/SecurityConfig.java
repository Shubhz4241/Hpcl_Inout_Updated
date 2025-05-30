package com.in_out.config;


import com.in_out.service.UserDetailsServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer;
import org.springframework.security.config.annotation.web.configurers.FormLoginConfigurer;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.session.ConcurrentSessionControlAuthenticationStrategy;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;
import org.springframework.security.web.session.HttpSessionEventPublisher;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
  @Bean
  public PasswordEncoder passwordEncoder() {
    return (PasswordEncoder)new BCryptPasswordEncoder();
  }
  
  @Bean
  public UserDetailsService userDetailsService() {
    return (UserDetailsService)new UserDetailsServiceImpl();
  }
  
  @Bean
  public DaoAuthenticationProvider daoAuthenticationProvider() {
    DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
    authenticationProvider.setUserDetailsService(userDetailsService());
    authenticationProvider.setPasswordEncoder(passwordEncoder());
    return authenticationProvider;
  }
  
  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http.csrf(AbstractHttpConfigurer::disable)
      .authorizeRequests(authorize -> ((ExpressionUrlAuthorizationConfigurer.AuthorizedUrl)((ExpressionUrlAuthorizationConfigurer.AuthorizedUrl)((ExpressionUrlAuthorizationConfigurer.AuthorizedUrl)authorize.requestMatchers(new String[] { "/License", "/js/**", "/css/**", "/img/**" })).permitAll().requestMatchers(new String[] { "/", "/register", "/Allusers", "/Officers", "/dashbord", "/Security", "/employee", "/submitOperators" })).authenticated().requestMatchers(new String[] { "/base", "/officer", "/setting", "/operatorDetails", "/licenseGate", "/mainGate/**" })).hasAnyAuthority(new String[] { "admin", "officer", "security" })).formLogin(form -> ((FormLoginConfigurer)((FormLoginConfigurer)form.loginPage("/login").permitAll()).defaultSuccessUrl("/")).permitAll());
    return (SecurityFilterChain)http.build();
  }
  
  @Bean
  public SessionRegistry sessionRegistry() {
    return (SessionRegistry)new SessionRegistryImpl();
  }
  
  @Bean
  public SessionAuthenticationStrategy sessionAuthenticationStrategy() {
    return (SessionAuthenticationStrategy)new ConcurrentSessionControlAuthenticationStrategy(sessionRegistry());
  }
  
  @Bean
  public HttpSessionEventPublisher httpSessionEventPublisher() {
    return new HttpSessionEventPublisher();
  }
}