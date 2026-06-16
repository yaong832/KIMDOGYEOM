package com.mysite.kimdogyeomboard.answer;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AnswerForm {

	@NotBlank(message = "답변 내용을 입력해 주세요.")
	private String content;

}
