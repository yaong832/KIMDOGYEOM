package com.mysite.kimdogyeomboard.question;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.mysite.kimdogyeomboard.answer.Answer;
import com.mysite.kimdogyeomboard.answer.AnswerCommentService;
import com.mysite.kimdogyeomboard.answer.AnswerForm;
import com.mysite.kimdogyeomboard.answer.AnswerLikeService;
import com.mysite.kimdogyeomboard.answer.AnswerService;
import com.mysite.kimdogyeomboard.member.MemberAuthorizationService;
import com.mysite.kimdogyeomboard.member.MemberUserDetails;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RequestMapping("/question")
@RequiredArgsConstructor
@Controller
public class QuestionController {

	private final QuestionService questionService;
	private final AnswerService answerService;
	private final AnswerLikeService answerLikeService;
	private final AnswerCommentService answerCommentService;
	private final QuestionAttachmentService attachmentService;
	private final MemberAuthorizationService authService;

	@GetMapping("/list")
	public String list(@RequestParam(value = "page", defaultValue = "0") int page,
			@RequestParam(value = "kw", required = false) String kw,
			@RequestParam(value = "category", required = false) QuestionCategory category,
			@RequestParam(value = "author", required = false) String author,
			@RequestParam(value = "dateFrom", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFrom,
			@RequestParam(value = "dateTo", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateTo,
			@RequestParam(value = "sort", defaultValue = "LATEST") QuestionSort sort,
			Model model) {
		QuestionSearchCondition condition = new QuestionSearchCondition();
		condition.setKw(kw != null ? kw : "");
		condition.setCategory(category);
		condition.setAuthor(author != null ? author : "");
		condition.setDateFrom(dateFrom);
		condition.setDateTo(dateTo);
		condition.setSort(sort);

		Page<Question> paging = this.questionService.getList(page, condition);
		var notices = page == 0 ? this.questionService.getPinnedNotices() : Collections.<Question>emptyList();
		long nonPinnedTotal = this.questionService.countNonPinned(condition);

		Map<Integer, Long> visibleAnswerCounts = Stream.concat(notices.stream(), paging.getContent().stream())
				.collect(Collectors.toMap(Question::getId, this.answerService::getVisibleCount, (a, b) -> a));

		model.addAttribute("paging", paging);
		model.addAttribute("notices", notices);
		model.addAttribute("nonPinnedTotal", nonPinnedTotal);
		model.addAttribute("questionList", paging.getContent());
		model.addAttribute("visibleAnswerCounts", visibleAnswerCounts);
		model.addAttribute("condition", condition);
		model.addAttribute("categories", QuestionCategory.values());
		model.addAttribute("sorts", QuestionSort.values());
		model.addAttribute("totalQuestions", this.questionService.countAll());
		model.addAttribute("pendingCount", this.questionService.countPending());
		model.addAttribute("totalAnswers", this.answerService.getVisibleAnswerCount());
		return "question_list";
	}

	@GetMapping("/detail/{id}")
	public String detail(Model model, @PathVariable("id") Integer id,
			@AuthenticationPrincipal MemberUserDetails userDetails,
			@ModelAttribute("answerForm") AnswerForm answerForm) {
		Question question = this.questionService.getQuestion(id);
		model.addAttribute("question", question);
		model.addAttribute("visibleAnswerCount", this.answerService.getVisibleCount(question));
		model.addAttribute("attachments", this.attachmentService.getAttachments(id));
		model.addAttribute("likedAnswerIds", question.getAnswerList().stream()
				.filter(a -> this.answerLikeService.isLiked(a,
						userDetails != null ? userDetails.getMember() : null))
				.map(Answer::getId)
				.collect(Collectors.toSet()));
		model.addAttribute("commentsByAnswer", question.getAnswerList().stream()
				.collect(Collectors.toMap(Answer::getId,
						a -> this.answerCommentService.getComments(a.getId()))));
		return "question_detail";
	}

	@GetMapping("/create")
	public String questionCreate(@ModelAttribute("questionForm") QuestionForm questionForm, Model model) {
		if (questionForm.getCategory() == null) {
			questionForm.setCategory(QuestionCategory.OTHER);
		}
		model.addAttribute("categories", QuestionCategory.values());
		return "question_form";
	}

	@PostMapping("/create")
	public String questionCreate(@AuthenticationPrincipal MemberUserDetails userDetails,
			@Valid @ModelAttribute("questionForm") QuestionForm questionForm,
			BindingResult bindingResult,
			@RequestParam(value = "files", required = false) java.util.List<MultipartFile> files,
			Model model) {
		if (bindingResult.hasErrors()) {
			model.addAttribute("categories", QuestionCategory.values());
			return "question_form";
		}
		this.questionService.create(questionForm, userDetails.getMember(), files);
		return "redirect:/question/list";
	}

	@GetMapping("/modify/{id}")
	public String questionModify(@AuthenticationPrincipal MemberUserDetails userDetails,
			Model model, @PathVariable("id") Integer id) {
		Question question = this.questionService.getQuestion(id);
		if (!this.authService.canEditQuestion(question, userDetails)) {
			throw new AccessDeniedException("수정 권한이 없습니다.");
		}
		QuestionForm form = new QuestionForm();
		form.setSubject(question.getSubject());
		form.setContent(question.getContent());
		form.setCategory(question.getCategory() != null ? question.getCategory() : QuestionCategory.OTHER);
		model.addAttribute("questionForm", form);
		model.addAttribute("question", question);
		model.addAttribute("categories", QuestionCategory.values());
		return "question_modify";
	}

	@PostMapping("/modify/{id}")
	public String questionModify(@AuthenticationPrincipal MemberUserDetails userDetails,
			@PathVariable("id") Integer id,
			@Valid @ModelAttribute("questionForm") QuestionForm questionForm,
			BindingResult bindingResult, Model model) {
		Question question = this.questionService.getQuestion(id);
		if (!this.authService.canEditQuestion(question, userDetails)) {
			throw new AccessDeniedException("수정 권한이 없습니다.");
		}
		if (bindingResult.hasErrors()) {
			model.addAttribute("question", question);
			model.addAttribute("categories", QuestionCategory.values());
			return "question_modify";
		}
		this.questionService.modify(question, questionForm);
		return "redirect:/question/detail/" + id;
	}

	@PostMapping("/delete/{id}")
	public String questionDelete(@AuthenticationPrincipal MemberUserDetails userDetails,
			@PathVariable("id") Integer id) {
		Question question = this.questionService.getQuestion(id);
		if (!this.authService.canEditQuestion(question, userDetails)) {
			throw new AccessDeniedException("삭제 권한이 없습니다.");
		}
		this.questionService.delete(question);
		return "redirect:/question/list";
	}

	@PostMapping("/accept/{questionId}/{answerId}")
	public String acceptAnswer(@AuthenticationPrincipal MemberUserDetails userDetails,
			@PathVariable("questionId") Integer questionId, @PathVariable("answerId") Integer answerId) {
		Question question = this.questionService.getQuestion(questionId);
		Answer answer = this.answerService.getAnswer(answerId);
		if (!this.authService.canAcceptAnswer(question, userDetails)) {
			throw new AccessDeniedException("채택 권한이 없습니다.");
		}
		this.questionService.acceptAnswer(question, answer, userDetails.getMember());
		return "redirect:/question/detail/" + questionId;
	}

}
