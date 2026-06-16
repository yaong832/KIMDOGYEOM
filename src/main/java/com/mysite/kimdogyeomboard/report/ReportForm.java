package com.mysite.kimdogyeomboard.report;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReportForm {

	@NotNull(message = "신고 대상이 없습니다.")
	private ReportTargetType targetType;

	@NotNull(message = "신고 대상이 없습니다.")
	private Integer targetId;

	@NotBlank(message = "신고 사유를 입력해 주세요.")
	private String reason;

}
