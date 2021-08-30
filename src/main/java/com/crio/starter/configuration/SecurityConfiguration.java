package com.crio.starter.configuration;

import com.crio.starter.data.*;
import com.crio.starter.repository.*;
import com.crio.starter.service.*;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.context.annotation.*;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.bcrypt.*;
import org.springframework.context.annotation.*;
import java.util.*;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Configuration
@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {
  @Autowired
  private UserRepository userRepository;

  @Autowired
private DataSource dataSource;

@Bean
	@Override
	public AuthenticationManager authenticationManagerBean() throws Exception {
	       return super.authenticationManagerBean();
	}


  @Override
  public void configure(WebSecurity web) throws Exception {
    web.ignoring().antMatchers("/register","/register.html","/favicon.ico","**/InsertData","/styles.css","/index.php");
  }


  @Override
            protected void configure(AuthenticationManagerBuilder builder) throws Exception {
                builder.jdbcAuthentication()
                       .passwordEncoder(new BCryptPasswordEncoder())
                       .dataSource(dataSource);
            }
}
