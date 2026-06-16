package com.mysite.kimdogyeomboard.question;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class QuestionForm {

	@NotBlank(message = "제목을 입력해 주세요.")
	@Size(max = 200, message = "제목은 200자 이하여야 합니다.")
	private String subject;

	@NotBlank(message = "내용을 입력해 주세요.")
	private String content;

	@NotNull(message = "카테고리를 선택해 주세요.")
	private QuestionCategory category;

}
