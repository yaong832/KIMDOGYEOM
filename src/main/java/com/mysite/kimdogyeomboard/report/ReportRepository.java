package com.mysite.kimdogyeomboard.report;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ReportRepository extends JpaRepository<Report, Long> {

	List<Report> findByStatusOrderByCreateDateDesc(ReportStatus status);

	long countByStatus(ReportStatus status);

}
