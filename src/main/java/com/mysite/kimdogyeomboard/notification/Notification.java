package com.mysite.kimdogyeomboard.notification;

import java.time.LocalDateTime;

import com.mysite.kimdogyeomboard.member.Member;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Notification {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(length = 200)
	private String message;

	@Column(length = 300)
	private String link;

	private boolean readFlag;

	private LocalDateTime createDate;

	@ManyToOne
	private Member member;

}
