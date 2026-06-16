package com.mysite.kimdogyeomboard.notification;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mysite.kimdogyeomboard.member.Member;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class NotificationService {

	private final NotificationRepository notificationRepository;

	public void notifyMember(Member member, String message, String link) {
		if (member == null) {
			return;
		}
		Notification notification = new Notification();
		notification.setMember(member);
		notification.setMessage(message);
		notification.setLink(link);
		notification.setReadFlag(false);
		notification.setCreateDate(LocalDateTime.now());
		this.notificationRepository.save(notification);
	}

	public List<Notification> getNotifications(Member member) {
		return this.notificationRepository.findByMemberOrderByCreateDateDesc(member);
	}

	public long countUnread(Member member) {
		return this.notificationRepository.countByMemberAndReadFlagFalse(member);
	}

	@Transactional
	public void markAsRead(Long id, Member member) {
		this.notificationRepository.findById(id).ifPresent(notification -> {
			if (notification.getMember().getId().equals(member.getId())) {
				notification.setReadFlag(true);
			}
		});
	}

	@Transactional
	public void markAllAsRead(Member member) {
		this.notificationRepository.findByMemberOrderByCreateDateDesc(member).forEach(n -> n.setReadFlag(true));
	}

}
