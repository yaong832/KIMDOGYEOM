package com.mysite.kimdogyeomboard.question;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

public interface QuestionRepository extends JpaRepository<Question, Integer>, JpaSpecificationExecutor<Question> {

	List<Question> findByPinnedTrueOrderByCreateDateDesc();

	@Query("""
			SELECT COUNT(q) FROM Question q
			WHERE NOT EXISTS (
				SELECT 1 FROM Answer a WHERE a.question = q AND a.hidden = false
			)
			""")
	long countPendingQuestions();

	long countByAuthorId(Long authorId);

}
