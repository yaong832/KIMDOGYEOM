package com.mysite.kimdogyeomboard.report;

import java.time.LocalDateTime;

import com.mysite.kimdogyeomboard.member.Member;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Report {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Enumerated(EnumType.STRING)
	private ReportTargetType targetType;

	private Integer targetId;

	@Column(columnDefinition = "TEXT")
	private String reason;

	@Enumerated(EnumType.STRING)
	private ReportStatus status = ReportStatus.PENDING;

	private LocalDateTime createDate;

	@ManyToOne
	private Member reporter;

}
