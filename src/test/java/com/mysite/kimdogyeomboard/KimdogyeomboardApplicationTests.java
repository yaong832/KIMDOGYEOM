package com.mysite.kimdogyeomboard;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.mysite.kimdogyeomboard.question.Question;
import com.mysite.kimdogyeomboard.question.QuestionRepository;

@SpringBootTest
class KimdogyeomboardApplicationTests {

	@Autowired
	private QuestionRepository questionRepository;

	@Test
	void testJpa() {
		Question q1 = new Question();
		q1.setSubject("반도체 장비 오류 문의");
		q1.setContent("에칭 장비 가동 중 알람이 발생합니다.");
		q1.setCreateDate(LocalDateTime.now());
		this.questionRepository.save(q1);

		Question q2 = new Question();
		q2.setSubject("Spring Boot 질문");
		q2.setContent("JPA에서 id는 자동 생성되나요?");
		q2.setCreateDate(LocalDateTime.now());
		this.questionRepository.save(q2);
	}

}
