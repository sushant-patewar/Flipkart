package com.shoppingcart.config;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.BeanIds;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import com.google.common.collect.Lists;
import com.shoppingcart.security.CustomUserDetailsService;
import com.shoppingcart.security.JwtAuthenticationEntryPoint;
import com.shoppingcart.security.JwtAuthenticationFilter;

import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiKey;
import springfox.documentation.service.AuthorizationScope;
import springfox.documentation.service.SecurityReference;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;



@Configuration
@EnableSwagger2
@EnableWebSecurity
@EnableGlobalMethodSecurity(
        securedEnabled = true,
        jsr250Enabled = true,
        prePostEnabled = true
)
public class SecurityConfig extends WebSecurityConfigurerAdapter{
	@Autowired
    CustomUserDetailsService customUserDetailsService;

    @Autowired
    private JwtAuthenticationEntryPoint unauthorizedHandler;
    
    @Autowired
    private JwtAuthenticationFilter jwtFilter;
    
    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String DEFAULT_INCLUDE_PATTERN = "/.*";
    
//    @Bean
//    public Docket api() { 
//        return new Docket(DocumentationType.SWAGGER_2)  
//          .select()                                  
//          .apis(RequestHandlerSelectors.any())              
//          .paths(PathSelectors.any())                          
//          .build()
//          .securitySchemes(Lists.newArrayList(apiKey()));                                          
//    }
//
//   	private ApiKey apiKey() {
//   		return new ApiKey("JWT", AUTHORIZATION_HEADER, "header");
//   	}
    
	@Bean
	public Docket api() {
		return new Docket(DocumentationType.SWAGGER_2).select().apis(RequestHandlerSelectors.any())
				.paths(PathSelectors.any()).build().securityContexts(Lists.newArrayList(securityContext()))
				.securitySchemes(Lists.newArrayList(apiKey()));
	}

	private ApiKey apiKey() {
		return new ApiKey("JWT", AUTHORIZATION_HEADER, "header");
	}
	
	private SecurityContext securityContext() {
		return SecurityContext.builder().securityReferences(defaultAuth())
				.forPaths(PathSelectors.regex(DEFAULT_INCLUDE_PATTERN)).build();
	}

	List<SecurityReference> defaultAuth() {
		AuthorizationScope authorizationScope = new AuthorizationScope("global", "accessEverything");
		AuthorizationScope[] authorizationScopes = new AuthorizationScope[1];
		authorizationScopes[0] = authorizationScope;
		return Lists.newArrayList(new SecurityReference("JWT", authorizationScopes));
	}
    
//    @Bean
//    public JwtAuthenticationFilter jwtAuthenticationFilter() {
//        return new JwtAuthenticationFilter();
//    }

    @Override
    public void configure(AuthenticationManagerBuilder authenticationManagerBuilder) throws Exception {
        authenticationManagerBuilder
                .userDetailsService(customUserDetailsService)
                .passwordEncoder(passwordEncoder());
    }// 1. Spring Security.

    
    // It should be default bean but it's bug so we are adding it.
    @Bean(BeanIds.AUTHENTICATION_MANAGER)
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }// 2.
    
    @Override
	public void configure(WebSecurity web) throws Exception {
		web.ignoring().antMatchers("/v2/api-docs", "/configuration/ui", "/swagger-resources/**",
				"/configuration/security", "/swagger-ui.html", "/webjars/**");
	}

    @Override
    protected void configure(HttpSecurity http) throws Exception {
//        http
//                .cors()
//                    .and()
//                .csrf()
//                    .disable()
//                .exceptionHandling()
//                    .authenticationEntryPoint(unauthorizedHandler)
//                    .and()
//                .sessionManagement()
//                    .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
//                    .and()
//                .authorizeRequests()
//                    .antMatchers("/",
//                        "/favicon.ico",
//                        "/**/*.png",
//                        "/**/*.gif",
//                        "/**/*.svg",
//                        "/**/*.jpg",
//                        "/**/*.html",
//                        "/**/*.css",
//                        "/**/*.js")
//                        .permitAll()
//                        .antMatchers("/api/auth/signin", "/api/auth/signup").permitAll()
//                    .antMatchers("/api/user/checkUsernameAvailability", "/api/user/checkEmailAvailability")
//                        .permitAll()
//                    .antMatchers(HttpMethod.GET, "/api/polls/**", "/api/users/**")
//                        .permitAll()
//                    .anyRequest()
//                        .authenticated();
//
//        // Add our custom JWT security filter
//        http.addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
    	
//    	http.cors().and().csrf().disable().authorizeRequests().antMatchers("/api/auth/signin", "/api/auth/signup").permitAll()
//		.antMatchers("/login").permitAll()
//		.antMatchers("/*.js").permitAll()
//		.antMatchers("/*.html").permitAll()
//		.antMatchers("/*.css").permitAll()
//		.antMatchers("/*.ico").permitAll()
////		.antMatchers("/wec").permitAll()
//		.antMatchers(HttpMethod.OPTIONS, "/**").permitAll().anyRequest()
//		.authenticated().and().exceptionHandling().and().sessionManagement()
//		.sessionCreationPolicy(SessionCreationPolicy.STATELESS);
//http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
    	
    	http.cors().and().csrf().disable().authorizeRequests()
    								.antMatchers("/api/auth/signin", "/api/auth/signup").permitAll()
    								.antMatchers("/api/auth/forgot-password", "api/auth/confirm-reset", "api/auth/reset-password").permitAll()
    								.anyRequest().authenticated()
    								.and().sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
    	
    	http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

    }


   
    
}
