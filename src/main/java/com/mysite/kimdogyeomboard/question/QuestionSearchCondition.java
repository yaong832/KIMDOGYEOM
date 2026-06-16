package com.mysite.kimdogyeomboard.question;

import java.time.LocalDate;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class QuestionSearchCondition {

	private String kw = "";
	private QuestionCategory category;
	private String author;
	private LocalDate dateFrom;
	private LocalDate dateTo;
	private QuestionSort sort = QuestionSort.LATEST;

}
