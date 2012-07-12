package org.waag.ah.spring.configuration;

import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.waag.ah.spring.RestParametersArgumentResolver;
import org.waag.ah.spring.SPARQLParametersArgumentResolver;

/**
 * API configuration class.
 * 
 * @author Raoul Wissink <raoul@raoul.net>
 * @see http://static.springsource.org/spring/docs/current/spring-framework-reference/html/mvc.html#mvc-config
 */
@EnableWebMvc // Enable mvc:annotation:driven
@Configuration
public class BaseConfig extends WebMvcConfigurerAdapter {
	private static final Charset UTF8 = Charset.forName("UTF-8");
	
	@Override
	public void addArgumentResolvers(
			List<HandlerMethodArgumentResolver> argumentResolvers) {
		argumentResolvers.add(new RestParametersArgumentResolver());
		argumentResolvers.add(new SPARQLParametersArgumentResolver());
	}

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry.addResourceHandler("/vocabulary/**").addResourceLocations("/vocabulary/");
		registry.addResourceHandler("/static/**").addResourceLocations("/static/");
		registry.addResourceHandler("/favicon.ico").addResourceLocations("/static/favicon.ico");
	}

	@Override
	public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
		StringHttpMessageConverter stringConverter = new StringHttpMessageConverter();
		stringConverter.setSupportedMediaTypes(Arrays.asList(
				new MediaType("application", "rdf", UTF8)));
		converters.add(stringConverter);
	}

//	@Override
//	public void addInterceptors(InterceptorRegistry registry) {
//		registry.addInterceptor(new DataUrlInterceptor());
//	}
}
