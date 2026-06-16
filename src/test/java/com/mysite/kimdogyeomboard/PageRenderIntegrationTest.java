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

import com.mysite.kimdogyeomboard.member.MemberUserDetails;
import com.mysite.kimdogyeomboard.member.Member;
import com.mysite.kimdogyeomboard.member.MemberRole;
import com.mysite.kimdogyeomboard.question.QuestionRepository;

@SpringBootTest
@AutoConfigureMockMvc
class PageRenderIntegrationTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private QuestionRepository questionRepository;

	private MemberUserDetails adminUser() {
		Member member = new Member();
		member.setId(1L);
		member.setUsername("kimdogyeom");
		member.setNickname("admin");
		member.setRole(MemberRole.ADMIN);
		return new MemberUserDetails(member);
	}

	private MemberUserDetails normalUser() {
		Member member = new Member();
		member.setId(2L);
		member.setUsername("parktech");
		member.setNickname("user");
		member.setRole(MemberRole.USER);
		return new MemberUserDetails(member);
	}

	@Test
	@DisplayName("비회원 공개 페이지 렌더링")
	void publicPages_anonymous() throws Exception {
		mockMvc.perform(get("/question/list")).andExpect(status().isOk());
		mockMvc.perform(get("/member/login")).andExpect(status().isOk());
		mockMvc.perform(get("/member/signup")).andExpect(status().isOk());

		Integer questionId = this.questionRepository.findAll().stream()
				.findFirst()
				.map(q -> q.getId())
				.orElse(1);
		mockMvc.perform(get("/question/detail/" + questionId)).andExpect(status().isOk());
	}

	@Test
	@DisplayName("비회원 글쓰기는 로그인으로 리다이렉트")
	void createQuestion_anonymous_redirectsToLogin() throws Exception {
		mockMvc.perform(get("/question/create"))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/member/login"));
	}

	@Test
	@DisplayName("회원 전용 페이지 렌더링")
	void memberPages_authenticated() throws Exception {
		mockMvc.perform(get("/mypage").with(user(adminUser()))).andExpect(status().isOk());
		mockMvc.perform(get("/member/profile").with(user(adminUser()))).andExpect(status().isOk());
		mockMvc.perform(get("/member/password").with(user(adminUser()))).andExpect(status().isOk());
		mockMvc.perform(get("/notification/list").with(user(adminUser()))).andExpect(status().isOk());
		mockMvc.perform(get("/question/create").with(user(adminUser()))).andExpect(status().isOk());
	}

	@Test
	@DisplayName("마이페이지 하위 목록 렌더링")
	void mypageSubPages_authenticated() throws Exception {
		mockMvc.perform(get("/mypage/questions").with(user(adminUser()))).andExpect(status().isOk());
		mockMvc.perform(get("/mypage/answers").with(user(adminUser()))).andExpect(status().isOk());
		mockMvc.perform(get("/mypage/pending").with(user(adminUser()))).andExpect(status().isOk());
	}

	@Test
	void adminPages_admin() throws Exception {
		mockMvc.perform(get("/admin/dashboard").with(user(adminUser()))).andExpect(status().isOk());
		mockMvc.perform(get("/admin/reports").with(user(adminUser()))).andExpect(status().isOk());
	}

	@Test
	@DisplayName("일반회원은 관리자 페이지 접근 불가")
	void adminPages_user_forbidden() throws Exception {
		mockMvc.perform(get("/admin/dashboard").with(user(normalUser())))
				.andExpect(status().isForbidden());
	}

	@Test
	@DisplayName("답변 있는 상세 페이지 비회원 렌더링")
	void detailWithAnswers_anonymous() throws Exception {
		Integer questionId = this.questionRepository.findAll().stream()
				.filter(q -> q.getVisibleAnswerCount() > 0)
				.map(q -> q.getId())
				.findFirst()
				.orElseGet(() -> this.questionRepository.findAll().stream()
						.findFirst()
						.map(q -> q.getId())
						.orElse(1));
		mockMvc.perform(get("/question/detail/" + questionId)).andExpect(status().isOk());
	}

	@Test
	@DisplayName("검색 및 페이징")
	void listSearchAndPaging() throws Exception {
		mockMvc.perform(get("/question/list").param("kw", "CVD")).andExpect(status().isOk());
		mockMvc.perform(get("/question/list").param("category", "CVD")).andExpect(status().isOk());
		mockMvc.perform(get("/question/list").param("sort", "MOST_VIEWS")).andExpect(status().isOk());
		mockMvc.perform(get("/question/list").param("page", "0")).andExpect(status().isOk());
	}

	@Test
	@DisplayName("글 등록 후 목록 이동")
	void createQuestion_post() throws Exception {
		mockMvc.perform(post("/question/create")
				.with(user(adminUser()))
				.with(csrf())
				.param("subject", "mockmvc test")
				.param("content", "content")
				.param("category", "OTHER"))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/question/list"));
	}

}
