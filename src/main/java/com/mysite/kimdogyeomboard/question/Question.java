package com.mysite.kimdogyeomboard.question;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.mysite.kimdogyeomboard.answer.Answer;
import com.mysite.kimdogyeomboard.member.Member;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Question {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Column(length = 200)
	private String subject;

	@Column(columnDefinition = "TEXT")
	private String content;

	private LocalDateTime createDate;

	@Column(nullable = false, columnDefinition = "integer default 0")
	private int viewCount;

	@Column(nullable = false, columnDefinition = "integer default 0")
	private int visibleAnswerCount;

	@Column(nullable = false, columnDefinition = "boolean default false")
	private boolean pinned;

	@Enumerated(EnumType.STRING)
	private QuestionCategory category;

	@ManyToOne
	private Member author;

	@ManyToOne
	private Answer acceptedAnswer;

	@OneToMany(mappedBy = "question", cascade = CascadeType.REMOVE)
	@OrderBy("createDate ASC")
	private List<Answer> answerList = new ArrayList<>();

	@OneToMany(mappedBy = "question", cascade = CascadeType.REMOVE)
	private List<QuestionAttachment> attachments = new ArrayList<>();

}
