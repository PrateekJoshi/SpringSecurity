package com.frankmoley.security.app;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.core.authority.mapping.SimpleAuthorityMapper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import com.frankmoley.security.app.auth.LandonUserDetailsService;

/*
 * Refer: https://www.lynda.com/Web-tutorials/Spring-Spring-Security/704153-2.html 
 */

@Configuration			// security config file for our project	
@EnableWebSecurity		//enable security 
@EnableGlobalMethodSecurity(prePostEnabled = true)		//to enable our method level security
public class ApplicationSecurityConfiguration extends WebSecurityConfigurerAdapter{

	@Autowired
	private LandonUserDetailsService userDetailsService;
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.csrf().disable()								//prevent from Cross-site request forgery exploit
		    .authorizeRequests()							//autorize requests ro root(/), index, css and js files 
		    .antMatchers("/","/index","/css/*","/js/*").permitAll()
			.anyRequest().authenticated()					//authenticate any other request (other than mentioned above)
			.and()
			//.httpBasic();									//use http basic authentication
			.formLogin()									//set form based authentication
			.loginPage("/login").permitAll()				//set our custom login page
			.and()
			.logout().invalidateHttpSession(true)			//on logout invalidate session
			.clearAuthentication(true)						//clear authentication
			.logoutRequestMatcher(new AntPathRequestMatcher("/logout"))					//set logout page
			.logoutSuccessUrl("/logout-success").permitAll();							//on successfull logout, load this page
			
			
	}
	
	/*
	 * -> In our methods we have set roles to ROLE_USER and ROLE_ADMIN but in our database we have set roles to
	 * USER and ADMIN, so we need to map those two roles.
	 * -> SimpleAuthorityMapper() fetch the value of role (say USER or ADMIN) from database and append ROLE_ 
	 *    to beginning of it (ie ROLE_USER and ROLE_ADMIN). 
	 * -> To map correctly with database , we have converted fetched value from database to upper case using
	 *    setConvertToUpperCase(true).		   
	 */
	@Bean   
	public GrantedAuthoritiesMapper authoritiesMapper() {
		SimpleAuthorityMapper authorityMapper = new SimpleAuthorityMapper();
		authorityMapper.setConvertToUpperCase(true);
		authorityMapper.setDefaultAuthority("USER");
		return authorityMapper;
	}

	@Bean
	public DaoAuthenticationProvider authenticationProvider() {
		DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
		provider.setUserDetailsService(userDetailsService);
		//provider.setPasswordEncoder(NoOpPasswordEncoder.getInstance());  //NoOpPasswordEncoder -> plaintext password encoder (Not for production )
		provider.setPasswordEncoder(new BCryptPasswordEncoder(10));			//hashed password 10 times
		provider.setAuthoritiesMapper(authoritiesMapper());
		return provider;
	}
	
	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception{
		auth.authenticationProvider(authenticationProvider());
	}
	
	
	/*
	 * In memory-authentication (NOT FOR PRODUCTION USE)
	 * 1. Set default list of users with credentials
	 * 2. Spring security will not prompt a default password after setting in memory authentication
	 */
//	@Bean
//	@Override
//	public UserDetailsService userDetailsServiceBean() throws Exception {
//		List<UserDetails> users = new ArrayList<>();
//		users.add(User.withDefaultPasswordEncoder().username("admin").password("password").roles("USER","ADMIN").build());
//		users.add(User.withDefaultPasswordEncoder().username("user").password("password").roles("USER").build());
//		return new InMemoryUserDetailsManager(users);
//	}


	 
	
}
