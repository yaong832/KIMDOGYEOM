package com.mysite.kimdogyeomboard.member;

import java.time.LocalDateTime;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class MemberService implements UserDetailsService {

	private final MemberRepository memberRepository;
	private final PasswordEncoder passwordEncoder;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		Member member = this.memberRepository.findByUsername(username)
				.orElseThrow(() -> new UsernameNotFoundException("회원을 찾을 수 없습니다."));
		return new MemberUserDetails(member);
	}

	public Member join(String username, String password, String nickname) {
		if (this.memberRepository.existsByUsername(username)) {
			throw new IllegalArgumentException("이미 사용 중인 아이디입니다.");
		}
		Member member = new Member();
		member.setUsername(username);
		member.setPassword(this.passwordEncoder.encode(password));
		member.setNickname(nickname);
		member.setRole(MemberRole.USER);
		member.setCreateDate(LocalDateTime.now());
		return this.memberRepository.save(member);
	}

	public Member createAdmin(String username, String password, String nickname) {
		if (this.memberRepository.existsByUsername(username)) {
			return this.memberRepository.findByUsername(username).get();
		}
		Member member = new Member();
		member.setUsername(username);
		member.setPassword(this.passwordEncoder.encode(password));
		member.setNickname(nickname);
		member.setRole(MemberRole.ADMIN);
		member.setCreateDate(LocalDateTime.now());
		return this.memberRepository.save(member);
	}

	public Member createUser(String username, String password, String nickname) {
		if (this.memberRepository.existsByUsername(username)) {
			return this.memberRepository.findByUsername(username).get();
		}
		Member member = new Member();
		member.setUsername(username);
		member.setPassword(this.passwordEncoder.encode(password));
		member.setNickname(nickname);
		member.setRole(MemberRole.USER);
		member.setCreateDate(LocalDateTime.now());
		return this.memberRepository.save(member);
	}

}
