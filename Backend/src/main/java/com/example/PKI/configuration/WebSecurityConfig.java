package com.example.PKI.configuration;

import com.example.PKI.security.RestAuthenticationEntryPoint;
import com.example.PKI.security.TokenAuthenticationFilter;
import com.example.PKI.service.AppUserService;
import com.example.PKI.util.TokenUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
   @Autowired
   private TokenUtils tokenUtils;
  
   public PasswordEncoder passwordEncoder() {
       return new BCryptPasswordEncoder();
   }

   @Autowired
   private AppUserService customUserDetailsService;

   @Autowired
   private RestAuthenticationEntryPoint restAuthenticationEntryPoint;

   @Bean
   @Override
   public AuthenticationManager authenticationManagerBean() throws Exception {
       return super.authenticationManagerBean();
   }

   @Autowired
   public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
       auth
               .userDetailsService(customUserDetailsService)
               .passwordEncoder(passwordEncoder());
   }

   @Override
   protected void configure(HttpSecurity http) throws Exception {
       http

               .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()

               .exceptionHandling().authenticationEntryPoint(restAuthenticationEntryPoint).and()

               .authorizeRequests()
               .antMatchers("/users").permitAll()
               

               .anyRequest().authenticated().and()

               .cors().and()

   .addFilterBefore(new TokenAuthenticationFilter(tokenUtils, customUserDetailsService), BasicAuthenticationFilter.class);
       http.csrf().disable();
   }

   @Override
   public void configure(WebSecurity web) throws Exception {

       web.ignoring().antMatchers(HttpMethod.POST, "/users/login");

       web.ignoring().antMatchers(HttpMethod.GET, "/", "/webjars/**", "/*.html", "favicon.ico", "/**/*.html",
               "/**/*.css", "/**/*.js");
   }
}
