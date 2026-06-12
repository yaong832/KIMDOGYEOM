package com.mysite.kimdogyeomboard.question;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.mysite.kimdogyeomboard.DataNotFoundException;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class QuestionService {

	private final QuestionRepository questionRepository;

	public List<Question> getList() {
		return this.questionRepository.findAllByOrderByCreateDateDesc();
	}

	public Question getQuestion(Integer id) {
		Optional<Question> question = this.questionRepository.findById(id);
		if (question.isPresent()) {
			return question.get();
		}
		throw new DataNotFoundException("question not found");
	}

	public void create(String subject, String content) {
		Question question = new Question();
		question.setSubject(subject);
		question.setContent(content);
		question.setCreateDate(LocalDateTime.now());
		this.questionRepository.save(question);
	}

	public void modify(Question question, String subject, String content) {
		question.setSubject(subject);
		question.setContent(content);
		this.questionRepository.save(question);
	}

	public void delete(Question question) {
		this.questionRepository.delete(question);
	}

}
