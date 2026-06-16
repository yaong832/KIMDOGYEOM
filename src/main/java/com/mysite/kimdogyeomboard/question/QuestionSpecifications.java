package com.mysite.kimdogyeomboard.question;

import java.time.LocalDateTime;
import java.time.LocalTime;

import org.springframework.data.jpa.domain.Specification;

import com.mysite.kimdogyeomboard.member.Member;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;

public final class QuestionSpecifications {

	private QuestionSpecifications() {
	}

	public static Specification<Question> withCondition(QuestionSearchCondition condition) {
		return Specification.allOf(
				keywordContains(condition.getKw()),
				categoryEquals(condition.getCategory()),
				authorContains(condition.getAuthor()),
				dateFrom(condition.getDateFrom()),
				dateTo(condition.getDateTo()));
	}

	private static Specification<Question> keywordContains(String kw) {
		return (root, query, cb) -> {
			if (kw == null || kw.isBlank()) {
				return cb.conjunction();
			}
			String pattern = "%" + kw.trim().toLowerCase() + "%";
			return cb.or(
					cb.like(cb.lower(root.get("subject")), pattern),
					cb.like(cb.lower(root.get("content")), pattern));
		};
	}

	private static Specification<Question> categoryEquals(QuestionCategory category) {
		return (root, query, cb) -> category == null ? cb.conjunction() : cb.equal(root.get("category"), category);
	}

	private static Specification<Question> authorContains(String author) {
		return (root, query, cb) -> {
			if (author == null || author.isBlank()) {
				return cb.conjunction();
			}
			Join<Question, Member> memberJoin = root.join("author", JoinType.LEFT);
			String pattern = "%" + author.trim().toLowerCase() + "%";
			return cb.or(
					cb.like(cb.lower(memberJoin.get("nickname")), pattern),
					cb.like(cb.lower(memberJoin.get("username")), pattern));
		};
	}

	private static Specification<Question> dateFrom(java.time.LocalDate dateFrom) {
		return (root, query, cb) -> {
			if (dateFrom == null) {
				return cb.conjunction();
			}
			LocalDateTime start = dateFrom.atStartOfDay();
			return cb.greaterThanOrEqualTo(root.get("createDate"), start);
		};
	}

	private static Specification<Question> dateTo(java.time.LocalDate dateTo) {
		return (root, query, cb) -> {
			if (dateTo == null) {
				return cb.conjunction();
			}
			LocalDateTime end = dateTo.atTime(LocalTime.MAX);
			return cb.lessThanOrEqualTo(root.get("createDate"), end);
		};
	}

}
