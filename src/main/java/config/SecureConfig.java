package config;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@EnableWebSecurity
@EnableAutoConfiguration
public class SecureConfig extends WebSecurityConfigurerAdapter {

	public void configure(WebSecurity web) throws Exception {
		web.ignoring()
		.antMatchers(HttpMethod.OPTIONS)
		.antMatchers("/resources/**")
		.antMatchers("/webjars/**")
		.antMatchers("/swagger**")
		.antMatchers("/swagger-resources/**")
		.antMatchers("/v2/api-docs")
		.antMatchers("/api/**")
		.antMatchers("/localize/**");
	}
}
