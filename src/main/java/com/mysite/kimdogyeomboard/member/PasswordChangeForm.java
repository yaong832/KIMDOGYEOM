package com.mysite.kimdogyeomboard.member;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PasswordChangeForm {

	@NotBlank(message = "현재 비밀번호를 입력해 주세요.")
	private String currentPassword;

	@NotBlank(message = "새 비밀번호를 입력해 주세요.")
	@Size(min = 4, max = 20, message = "비밀번호는 4~20자여야 합니다.")
	private String newPassword;

	@NotBlank(message = "새 비밀번호 확인을 입력해 주세요.")
	private String newPassword2;

	@AssertTrue(message = "새 비밀번호가 일치하지 않습니다.")
	public boolean isNewPasswordMatched() {
		if (newPassword == null || newPassword.isBlank() || newPassword2 == null || newPassword2.isBlank()) {
			return true;
		}
		return newPassword.equals(newPassword2);
	}

}
