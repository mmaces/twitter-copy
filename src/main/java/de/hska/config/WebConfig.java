package de.hska.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import de.hska.core.interceptor.SimpleCookieInterceptor;

@Configuration
@ComponentScan(basePackages = "de.hska.config")
@EnableWebMvc
public class WebConfig extends WebMvcConfigurerAdapter {
	// This variant is needed. Otherwise would be no injection available in the SimpleCookieInterceptor
	@Bean
	public SimpleCookieInterceptor simpleCookieInterceptor() {
		return new SimpleCookieInterceptor();
	}
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(simpleCookieInterceptor()).addPathPatterns("/auth/**");
	}

}
