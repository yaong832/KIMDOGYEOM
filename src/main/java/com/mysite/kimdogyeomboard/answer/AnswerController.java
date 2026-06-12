package com.mysite.kimdogyeomboard.answer;

import org.springframework.stereotype.Controller;
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

}
