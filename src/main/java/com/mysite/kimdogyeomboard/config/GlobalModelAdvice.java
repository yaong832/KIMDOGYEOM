package com.mysite.kimdogyeomboard.config;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.mysite.kimdogyeomboard.member.MemberUserDetails;
import com.mysite.kimdogyeomboard.notification.NotificationService;

import lombok.RequiredArgsConstructor;

@ControllerAdvice
@RequiredArgsConstructor
public class GlobalModelAdvice {

	private final NotificationService notificationService;

	@ModelAttribute("unreadNotificationCount")
	public long unreadNotificationCount(@AuthenticationPrincipal MemberUserDetails userDetails) {
		if (userDetails == null) {
			return 0;
		}
		return this.notificationService.countUnread(userDetails.getMember());
	}

}
