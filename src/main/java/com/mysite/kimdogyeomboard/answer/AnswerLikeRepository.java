package com.mysite.kimdogyeomboard.answer;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mysite.kimdogyeomboard.member.Member;

public interface AnswerLikeRepository extends JpaRepository<AnswerLike, Long> {

	Optional<AnswerLike> findByMemberAndAnswer(Member member, Answer answer);

	boolean existsByMemberAndAnswer(Member member, Answer answer);

}
