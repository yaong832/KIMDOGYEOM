package com.mysite.kimdogyeomboard.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

	@Bean
	SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http.authorizeHttpRequests(auth -> auth
				.requestMatchers("/", "/question/list", "/question/detail/**").permitAll()
				.requestMatchers("/member/login", "/member/signup", "/access-denied").permitAll()
				.requestMatchers("/h2-console/**").permitAll()
				.requestMatchers("/bootstrap.min.css", "/style.css").permitAll()
				.requestMatchers("/answer/hide/**", "/answer/unhide/**").hasRole("ADMIN")
				.anyRequest().authenticated())
			.formLogin(form -> form
				.loginPage("/member/login")
				.loginProcessingUrl("/member/login")
				.defaultSuccessUrl("/question/list")
				.failureUrl("/member/login?error")
				.permitAll())
			.logout(logout -> logout
				.logoutUrl("/member/logout")
				.logoutSuccessUrl("/question/list?logout")
				.permitAll())
			.exceptionHandling(ex -> ex.accessDeniedPage("/access-denied"))
			.csrf(csrf -> csrf.ignoringRequestMatchers("/h2-console/**"));

		http.headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()));
		return http.build();
	}

	@Bean
	PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

}
