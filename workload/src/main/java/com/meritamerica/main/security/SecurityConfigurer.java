package com.meritamerica.main.security;

import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@EnableWebSecurity
public class SecurityConfigurer extends WebSecurityConfigurerAdapter {

	@Autowired
	private MyUserDetailsService myUserDetailsService;
	
	@Autowired
	private JwtRequestFilter jwtRequestFilter;
	
	@Autowired
	DataSource dataSource;

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.jdbcAuthentication()
		.dataSource(dataSource)
		.usersByUsernameQuery("select username, password, enabled from users where username = ?")
		.authoritiesByUsernameQuery("select username,authorities from users where username = ?");
	}
	 
	@Override
	protected void configure(HttpSecurity httpSecurity) throws Exception {
	    DaoAuthenticationProvider provider = 
	    	      new DaoAuthenticationProvider();
	    	    provider.setPasswordEncoder(passwordEncoder());
	    	    provider.setUserDetailsService(this.myUserDetailsService);
		httpSecurity.csrf().disable()
				.authorizeRequests().antMatchers("/authenticate/**").permitAll().
//						antMatchers("/authenticate/createUser/**").hasAnyAuthority("ADMIN_PRIVILEGE").
//						antMatchers("/CDOfferings").hasAnyAuthority("ADMIN_PRIVILEGE").
//						antMatchers("/AccountHolders/**").hasAnyAuthority("ADMIN_PRIVILEGE").
						antMatchers("/Me/**").hasAnyAuthority("USER_PRIVILEGE").
						anyRequest().authenticated().and().
						exceptionHandling()
						.and().cors()
						.and().sessionManagement()
				.sessionCreationPolicy(SessionCreationPolicy.STATELESS);

		httpSecurity.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);
	}
	
	@Override
	public void configure(WebSecurity web) throws Exception {
	    web.ignoring().antMatchers("/h2-console/**");
	}
	
	// for newer spring security, we need this to make it backward compatible.
	@Override
	@Bean
	public AuthenticationManager authenticationManagerBean() throws Exception {
		return super.authenticationManagerBean();
	}
	
	
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
}
