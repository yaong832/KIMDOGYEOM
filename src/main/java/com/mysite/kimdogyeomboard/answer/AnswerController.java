package com.mysite.kimdogyeomboard.answer;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.mysite.kimdogyeomboard.question.Question;
import com.mysite.kimdogyeomboard.question.QuestionService;

import lombok.RequiredArgsConstructor;

@RequestMapping("/answer")
@RequiredArgsConstructor
@Controller
public class AnswerController {

	private final AnswerService answerService;
	private final QuestionService questionService;

	@PostMapping("/create/{id}")
	public String createAnswer(@PathVariable("id") Integer id,
			@RequestParam("content") String content) {
		Question question = this.questionService.getQuestion(id);
		this.answerService.create(question, content);
		return "redirect:/question/detail/" + id;
	}

	@GetMapping("/modify/{id}")
	public String answerModify(Model model, @PathVariable("id") Integer id) {
		Answer answer = this.answerService.getAnswer(id);
		model.addAttribute("answer", answer);
		return "answer_modify";
	}

	@PostMapping("/modify/{id}")
	public String answerModify(@PathVariable("id") Integer id,
			@RequestParam("content") String content) {
		Answer answer = this.answerService.getAnswer(id);
		Integer questionId = answer.getQuestion().getId();
		this.answerService.modify(answer, content);
		return "redirect:/question/detail/" + questionId;
	}

	@GetMapping("/delete/{id}")
	public String answerDelete(@PathVariable("id") Integer id) {
		Answer answer = this.answerService.getAnswer(id);
		Integer questionId = answer.getQuestion().getId();
		this.answerService.delete(answer);
		return "redirect:/question/detail/" + questionId;
	}

	@GetMapping("/hide/{id}")
	public String answerHide(@PathVariable("id") Integer id) {
		Answer answer = this.answerService.getAnswer(id);
		Integer questionId = answer.getQuestion().getId();
		this.answerService.hide(answer);
		return "redirect:/question/detail/" + questionId;
	}

	@GetMapping("/unhide/{id}")
	public String answerUnhide(@PathVariable("id") Integer id) {
		Answer answer = this.answerService.getAnswer(id);
		Integer questionId = answer.getQuestion().getId();
		this.answerService.unhide(answer);
		return "redirect:/question/detail/" + questionId;
	}

}
