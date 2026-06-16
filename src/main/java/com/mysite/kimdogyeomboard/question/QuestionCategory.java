package com.mysite.kimdogyeomboard.question;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum QuestionCategory {

	ETCH("식각"),
	CVD("증착"),
	INSPECTION("검사"),
	PM("PM/캘리브레이션"),
	OTHER("기타");

	private final String label;

}
