package com.mysite.kimdogyeomboard.answer;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.mysite.kimdogyeomboard.DataNotFoundException;
import com.mysite.kimdogyeomboard.member.Member;
import com.mysite.kimdogyeomboard.question.Question;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class AnswerService {

	private final AnswerRepository answerRepository;

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

	public void create(Question question, String content, Member author) {
		Answer answer = new Answer();
		answer.setContent(content);
		answer.setAuthor(author);
		answer.setHidden(false);
		answer.setCreateDate(LocalDateTime.now());
		answer.setQuestion(question);
		this.answerRepository.save(answer);
	}

	public void modify(Answer answer, String content) {
		answer.setContent(content);
		this.answerRepository.save(answer);
	}

	public void delete(Answer answer) {
		this.answerRepository.delete(answer);
	}

	public void hide(Answer answer) {
		answer.setHidden(true);
		this.answerRepository.save(answer);
	}

	public void unhide(Answer answer) {
		answer.setHidden(false);
		this.answerRepository.save(answer);
	}

}
