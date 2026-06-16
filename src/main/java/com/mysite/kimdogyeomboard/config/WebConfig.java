package com.mysite.kimdogyeomboard.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.mysite.kimdogyeomboard.question.QuestionAttachmentService;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

	private final QuestionAttachmentService attachmentService;

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		String location = this.attachmentService.getUploadPath().toUri().toString();
		if (!location.endsWith("/")) {
			location += "/";
		}
		registry.addResourceHandler("/uploads/**")
				.addResourceLocations(location);
	}

}
