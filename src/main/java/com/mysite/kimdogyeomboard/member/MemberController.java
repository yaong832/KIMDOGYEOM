package com.mysite.kimdogyeomboard.member;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.validation.Valid;
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
	public String signup(@ModelAttribute("memberSignupForm") MemberSignupForm memberSignupForm) {
		return "signup_form";
	}

	@PostMapping("/signup")
	public String signup(@Valid @ModelAttribute("memberSignupForm") MemberSignupForm memberSignupForm,
			BindingResult bindingResult) {
		if (bindingResult.hasErrors()) {
			return "signup_form";
		}
		try {
			this.memberService.join(memberSignupForm.getUsername(), memberSignupForm.getPassword(),
					memberSignupForm.getNickname());
			return "redirect:/member/login?signup";
		} catch (IllegalArgumentException e) {
			bindingResult.reject("duplicateUsername", e.getMessage());
			return "signup_form";
		}
	}

	@GetMapping("/profile")
	public String profile(@AuthenticationPrincipal MemberUserDetails userDetails, Model model) {
		Member member = userDetails.getMember();
		ProfileForm profileForm = new ProfileForm();
		profileForm.setNickname(member.getNickname());
		profileForm.setEmail(member.getEmail());
		profileForm.setBio(member.getBio());
		model.addAttribute("profileForm", profileForm);
		model.addAttribute("member", member);
		return "profile_form";
	}

	@GetMapping("/password")
	public String passwordForm(@ModelAttribute("passwordChangeForm") PasswordChangeForm passwordChangeForm) {
		return "password_form";
	}

	@PostMapping("/profile")
	public String updateProfile(@AuthenticationPrincipal MemberUserDetails userDetails,
			@Valid @ModelAttribute ProfileForm profileForm,
			BindingResult bindingResult, Model model) {
		if (bindingResult.hasErrors()) {
			model.addAttribute("member", userDetails.getMember());
			return "profile_form";
		}
		this.memberService.updateProfile(userDetails.getMember(), profileForm.getNickname(),
				profileForm.getEmail(), profileForm.getBio());
		return "redirect:/member/profile?updated";
	}

	@PostMapping("/password")
	public String changePassword(@AuthenticationPrincipal MemberUserDetails userDetails,
			@Valid @ModelAttribute PasswordChangeForm passwordChangeForm,
			BindingResult bindingResult) {
		if (bindingResult.hasErrors()) {
			return "password_form";
		}
		Member member = userDetails.getMember();
		try {
			this.memberService.changePassword(member, passwordChangeForm.getCurrentPassword(),
					passwordChangeForm.getNewPassword());
			MemberUserDetails updated = new MemberUserDetails(member);
			var authentication = new UsernamePasswordAuthenticationToken(updated, updated.getPassword(),
					updated.getAuthorities());
			SecurityContextHolder.getContext().setAuthentication(authentication);
			return "redirect:/member/profile?passwordChanged";
		} catch (IllegalArgumentException e) {
			bindingResult.rejectValue("currentPassword", "invalid", e.getMessage());
			return "password_form";
		}
	}

}
