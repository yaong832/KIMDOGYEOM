package com.mysite.kimdogyeomboard.question;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class QuestionAttachmentService {

	private final QuestionAttachmentRepository attachmentRepository;

	@Value("${app.upload.dir:uploads}")
	private String uploadDir;

	private Path uploadPath;

	@PostConstruct
	void initUploadPath() throws IOException {
		this.uploadPath = Paths.get(this.uploadDir).toAbsolutePath().normalize();
		Files.createDirectories(this.uploadPath);
	}

	public void saveAttachments(Question question, List<org.springframework.web.multipart.MultipartFile> files)
			throws IOException {
		if (files == null) {
			return;
		}
		for (org.springframework.web.multipart.MultipartFile file : files) {
			if (file == null || file.isEmpty()) {
				continue;
			}
			String stored = UUID.randomUUID() + "_" + file.getOriginalFilename();
			Path target = this.uploadPath.resolve(stored);
			file.transferTo(target);

			QuestionAttachment attachment = new QuestionAttachment();
			attachment.setQuestion(question);
			attachment.setOriginalFilename(file.getOriginalFilename());
			attachment.setStoredFilename(stored);
			attachment.setFileSize(file.getSize());
			attachment.setCreateDate(LocalDateTime.now());
			this.attachmentRepository.save(attachment);
		}
	}

	public List<QuestionAttachment> getAttachments(Integer questionId) {
		return this.attachmentRepository.findByQuestionId(questionId);
	}

	public QuestionAttachment getAttachment(Long id) {
		return this.attachmentRepository.findById(id)
				.orElseThrow(() -> new IllegalArgumentException("첨부파일을 찾을 수 없습니다."));
	}

	public Path getFilePath(QuestionAttachment attachment) {
		return this.uploadPath.resolve(attachment.getStoredFilename());
	}

	public Path getUploadPath() {
		return this.uploadPath;
	}

}
