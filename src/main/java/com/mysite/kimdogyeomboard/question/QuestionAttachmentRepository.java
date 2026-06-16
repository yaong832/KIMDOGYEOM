package com.mysite.kimdogyeomboard.question;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface QuestionAttachmentRepository extends JpaRepository<QuestionAttachment, Long> {

	List<QuestionAttachment> findByQuestionId(Integer questionId);

}
