package com.mysite.kimdogyeomboard.member;

import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.mysite.kimdogyeomboard.answer.Answer;
import com.mysite.kimdogyeomboard.answer.AnswerService;
import com.mysite.kimdogyeomboard.question.Question;
import com.mysite.kimdogyeomboard.question.QuestionService;

import lombok.RequiredArgsConstructor;

@RequestMapping("/mypage")
@RequiredArgsConstructor
@Controller
public class MypageController {

	private final QuestionService questionService;
	private final AnswerService answerService;

	@GetMapping
	public String index(@AuthenticationPrincipal MemberUserDetails userDetails, Model model) {
		model.addAttribute("member", userDetails.getMember());
		model.addAttribute("myQuestionCount",
				this.questionService.getMyQuestions(userDetails.getMember(), 0).getTotalElements());
		model.addAttribute("myAnswerCount",
				this.answerService.getMyAnswers(userDetails.getMember(), 0).getTotalElements());
		model.addAttribute("myPendingCount",
				this.questionService.getMyPendingQuestions(userDetails.getMember(), 0).getTotalElements());
		return "mypage_index";
	}

	@GetMapping("/questions")
	public String myQuestions(@AuthenticationPrincipal MemberUserDetails userDetails,
			@RequestParam(value = "page", defaultValue = "0") int page, Model model) {
		Page<Question> paging = this.questionService.getMyQuestions(userDetails.getMember(), page);
		Map<Integer, Long> visibleAnswerCounts = paging.getContent().stream()
				.collect(Collectors.toMap(Question::getId, this.answerService::getVisibleCount));
		model.addAttribute("paging", paging);
		model.addAttribute("questionList", paging.getContent());
		model.addAttribute("visibleAnswerCounts", visibleAnswerCounts);
		return "mypage_questions";
	}

	@GetMapping("/answers")
	public String myAnswers(@AuthenticationPrincipal MemberUserDetails userDetails,
			@RequestParam(value = "page", defaultValue = "0") int page, Model model) {
		Page<Answer> paging = this.answerService.getMyAnswers(userDetails.getMember(), page);
		model.addAttribute("paging", paging);
		model.addAttribute("answerList", paging.getContent());
		return "mypage_answers";
	}

	@GetMapping("/pending")
	public String myPending(@AuthenticationPrincipal MemberUserDetails userDetails,
			@RequestParam(value = "page", defaultValue = "0") int page, Model model) {
		Page<Question> paging = this.questionService.getMyPendingQuestions(userDetails.getMember(), page);
		model.addAttribute("paging", paging);
		model.addAttribute("questionList", paging.getContent());
		return "mypage_pending";
	}

}
