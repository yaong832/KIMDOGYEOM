package com.mysite.kimdogyeomboard.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.mysite.kimdogyeomboard.member.MemberService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

	private final MemberService memberService;

	@Override
	public void run(String... args) {
		this.memberService.createAdmin("kimdogyeom", "1234", "김도겸");
	}

}
