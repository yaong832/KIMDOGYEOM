package com.mysite.kimdogyeomboard.question;

import java.util.List;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.mysite.kimdogyeomboard.answer.AnswerService;
import com.mysite.kimdogyeomboard.member.MemberAuthorizationService;
import com.mysite.kimdogyeomboard.member.MemberUserDetails;

import lombok.RequiredArgsConstructor;

@RequestMapping("/question")
@RequiredArgsConstructor
@Controller
public class QuestionController {

	private final QuestionService questionService;
	private final AnswerService answerService;
	private final MemberAuthorizationService authService;

	@GetMapping("/list")
	public String list(Model model) {
		List<Question> questionList = this.questionService.getList();
		long pendingCount = questionList.stream()
				.filter(q -> q.getAnswerList() == null || q.getAnswerList().isEmpty())
				.count();
		model.addAttribute("questionList", questionList);
		model.addAttribute("totalAnswers", this.answerService.getVisibleAnswerCount());
		model.addAttribute("pendingCount", pendingCount);
		return "question_list";
	}

	@GetMapping("/detail/{id}")
	public String detail(Model model, @PathVariable("id") Integer id) {
		Question question = this.questionService.getQuestion(id);
		model.addAttribute("question", question);
		return "question_detail";
	}

	@GetMapping("/create")
	public String questionCreate() {
		return "question_form";
	}

	@PostMapping("/create")
	public String questionCreate(@AuthenticationPrincipal MemberUserDetails userDetails,
			@RequestParam("subject") String subject,
			@RequestParam("content") String content) {
		this.questionService.create(subject, content, userDetails.getMember());
		return "redirect:/question/list";
	}

	@GetMapping("/modify/{id}")
	public String questionModify(@AuthenticationPrincipal MemberUserDetails userDetails,
			Model model, @PathVariable("id") Integer id) {
		Question question = this.questionService.getQuestion(id);
		if (!this.authService.canEditQuestion(question, userDetails)) {
			throw new AccessDeniedException("수정 권한이 없습니다.");
		}
		model.addAttribute("question", question);
		return "question_modify";
	}

	@PostMapping("/modify/{id}")
	public String questionModify(@AuthenticationPrincipal MemberUserDetails userDetails,
			@PathVariable("id") Integer id,
			@RequestParam("subject") String subject,
			@RequestParam("content") String content) {
		Question question = this.questionService.getQuestion(id);
		if (!this.authService.canEditQuestion(question, userDetails)) {
			throw new AccessDeniedException("수정 권한이 없습니다.");
		}
		this.questionService.modify(question, subject, content);
		return "redirect:/question/detail/" + id;
	}

	@GetMapping("/delete/{id}")
	public String questionDelete(@AuthenticationPrincipal MemberUserDetails userDetails,
			@PathVariable("id") Integer id) {
		Question question = this.questionService.getQuestion(id);
		if (!this.authService.canEditQuestion(question, userDetails)) {
			throw new AccessDeniedException("삭제 권한이 없습니다.");
		}
		this.questionService.delete(question);
		return "redirect:/question/list";
	}

}
