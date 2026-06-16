package com.mysite.kimdogyeomboard.notification;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.mysite.kimdogyeomboard.member.MemberUserDetails;

import lombok.RequiredArgsConstructor;

@RequestMapping("/notification")
@RequiredArgsConstructor
@Controller
public class NotificationController {

	private final NotificationService notificationService;

	@GetMapping("/list")
	public String list(@AuthenticationPrincipal MemberUserDetails userDetails, Model model) {
		model.addAttribute("notifications",
				this.notificationService.getNotifications(userDetails.getMember()));
		return "notification_list";
	}

	@PostMapping("/read/{id}")
	public String markRead(@AuthenticationPrincipal MemberUserDetails userDetails, @PathVariable("id") Long id) {
		this.notificationService.markAsRead(id, userDetails.getMember());
		return "redirect:/notification/list";
	}

	@PostMapping("/read-all")
	public String markAllRead(@AuthenticationPrincipal MemberUserDetails userDetails) {
		this.notificationService.markAllAsRead(userDetails.getMember());
		return "redirect:/notification/list";
	}

}
