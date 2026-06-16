package com.mysite.kimdogyeomboard.answer;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.mysite.kimdogyeomboard.member.Member;
import com.mysite.kimdogyeomboard.question.Question;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
public class Answer {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Column(columnDefinition = "TEXT")
	private String content;

	private boolean hidden;

	@Column(nullable = false, columnDefinition = "integer default 0")
	private int likeCount;

	private LocalDateTime createDate;

	@ManyToOne
	private Member author;

	@ManyToOne
	private Question question;

	@OneToMany(mappedBy = "answer", cascade = CascadeType.REMOVE)
	@OrderBy("createDate ASC")
	private List<AnswerComment> commentList = new ArrayList<>();

}
