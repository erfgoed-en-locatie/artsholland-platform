package org.waag.ah.spring.configuration;

import java.util.List;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.waag.ah.spring.RestParametersArgumentResolver;
import org.waag.ah.spring.interceptor.MetricsInterceptor;

/**
 * API configuration class.
 * 
 * @author Raoul Wissink <raoul@raoul.net>
 * @see http://static.springsource.org/spring/docs/current/spring-framework-reference/html/mvc.html#mvc-config
 */
@EnableWebMvc // Enable mvc:annotation:driven
@Configuration
public class BaseConfig extends WebMvcConfigurerAdapter {

	@Override
	public void addArgumentResolvers(
			List<HandlerMethodArgumentResolver> argumentResolvers) {
		argumentResolvers.add(new RestParametersArgumentResolver());
	}

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry.addResourceHandler("/vocabulary/**").addResourceLocations("/vocabulary/");
		registry.addResourceHandler("/static/**").addResourceLocations("/static/");
		registry.addResourceHandler("/favicon.ico").addResourceLocations("/static/favicon.ico");
	}

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
//		registry.addInterceptor(new DataUrlInterceptor());
		registry.addInterceptor(new MetricsInterceptor());
	}
}
