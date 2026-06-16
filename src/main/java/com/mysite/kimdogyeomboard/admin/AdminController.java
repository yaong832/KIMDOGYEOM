package com.mysite.kimdogyeomboard.admin;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.mysite.kimdogyeomboard.answer.AnswerService;
import com.mysite.kimdogyeomboard.member.MemberService;
import com.mysite.kimdogyeomboard.member.MemberUserDetails;
import com.mysite.kimdogyeomboard.question.Question;
import com.mysite.kimdogyeomboard.question.QuestionService;
import com.mysite.kimdogyeomboard.report.ReportService;

import lombok.RequiredArgsConstructor;

@RequestMapping("/admin")
@RequiredArgsConstructor
@Controller
public class AdminController {

	private final QuestionService questionService;
	private final AnswerService answerService;
	private final MemberService memberService;
	private final ReportService reportService;

	@GetMapping("/dashboard")
	public String dashboard(Model model) {
		model.addAttribute("totalQuestions", this.questionService.countAll());
		model.addAttribute("totalAnswers", this.answerService.getVisibleAnswerCount());
		model.addAttribute("totalMembers", this.memberService.countMembers());
		model.addAttribute("pendingReports", this.reportService.countPending());
		model.addAttribute("pendingQuestions", this.questionService.countPending());
		return "admin/dashboard";
	}

	@GetMapping("/reports")
	public String reports(Model model) {
		model.addAttribute("reports", this.reportService.getPendingReports());
		return "admin/reports";
	}

	@PostMapping("/reports/{id}/resolve")
	public String resolveReport(@PathVariable("id") Long id) {
		this.reportService.resolve(id);
		return "redirect:/admin/reports";
	}

	@PostMapping("/question/pin/{id}")
	public String togglePin(@PathVariable("id") Integer id) {
		Question question = this.questionService.getQuestion(id);
		this.questionService.togglePin(question);
		return "redirect:/question/detail/" + id;
	}

}
