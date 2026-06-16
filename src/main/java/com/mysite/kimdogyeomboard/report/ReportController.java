package com.mysite.kimdogyeomboard.report;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.mysite.kimdogyeomboard.member.MemberUserDetails;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RequestMapping("/report")
@RequiredArgsConstructor
@Controller
public class ReportController {

	private final ReportService reportService;

	@PostMapping
	public String report(@AuthenticationPrincipal MemberUserDetails userDetails,
			@Valid @ModelAttribute ReportForm reportForm,
			BindingResult bindingResult) {
		if (bindingResult.hasErrors()) {
			return "redirect:/question/list";
		}
		this.reportService.create(userDetails.getMember(), reportForm.getTargetType(),
				reportForm.getTargetId(), reportForm.getReason());
		return "redirect:/question/list?reported";
	}

}
