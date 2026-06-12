package com.mysite.kimdogyeomboard.answer;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AnswerRepository extends JpaRepository<Answer, Integer> {

	long countByHiddenFalse();

	long countByQuestionIdAndHiddenFalse(Integer questionId);

}
