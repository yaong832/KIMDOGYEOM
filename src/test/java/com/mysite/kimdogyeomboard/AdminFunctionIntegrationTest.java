package com.mysite.kimdogyeomboard;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import com.mysite.kimdogyeomboard.answer.AnswerRepository;
import com.mysite.kimdogyeomboard.member.Member;
import com.mysite.kimdogyeomboard.member.MemberRepository;
import com.mysite.kimdogyeomboard.member.MemberRole;
import com.mysite.kimdogyeomboard.member.MemberUserDetails;
import com.mysite.kimdogyeomboard.question.QuestionRepository;
import com.mysite.kimdogyeomboard.report.ReportRepository;
import com.mysite.kimdogyeomboard.report.ReportStatus;

@SpringBootTest
@AutoConfigureMockMvc
class AdminFunctionIntegrationTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private QuestionRepository questionRepository;

	@Autowired
	private AnswerRepository answerRepository;

	@Autowired
	private ReportRepository reportRepository;

	@Autowired
	private MemberRepository memberRepository;

	private MemberUserDetails adminUser() {
		Member member = this.memberRepository.findByUsername("kimdogyeom").orElseGet(() -> {
			Member m = new Member();
			m.setUsername("kimdogyeom");
			m.setRole(MemberRole.ADMIN);
			return m;
		});
		return new MemberUserDetails(member);
	}

	private MemberUserDetails normalUser() {
		Member member = this.memberRepository.findByUsername("parktech").orElseGet(() -> {
			Member m = new Member();
			m.setUsername("parktech");
			m.setRole(MemberRole.USER);
			return m;
		});
		return new MemberUserDetails(member);
	}

	@Test
	@DisplayName("관리자 대시보드·신고 목록 렌더링")
	void adminPages_render() throws Exception {
		mockMvc.perform(get("/admin/dashboard").with(user(adminUser()))).andExpect(status().isOk());
		mockMvc.perform(get("/admin/reports").with(user(adminUser()))).andExpect(status().isOk());
	}

	@Test
	@DisplayName("일반회원 관리자 기능 차단")
	void adminActions_user_forbidden() throws Exception {
		Integer questionId = this.questionRepository.findAll().stream().findFirst().map(q -> q.getId()).orElse(1);
		mockMvc.perform(get("/admin/dashboard").with(user(normalUser()))).andExpect(status().isForbidden());
		mockMvc.perform(get("/admin/reports").with(user(normalUser()))).andExpect(status().isForbidden());
		mockMvc.perform(post("/admin/question/pin/" + questionId).with(user(normalUser())).with(csrf()))
				.andExpect(status().isForbidden());
	}

	@Test
	@DisplayName("관리자 공지 고정/해제")
	void admin_pinQuestion() throws Exception {
		Integer questionId = this.questionRepository.findAll().stream().findFirst().map(q -> q.getId()).orElse(1);
		mockMvc.perform(post("/admin/question/pin/" + questionId).with(user(adminUser())).with(csrf()))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/question/detail/" + questionId));
	}

	@Test
	@DisplayName("관리자 신고 처리")
	void admin_resolveReport() throws Exception {
		Integer questionId = this.questionRepository.findAll().stream().findFirst().map(q -> q.getId()).orElse(1);
		mockMvc.perform(post("/report").with(user(adminUser())).with(csrf())
				.param("targetType", "QUESTION")
				.param("targetId", questionId.toString())
				.param("reason", "admin test report"))
				.andExpect(status().is3xxRedirection());

		Long reportId = this.reportRepository.findAll().stream()
				.filter(r -> r.getStatus() == ReportStatus.PENDING)
				.map(r -> r.getId())
				.reduce((first, second) -> second)
				.orElseThrow();

		mockMvc.perform(post("/admin/reports/" + reportId + "/resolve").with(user(adminUser())).with(csrf()))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/admin/reports"));
	}

	@Test
	@DisplayName("관리자 답변 숨김/해제")
	void admin_hideAndUnhideAnswer() throws Exception {
		Integer answerId = this.answerRepository.findAll().stream().findFirst().map(a -> a.getId()).orElseThrow();
		Integer questionId = this.answerRepository.findById(answerId).orElseThrow().getQuestion().getId();

		mockMvc.perform(get("/answer/hide/" + answerId).with(user(adminUser())))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/question/detail/" + questionId));

		mockMvc.perform(get("/answer/unhide/" + answerId).with(user(adminUser())))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/question/detail/" + questionId));

		mockMvc.perform(get("/answer/hide/" + answerId).with(user(normalUser())))
				.andExpect(status().isForbidden());
	}

}
