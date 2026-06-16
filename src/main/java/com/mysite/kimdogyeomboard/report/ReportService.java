package com.mysite.kimdogyeomboard.report;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.mysite.kimdogyeomboard.member.Member;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class ReportService {

	private final ReportRepository reportRepository;

	public void create(Member reporter, ReportTargetType targetType, Integer targetId, String reason) {
		Report report = new Report();
		report.setReporter(reporter);
		report.setTargetType(targetType);
		report.setTargetId(targetId);
		report.setReason(reason);
		report.setStatus(ReportStatus.PENDING);
		report.setCreateDate(LocalDateTime.now());
		this.reportRepository.save(report);
	}

	public List<Report> getPendingReports() {
		return this.reportRepository.findByStatusOrderByCreateDateDesc(ReportStatus.PENDING);
	}

	public long countPending() {
		return this.reportRepository.countByStatus(ReportStatus.PENDING);
	}

	public Report getReport(Long id) {
		return this.reportRepository.findById(id)
				.orElseThrow(() -> new IllegalArgumentException("신고를 찾을 수 없습니다."));
	}

	public void resolve(Long id) {
		Report report = getReport(id);
		report.setStatus(ReportStatus.RESOLVED);
		this.reportRepository.save(report);
	}

}
