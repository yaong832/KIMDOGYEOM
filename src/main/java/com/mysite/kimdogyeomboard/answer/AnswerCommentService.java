package com.mysite.kimdogyeomboard.answer;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.mysite.kimdogyeomboard.DataNotFoundException;
import com.mysite.kimdogyeomboard.member.Member;
import com.mysite.kimdogyeomboard.notification.NotificationService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class AnswerCommentService {

	private final AnswerCommentRepository commentRepository;
	private final NotificationService notificationService;

	public List<AnswerComment> getComments(Integer answerId) {
		return this.commentRepository.findByAnswerIdOrderByCreateDateAsc(answerId);
	}

	public AnswerComment getComment(Long id) {
		return this.commentRepository.findById(id)
				.orElseThrow(() -> new DataNotFoundException("comment not found"));
	}

	public void create(Answer answer, Member author, String content) {
		AnswerComment comment = new AnswerComment();
		comment.setAnswer(answer);
		comment.setAuthor(author);
		comment.setContent(content);
		comment.setCreateDate(LocalDateTime.now());
		this.commentRepository.save(comment);

		if (answer.getAuthor() != null && !answer.getAuthor().getId().equals(author.getId())) {
			this.notificationService.notifyMember(answer.getAuthor(),
					author.getNickname() + "님이 내 답변에 댓글을 남겼습니다.",
					"/question/detail/" + answer.getQuestion().getId());
		}
	}

	public void delete(AnswerComment comment) {
		this.commentRepository.delete(comment);
	}

}
