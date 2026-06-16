package com.mysite.kimdogyeomboard.member;

import org.springframework.stereotype.Service;

import com.mysite.kimdogyeomboard.answer.Answer;
import com.mysite.kimdogyeomboard.answer.AnswerComment;
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

	public boolean canAcceptAnswer(Question question, MemberUserDetails userDetails) {
		if (userDetails == null || question.getAuthor() == null) {
			return false;
		}
		if (isAdmin(userDetails)) {
			return true;
		}
		return question.getAuthor().getId().equals(userDetails.getMember().getId());
	}

	public boolean canEditComment(AnswerComment comment, MemberUserDetails userDetails) {
		if (userDetails == null) {
			return false;
		}
		if (isAdmin(userDetails)) {
			return true;
		}
		return comment.getAuthor() != null
				&& comment.getAuthor().getId().equals(userDetails.getMember().getId());
	}

	public boolean isAdmin(Object principal) {
		return isAdmin(toUserDetails(principal));
	}

	public boolean canEditQuestion(Question question, Object principal) {
		return canEditQuestion(question, toUserDetails(principal));
	}

	public boolean canEditAnswer(Answer answer, Object principal) {
		return canEditAnswer(answer, toUserDetails(principal));
	}

	public boolean canAcceptAnswer(Question question, Object principal) {
		return canAcceptAnswer(question, toUserDetails(principal));
	}

	public boolean canEditComment(AnswerComment comment, Object principal) {
		return canEditComment(comment, toUserDetails(principal));
	}

	private MemberUserDetails toUserDetails(Object principal) {
		if (principal instanceof MemberUserDetails details) {
			return details;
		}
		return null;
	}

}
