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
			String originalFilename = cleanOriginalFilename(file.getOriginalFilename());
			// 저장 이름은 UUID와 확장자만 쓴다. 사용자가 보낸 이름(../ 포함 가능)은
			// 경로에 넣지 않고 DB에만 화면 표시용으로 남긴다.
			String extension = extensionOf(originalFilename);
			String stored = UUID.randomUUID() + (extension.isEmpty() ? "" : "." + extension);
			Path target = resolveInUploadDir(stored);
			file.transferTo(target);

			QuestionAttachment attachment = new QuestionAttachment();
			attachment.setQuestion(question);
			attachment.setOriginalFilename(originalFilename);
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
		return resolveInUploadDir(attachment.getStoredFilename());
	}

	/** 업로드 폴더 안의 경로만 돌려준다. 폴더 밖을 가리키면 예외. */
	private Path resolveInUploadDir(String storedFilename) {
		Path path = this.uploadPath.resolve(storedFilename).normalize();
		if (!path.startsWith(this.uploadPath) || path.equals(this.uploadPath)) {
			throw new IllegalArgumentException("잘못된 첨부파일 경로입니다.");
		}
		return path;
	}

	/** 브라우저가 보낸 파일 이름에서 폴더 부분을 떼고 이름만 남긴다. */
	static String cleanOriginalFilename(String name) {
		if (name == null) {
			return "file";
		}
		String cleaned = name.substring(Math.max(name.lastIndexOf('/'), name.lastIndexOf('\\')) + 1).trim();
		if (cleaned.isEmpty() || cleaned.equals(".") || cleaned.equals("..")) {
			return "file";
		}
		return cleaned;
	}

	/** 확장자(영문 소문자·숫자 1~10자)만 꺼낸다. 없거나 이상하면 빈 문자열. */
	static String extensionOf(String filename) {
		int dot = filename.lastIndexOf('.');
		if (dot < 0 || dot == filename.length() - 1) {
			return "";
		}
		String ext = filename.substring(dot + 1).toLowerCase(java.util.Locale.ROOT);
		return ext.matches("[a-z0-9]{1,10}") ? ext : "";
	}

	public Path getUploadPath() {
		return this.uploadPath;
	}

}
