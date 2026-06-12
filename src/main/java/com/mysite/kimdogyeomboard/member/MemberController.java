package com.mysite.kimdogyeomboard.member;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import lombok.RequiredArgsConstructor;

@RequestMapping("/member")
@RequiredArgsConstructor
@Controller
public class MemberController {

	private final MemberService memberService;

	@GetMapping("/login")
	public String login() {
		return "login_form";
	}

	@GetMapping("/signup")
	public String signup() {
		return "signup_form";
	}

	@PostMapping("/signup")
	public String signup(@RequestParam("username") String username,
			@RequestParam("password") String password,
			@RequestParam("nickname") String nickname,
			Model model) {
		try {
			this.memberService.join(username, password, nickname);
			return "redirect:/member/login?signup";
		} catch (IllegalArgumentException e) {
			model.addAttribute("error", e.getMessage());
			return "signup_form";
		}
	}

}
