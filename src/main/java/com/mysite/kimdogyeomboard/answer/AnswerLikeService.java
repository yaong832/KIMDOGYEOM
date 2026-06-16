package com.mysite.kimdogyeomboard.answer;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mysite.kimdogyeomboard.member.Member;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class AnswerLikeService {

	private final AnswerLikeRepository likeRepository;

	@Transactional
	public boolean toggle(Answer answer, Member member) {
		return this.likeRepository.findByMemberAndAnswer(member, answer)
				.map(existing -> {
					this.likeRepository.delete(existing);
					answer.setLikeCount(Math.max(0, answer.getLikeCount() - 1));
					return false;
				})
				.orElseGet(() -> {
					AnswerLike like = new AnswerLike();
					like.setMember(member);
					like.setAnswer(answer);
					like.setCreateDate(LocalDateTime.now());
					this.likeRepository.save(like);
					answer.setLikeCount(answer.getLikeCount() + 1);
					return true;
				});
	}

	public boolean isLiked(Answer answer, Member member) {
		return member != null && this.likeRepository.existsByMemberAndAnswer(member, answer);
	}

}
