package com.mysite.kimdogyeomboard.member;

import org.springframework.stereotype.Service;

import com.mysite.kimdogyeomboard.answer.Answer;
import com.mysite.kimdogyeomboard.question.Question;

@Service
public class MemberAuthorizationService {

	public boolean isAdmin(MemberUserDetails userDetails) {
		return userDetails != null && userDetails.getMember().getRole() == MemberRole.ADMIN;
	}

	public boolean canEditQuestion(Question question, MemberUserDetails userDetails) {
		if (userDetails == null) {
			return false;
		}
		if (isAdmin(userDetails)) {
			return true;
		}
		Member member = userDetails.getMember();
		return question.getAuthor() != null && question.getAuthor().getId().equals(member.getId());
	}

	public boolean canEditAnswer(Answer answer, MemberUserDetails userDetails) {
		if (userDetails == null) {
			return false;
		}
		if (isAdmin(userDetails)) {
			return true;
		}
		Member member = userDetails.getMember();
		return answer.getAuthor() != null && answer.getAuthor().getId().equals(member.getId());
	}

}
