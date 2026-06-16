package com.mysite.kimdogyeomboard.answer;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mysite.kimdogyeomboard.DataNotFoundException;
import com.mysite.kimdogyeomboard.member.Member;
import com.mysite.kimdogyeomboard.notification.NotificationService;
import com.mysite.kimdogyeomboard.question.Question;
import com.mysite.kimdogyeomboard.question.QuestionService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class AnswerService {

	private static final int PAGE_SIZE = 10;

	private final AnswerRepository answerRepository;
	private final QuestionService questionService;
	private final NotificationService notificationService;

	public Answer getAnswer(Integer id) {
		Optional<Answer> answer = this.answerRepository.findById(id);
		if (answer.isPresent()) {
			return answer.get();
		}
		throw new DataNotFoundException("answer not found");
	}

	public long getVisibleAnswerCount() {
		return this.answerRepository.countByHiddenFalse();
	}

	public long getVisibleCount(Question question) {
		return this.answerRepository.countByQuestionIdAndHiddenFalse(question.getId());
	}

	public Page<Answer> getMyAnswers(Member member, int page) {
		Pageable pageable = PageRequest.of(page, PAGE_SIZE, Sort.by(Sort.Direction.DESC, "createDate"));
		return this.answerRepository.findByAuthorOrderByCreateDateDesc(member, pageable);
	}

	@Transactional
	public void create(Question question, String content, Member author) {
		Answer answer = new Answer();
		answer.setContent(content);
		answer.setAuthor(author);
		answer.setHidden(false);
		answer.setLikeCount(0);
		answer.setCreateDate(LocalDateTime.now());
		answer.setQuestion(question);
		this.answerRepository.save(answer);
		syncVisibleCount(question);

		if (question.getAuthor() != null && !question.getAuthor().getId().equals(author.getId())) {
			this.notificationService.notifyMember(question.getAuthor(),
					author.getNickname() + "님이 질문에 답변을 등록했습니다.",
					"/question/detail/" + question.getId());
		}
	}

	public void modify(Answer answer, String content) {
		answer.setContent(content);
		this.answerRepository.save(answer);
	}

	@Transactional
	public void delete(Answer answer) {
		Question question = answer.getQuestion();
		if (question.getAcceptedAnswer() != null && question.getAcceptedAnswer().getId().equals(answer.getId())) {
			question.setAcceptedAnswer(null);
		}
		this.answerRepository.delete(answer);
		syncVisibleCount(question);
	}

	@Transactional
	public void hide(Answer answer) {
		if (!answer.isHidden()) {
			answer.setHidden(true);
			this.answerRepository.save(answer);
			syncVisibleCount(answer.getQuestion());
		}
	}

	@Transactional
	public void unhide(Answer answer) {
		if (answer.isHidden()) {
			answer.setHidden(false);
			this.answerRepository.save(answer);
			syncVisibleCount(answer.getQuestion());
		}
	}

	private void syncVisibleCount(Question question) {
		this.questionService.refreshVisibleAnswerCount(question, getVisibleCount(question));
	}

}
