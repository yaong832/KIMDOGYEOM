package com.mysite.kimdogyeomboard.member;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProfileForm {

	@NotBlank(message = "닉네임을 입력해 주세요.")
	@Size(min = 2, max = 50, message = "닉네임은 2~50자여야 합니다.")
	private String nickname;

	@Email(message = "올바른 이메일 형식이 아닙니다.")
	private String email;

	@Size(max = 500, message = "소개는 500자 이하여야 합니다.")
	private String bio;

}
