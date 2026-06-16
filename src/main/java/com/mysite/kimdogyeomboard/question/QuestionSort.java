package com.mysite.kimdogyeomboard.question;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum QuestionSort {

	LATEST("최신순"),
	MOST_VIEWS("조회순"),
	MOST_ANSWERS("답변많은순");

	private final String label;

}
