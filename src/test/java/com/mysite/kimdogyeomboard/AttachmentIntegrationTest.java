package com.mysite.kimdogyeomboard;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import com.mysite.kimdogyeomboard.member.MemberRepository;
import com.mysite.kimdogyeomboard.member.MemberUserDetails;
import com.mysite.kimdogyeomboard.question.QuestionAttachment;
import com.mysite.kimdogyeomboard.question.QuestionAttachmentRepository;
import com.mysite.kimdogyeomboard.question.QuestionAttachmentService;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
class AttachmentIntegrationTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private MemberRepository memberRepository;

	@Autowired
	private QuestionAttachmentRepository attachmentRepository;

	@Autowired
	private QuestionAttachmentService attachmentService;

	private MemberUserDetails parktech() {
		return new MemberUserDetails(this.memberRepository.findByUsername("parktech").orElseThrow());
	}

	private QuestionAttachment upload(String filename, String contentType, String body) throws Exception {
		MockMultipartFile file = new MockMultipartFile("files", filename, contentType,
				body.getBytes(StandardCharsets.UTF_8));
		mockMvc.perform(multipart("/question/create").file(file)
				.param("category", "ETCH")
				.param("subject", "첨부파일 시험")
				.param("content", "본문")
				.with(user(parktech())).with(csrf()))
				.andExpect(status().is3xxRedirection());
		return this.attachmentRepository.findAll().stream()
				.max(Comparator.comparing(QuestionAttachment::getId))
				.orElseThrow();
	}

	@Test
	@DisplayName("파일 이름의 ../ 는 저장 경로에 들어가지 않는다")
	void traversalName_staysInUploadDir() throws Exception {
		QuestionAttachment saved = upload("../../../evil.html", "text/html", "<script>alert(1)</script>");

		assertThat(saved.getOriginalFilename()).isEqualTo("evil.html");
		assertThat(saved.getStoredFilename()).matches("[0-9a-f-]{36}\\.html");
		Path stored = this.attachmentService.getFilePath(saved);
		assertThat(stored.getParent()).isEqualTo(this.attachmentService.getUploadPath());
		assertThat(Files.exists(stored)).isTrue();
	}

	@Test
	@DisplayName("Windows식 경로(\\)도 이름만 남긴다")
	void windowsPathName_keepsOnlyName() throws Exception {
		QuestionAttachment saved = upload("C:\\Users\\kim\\report.TXT", "text/plain", "log");

		assertThat(saved.getOriginalFilename()).isEqualTo("report.TXT");
		assertThat(saved.getStoredFilename()).matches("[0-9a-f-]{36}\\.txt");
	}

	@Test
	@DisplayName("올린 파일은 /uploads 주소로 바로 열리지 않는다")
	void uploadsUrl_isNotServed() throws Exception {
		QuestionAttachment saved = upload("page.html", "text/html", "<script>alert(1)</script>");

		mockMvc.perform(get("/uploads/" + saved.getStoredFilename()).with(user(parktech())))
				.andExpect(status().isNotFound());
		mockMvc.perform(get("/uploads/" + saved.getStoredFilename()))
				.andExpect(status().is3xxRedirection());
	}

	@Test
	@DisplayName("내려받기는 항상 첨부파일로, 한글 이름은 UTF-8로")
	void download_isAttachmentWithUtf8Name() throws Exception {
		QuestionAttachment saved = upload("점검 결과.txt", "text/plain", "ok");

		mockMvc.perform(get("/attachment/download/" + saved.getId()))
				.andExpect(status().isOk())
				.andExpect(header().string("Content-Type", "application/octet-stream"))
				.andExpect(header().string("Content-Disposition",
						org.hamcrest.Matchers.allOf(
								org.hamcrest.Matchers.startsWith("attachment;"),
								org.hamcrest.Matchers.containsString(
										"filename*=UTF-8''%EC%A0%90%EA%B2%80%20%EA%B2%B0%EA%B3%BC.txt"))));
	}

}
