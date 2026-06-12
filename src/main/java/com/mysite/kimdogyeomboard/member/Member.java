package com.mysite.kimdogyeomboard.member;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Member {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(unique = true, length = 50)
	private String username;

	@Column(length = 200)
	private String password;

	@Column(length = 50)
	private String nickname;

	@Enumerated(EnumType.STRING)
	private MemberRole role;

	private LocalDateTime createDate;

}
