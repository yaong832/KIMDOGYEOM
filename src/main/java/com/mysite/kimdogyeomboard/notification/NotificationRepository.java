package com.mysite.kimdogyeomboard.notification;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mysite.kimdogyeomboard.member.Member;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

	List<Notification> findByMemberOrderByCreateDateDesc(Member member);

	long countByMemberAndReadFlagFalse(Member member);

}
