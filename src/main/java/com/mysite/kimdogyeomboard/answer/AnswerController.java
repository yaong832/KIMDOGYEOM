package com.mysite.kimdogyeomboard.answer;

import java.util.stream.Collectors;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.mysite.kimdogyeomboard.member.MemberAuthorizationService;
import com.mysite.kimdogyeomboard.member.MemberUserDetails;
import com.mysite.kimdogyeomboard.question.Question;
import com.mysite.kimdogyeomboard.question.QuestionAttachmentService;
import com.mysite.kimdogyeomboard.question.QuestionService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RequestMapping("/answer")
@RequiredArgsConstructor
@Controller
public class AnswerController {

	private final AnswerService answerService;
	private final QuestionService questionService;
	private final AnswerLikeService answerLikeService;
	private final AnswerCommentService answerCommentService;
	private final MemberAuthorizationService authService;
	private final QuestionAttachmentService attachmentService;

	@PostMapping("/create/{id}")
	public String createAnswer(@AuthenticationPrincipal MemberUserDetails userDetails,
			@PathVariable("id") Integer id,
			@Valid @ModelAttribute("answerForm") AnswerForm answerForm,
			BindingResult bindingResult, Model model) {
		Question question = this.questionService.getQuestion(id);
		if (bindingResult.hasErrors()) {
			populateDetailModel(model, question, id, userDetails);
			return "question_detail";
		}
		this.answerService.create(question, answerForm.getContent(), userDetails.getMember());
		return "redirect:/question/detail/" + id;
	}

	@GetMapping("/modify/{id}")
	public String answerModify(@AuthenticationPrincipal MemberUserDetails userDetails,
			Model model, @PathVariable("id") Integer id,
			@ModelAttribute("answerForm") AnswerForm answerForm) {
		Answer answer = this.answerService.getAnswer(id);
		if (!this.authService.canEditAnswer(answer, userDetails)) {
			throw new AccessDeniedException("수정 권한이 없습니다.");
		}
		answerForm.setContent(answer.getContent());
		model.addAttribute("answer", answer);
		return "answer_modify";
	}

	@PostMapping("/modify/{id}")
	public String answerModify(@AuthenticationPrincipal MemberUserDetails userDetails,
			@PathVariable("id") Integer id,
			@Valid @ModelAttribute("answerForm") AnswerForm answerForm,
			BindingResult bindingResult, Model model) {
		Answer answer = this.answerService.getAnswer(id);
		if (!this.authService.canEditAnswer(answer, userDetails)) {
			throw new AccessDeniedException("수정 권한이 없습니다.");
		}
		if (bindingResult.hasErrors()) {
			model.addAttribute("answer", answer);
			return "answer_modify";
		}
		Integer questionId = answer.getQuestion().getId();
		this.answerService.modify(answer, answerForm.getContent());
		return "redirect:/question/detail/" + questionId;
	}

	@PostMapping("/delete/{id}")
	public String answerDelete(@AuthenticationPrincipal MemberUserDetails userDetails,
			@PathVariable("id") Integer id) {
		Answer answer = this.answerService.getAnswer(id);
		if (!this.authService.canEditAnswer(answer, userDetails)) {
			throw new AccessDeniedException("삭제 권한이 없습니다.");
		}
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

	@PostMapping("/like/{id}")
	public String toggleLike(@AuthenticationPrincipal MemberUserDetails userDetails,
			@PathVariable("id") Integer id) {
		Answer answer = this.answerService.getAnswer(id);
		this.answerLikeService.toggle(answer, userDetails.getMember());
		return "redirect:/question/detail/" + answer.getQuestion().getId();
	}

	@PostMapping("/comment/{id}")
	public String createComment(@AuthenticationPrincipal MemberUserDetails userDetails,
			@PathVariable("id") Integer id,
			@Valid @ModelAttribute AnswerCommentForm form,
			BindingResult bindingResult) {
		Answer answer = this.answerService.getAnswer(id);
		if (!bindingResult.hasErrors()) {
			this.answerCommentService.create(answer, userDetails.getMember(), form.getContent());
		}
		return "redirect:/question/detail/" + answer.getQuestion().getId();
	}

	@PostMapping("/comment/delete/{id}")
	public String deleteComment(@AuthenticationPrincipal MemberUserDetails userDetails,
			@PathVariable("id") Long id) {
		AnswerComment comment = this.answerCommentService.getComment(id);
		if (!this.authService.canEditComment(comment, userDetails)) {
			throw new AccessDeniedException("댓글 삭제 권한이 없습니다.");
		}
		Integer questionId = comment.getAnswer().getQuestion().getId();
		this.answerCommentService.delete(comment);
		return "redirect:/question/detail/" + questionId;
	}

	private void populateDetailModel(Model model, Question question, Integer questionId,
			MemberUserDetails userDetails) {
		model.addAttribute("question", question);
		model.addAttribute("visibleAnswerCount", this.answerService.getVisibleCount(question));
		model.addAttribute("attachments", this.attachmentService.getAttachments(questionId));
		model.addAttribute("likedAnswerIds", question.getAnswerList().stream()
				.filter(a -> this.answerLikeService.isLiked(a,
						userDetails != null ? userDetails.getMember() : null))
				.map(Answer::getId)
				.collect(Collectors.toSet()));
		model.addAttribute("commentsByAnswer", question.getAnswerList().stream()
				.collect(Collectors.toMap(Answer::getId,
						a -> this.answerCommentService.getComments(a.getId()))));
	}

}
