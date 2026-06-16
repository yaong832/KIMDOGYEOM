package com.mysite.kimdogyeomboard.member;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MemberSignupForm {

	@NotBlank(message = "아이디를 입력해 주세요.")
	@Size(min = 3, max = 20, message = "아이디는 3~20자여야 합니다.")
	private String username;

	@NotBlank(message = "비밀번호를 입력해 주세요.")
	@Size(min = 4, max = 20, message = "비밀번호는 4~20자여야 합니다.")
	private String password;

	@NotBlank(message = "비밀번호 확인을 입력해 주세요.")
	private String password2;

	@NotBlank(message = "닉네임을 입력해 주세요.")
	@Size(min = 2, max = 50, message = "닉네임은 2~50자여야 합니다.")
	private String nickname;

	@AssertTrue(message = "비밀번호가 일치하지 않습니다.")
	public boolean isPasswordMatched() {
		return password != null && password.equals(password2);
	}

}
