package com.mysite.kimdogyeomboard.answer;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AnswerCommentRepository extends JpaRepository<AnswerComment, Long> {

	List<AnswerComment> findByAnswerIdOrderByCreateDateAsc(Integer answerId);

}
