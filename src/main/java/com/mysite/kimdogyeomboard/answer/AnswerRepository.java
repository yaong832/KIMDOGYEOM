package com.mysite.kimdogyeomboard.answer;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.mysite.kimdogyeomboard.member.Member;

public interface AnswerRepository extends JpaRepository<Answer, Integer> {

	long countByHiddenFalse();

	long countByQuestionIdAndHiddenFalse(Integer questionId);

	Page<Answer> findByAuthorOrderByCreateDateDesc(Member author, Pageable pageable);

}
