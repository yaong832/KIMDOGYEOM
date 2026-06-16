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

	public Member createUser(String username, String password, String nickname) {
		return ensureSampleMember(username, password, nickname, null, null, MemberRole.USER);
	}

	public Member ensureSampleMember(String username, String password, String nickname,
			String email, String bio, MemberRole role) {
		if (this.memberRepository.existsByUsername(username)) {
			Member existing = this.memberRepository.findByUsername(username).get();
			existing.setNickname(nickname);
			if (email != null) {
				existing.setEmail(email);
			}
			if (bio != null) {
				existing.setBio(bio);
			}
			return this.memberRepository.save(existing);
		}
		Member member = new Member();
		member.setUsername(username);
		member.setPassword(this.passwordEncoder.encode(password));
		member.setNickname(nickname);
		member.setEmail(email);
		member.setBio(bio);
		member.setRole(role);
		member.setCreateDate(LocalDateTime.now().minusDays(30));
		return this.memberRepository.save(member);
	}

	public Member createAdmin(String username, String password, String nickname) {
		if (this.memberRepository.existsByUsername(username)) {
			return ensureSampleMember(username, password, nickname,
					"admin@kimdogyeom.local", "반도체 장비 Q&A 운영", MemberRole.ADMIN);
		}
		return ensureSampleMember(username, password, nickname,
				"admin@kimdogyeom.local", "반도체 장비 Q&A 운영", MemberRole.ADMIN);
	}

	public void updateProfile(Member member, String nickname, String email, String bio) {
		member.setNickname(nickname);
		member.setEmail(email != null && !email.isBlank() ? email : null);
		member.setBio(bio);
		this.memberRepository.save(member);
	}

	public void changePassword(Member member, String currentPassword, String newPassword) {
		if (!this.passwordEncoder.matches(currentPassword, member.getPassword())) {
			throw new IllegalArgumentException("현재 비밀번호가 일치하지 않습니다.");
		}
		member.setPassword(this.passwordEncoder.encode(newPassword));
		this.memberRepository.save(member);
	}

	public long countMembers() {
		return this.memberRepository.count();
	}

}
