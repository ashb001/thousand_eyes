package challenge;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

	@Autowired
	public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
		  
		auth.inMemoryAuthentication().withUser("batman").password("batman").roles("USER");
		auth.inMemoryAuthentication().withUser("superman").password("superman").roles("USER");
		auth.inMemoryAuthentication().withUser("catwoman").password("catwoman").roles("USER");
		auth.inMemoryAuthentication().withUser("daredevil").password("daredevil").roles("USER");
		auth.inMemoryAuthentication().withUser("alfred").password("alfred").roles("USER");
		auth.inMemoryAuthentication().withUser("dococ").password("dococ").roles("USER");
		auth.inMemoryAuthentication().withUser("zod").password("zod").roles("USER");
		auth.inMemoryAuthentication().withUser("spiderman").password("spiderman").roles("USER");
		auth.inMemoryAuthentication().withUser("ironman").password("ironman").roles("USER");
		auth.inMemoryAuthentication().withUser("profx").password("profx").roles("USER");		  

	}
    
    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http.authorizeRequests()
	        .antMatchers("/h2-console/*").permitAll()
	        .anyRequest().authenticated().and().httpBasic();
        
    	http.csrf().disable();
        http.headers().frameOptions().disable();
    }
}