package com.mysite.kimdogyeomboard.question;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.RequiredArgsConstructor;

@RequestMapping("/attachment")
@RequiredArgsConstructor
@Controller
public class QuestionAttachmentController {

	private final QuestionAttachmentService attachmentService;

	@GetMapping("/download/{id}")
	public ResponseEntity<Resource> download(@PathVariable("id") Long id) throws Exception {
		QuestionAttachment attachment = this.attachmentService.getAttachment(id);
		Path path = this.attachmentService.getFilePath(attachment);
		if (!Files.exists(path)) {
			return ResponseEntity.notFound().build();
		}
		Resource resource = new FileSystemResource(path);
		return ResponseEntity.ok()
				.header(HttpHeaders.CONTENT_DISPOSITION,
						ContentDisposition.attachment()
								.filename(attachment.getOriginalFilename(), StandardCharsets.UTF_8)
								.build()
								.toString())
				.contentType(MediaType.APPLICATION_OCTET_STREAM)
				.body(resource);
	}

}
