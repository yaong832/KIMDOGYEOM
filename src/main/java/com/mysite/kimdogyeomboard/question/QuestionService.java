package com.mysite.kimdogyeomboard.question;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.mysite.kimdogyeomboard.DataNotFoundException;
import com.mysite.kimdogyeomboard.answer.Answer;
import com.mysite.kimdogyeomboard.member.Member;
import com.mysite.kimdogyeomboard.member.MemberRole;
import com.mysite.kimdogyeomboard.notification.NotificationService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class QuestionService {

	private static final int PAGE_SIZE = 10;

	private final QuestionRepository questionRepository;
	private final QuestionAttachmentService attachmentService;
	private final NotificationService notificationService;

	public Page<Question> getList(int page, QuestionSearchCondition condition) {
		Specification<Question> spec = Specification.allOf(
				QuestionSpecifications.withCondition(condition),
				notPinned());
		Pageable pageable = PageRequest.of(page, PAGE_SIZE, buildSort(condition.getSort()));
		return this.questionRepository.findAll(spec, pageable);
	}

	public List<Question> getPinnedNotices() {
		return this.questionRepository.findByPinnedTrueOrderByCreateDateDesc();
	}

	public long countNonPinned(QuestionSearchCondition condition) {
		return this.questionRepository.count(Specification.allOf(
				QuestionSpecifications.withCondition(condition),
				notPinned()));
	}

	private Specification<Question> notPinned() {
		return (root, query, cb) -> cb.isFalse(root.get("pinned"));
	}

	private Sort buildSort(QuestionSort sort) {
		if (sort == null) {
			sort = QuestionSort.LATEST;
		}
		return switch (sort) {
			case MOST_VIEWS -> Sort.by(Sort.Order.desc("viewCount"), Sort.Order.desc("createDate"));
			case MOST_ANSWERS -> Sort.by(Sort.Order.desc("visibleAnswerCount"), Sort.Order.desc("createDate"));
			default -> Sort.by(Sort.Order.desc("createDate"));
		};
	}

	public long countAll() {
		return this.questionRepository.count();
	}

	public long countPending() {
		return this.questionRepository.countPendingQuestions();
	}

	@Transactional
	public Question getQuestion(Integer id) {
		Optional<Question> question = this.questionRepository.findById(id);
		if (question.isPresent()) {
			Question q = question.get();
			q.setViewCount(q.getViewCount() + 1);
			return q;
		}
		throw new DataNotFoundException("question not found");
	}

	public Page<Question> getMyQuestions(Member member, int page) {
		Specification<Question> spec = (root, query, cb) -> cb.equal(root.get("author"), member);
		Pageable pageable = PageRequest.of(page, PAGE_SIZE, Sort.by(Sort.Direction.DESC, "createDate"));
		return this.questionRepository.findAll(spec, pageable);
	}

	public Page<Question> getMyPendingQuestions(Member member, int page) {
		Specification<Question> spec = (root, query, cb) -> cb.and(
				cb.equal(root.get("author"), member),
				cb.isNull(root.get("acceptedAnswer")));
		Pageable pageable = PageRequest.of(page, PAGE_SIZE, Sort.by(Sort.Direction.DESC, "createDate"));
		return this.questionRepository.findAll(spec, pageable);
	}

	@Transactional
	public void create(QuestionForm form, Member author, List<MultipartFile> files) {
		Question question = new Question();
		question.setSubject(form.getSubject());
		question.setContent(form.getContent());
		question.setCategory(form.getCategory() != null ? form.getCategory() : QuestionCategory.OTHER);
		question.setAuthor(author);
		question.setCreateDate(LocalDateTime.now());
		question.setViewCount(0);
		question.setVisibleAnswerCount(0);
		question.setPinned(false);
		this.questionRepository.save(question);
		try {
			this.attachmentService.saveAttachments(question, files);
		} catch (Exception e) {
			throw new IllegalStateException("첨부파일 저장에 실패했습니다.", e);
		}
	}

	public void modify(Question question, QuestionForm form) {
		question.setSubject(form.getSubject());
		question.setContent(form.getContent());
		if (form.getCategory() != null) {
			question.setCategory(form.getCategory());
		}
		this.questionRepository.save(question);
	}

	public void delete(Question question) {
		this.questionRepository.delete(question);
	}

	@Transactional
	public void acceptAnswer(Question question, Answer answer, Member actor) {
		if (!question.getAuthor().getId().equals(actor.getId()) && actor.getRole() != MemberRole.ADMIN) {
			throw new IllegalStateException("채택 권한이 없습니다.");
		}
		question.setAcceptedAnswer(answer);
		this.questionRepository.save(question);
		if (answer.getAuthor() != null) {
			this.notificationService.notifyMember(answer.getAuthor(),
					"내 답변이 채택되었습니다: " + question.getSubject(),
					"/question/detail/" + question.getId());
		}
	}

	public void togglePin(Question question) {
		question.setPinned(!question.isPinned());
		this.questionRepository.save(question);
	}

	public void refreshVisibleAnswerCount(Question question, long count) {
		question.setVisibleAnswerCount((int) count);
		this.questionRepository.save(question);
	}

}
