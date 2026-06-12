package com.mysite.kimdogyeomboard.config;

import java.time.LocalDateTime;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.mysite.kimdogyeomboard.answer.Answer;
import com.mysite.kimdogyeomboard.answer.AnswerRepository;
import com.mysite.kimdogyeomboard.member.Member;
import com.mysite.kimdogyeomboard.member.MemberService;
import com.mysite.kimdogyeomboard.question.Question;
import com.mysite.kimdogyeomboard.question.QuestionRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

	private final MemberService memberService;
	private final QuestionRepository questionRepository;
	private final AnswerRepository answerRepository;

	@Override
	public void run(String... args) {
		Member admin = this.memberService.createAdmin("kimdogyeom", "1234", "김도겸");
		Member user1 = this.memberService.createUser("parktech", "1234", "박현장");
		Member user2 = this.memberService.createUser("leeequip", "1234", "이설비");

		if (this.questionRepository.count() > 0) {
			return;
		}

		seedSampleData(admin, user1, user2);
	}

	private void seedSampleData(Member admin, Member user1, Member user2) {
		Question q1 = saveQuestion(user1,
				"CVD 챔버 Pressure High 알람 대처 방법",
				"CVD 공정 중 챔버 압력이 설정값보다 높아지며 Pressure High 알람이 발생했습니다.\n"
						+ "로드락 도어 밀림 현상은 없었고, 진공 펌프는 정상 가동 중입니다.\n"
						+ "현장에서 우선 확인해야 할 항목이 무엇인지 알려주세요.",
				LocalDateTime.now().minusDays(3));

		saveAnswer(q1, admin,
				"1) 챔버 게이지 밸브 및 배기 라인 막힘 여부를 먼저 확인하세요.\n"
						+ "2) MFC(질량유량제어기) 설정값과 실제 유량 로그를 비교합니다.\n"
						+ "3) 압력 센서 캘리브레이션 이력을 확인하고, 필요 시 PM 후 재캘리브레이션을 진행하세요.\n"
						+ "4) 동일 레시피 재시도 전 챔버 클린 사이클을 1회 수행하는 것을 권장합니다.",
				LocalDateTime.now().minusDays(2));

		saveQuestion(admin,
				"식각 장비 레시피 파라미터 변경 시 주의사항",
				"신규 웨이퍼 스펙 적용을 위해 식각 레시피의 RF Power와 가스 유량을 변경하려고 합니다.\n"
						+ "기존 양산 레시피와 혼용 관리할 때 주의할 점이 있을까요?",
				LocalDateTime.now().minusDays(2));

		Question q3 = saveQuestion(user2,
				"로드락 펌프 이상 진동 원인 문의",
				"로드락 펌프 가동 시 평소보다 진동이 크고 소음이 발생합니다.\n"
						+ "최근 2주 전 PM 이후 증상이 나타났으며, 진공도는 스펙 내입니다.\n"
						+ "교체가 필요한 부품이 있는지, 점검 순서를 알려주세요.",
				LocalDateTime.now().minusDays(1));

		saveAnswer(q3, user2,
				"저희 라인에서도 비슷한 증상이 있었는데, 펌프 베어링 마모가 원인이었습니다.\n"
						+ "진동 센서 트렌드 로그를 확인해 보시는 것을 추천합니다.",
				LocalDateTime.now().minusHours(20));

		saveAnswer(q3, admin,
				"1) 펌프 베어링·블레이드 상태를 육안 및 진동 측정으로 점검하세요.\n"
						+ "2) PM 시 체결 토크 이력을 확인하고, 베이스 플레이트 수평을 재조정합니다.\n"
						+ "3) 진동값이 스펙 초과 시 펌프 오버홀 또는 교체를 검토하세요.",
				LocalDateTime.now().minusHours(18));

		Question q4 = saveQuestion(user1,
				"PM 후 캘리브레이션 절차 확인",
				"월간 PM을 완료한 후 온도·압력 센서 캘리브레이션을 진행해야 합니다.\n"
						+ "표준 절차서 순서와 H2 DB에 기록해야 할 항목이 궁금합니다.",
				LocalDateTime.now().minusHours(6));

		saveAnswer(q4, admin,
				"PM 후 캘리브레이션 권장 순서:\n"
						+ "1) 장비 워밍업 30분\n"
						+ "2) 제로/스팬 캘리브레이션 수행\n"
						+ "3) 기준값 대비 오차 기록\n"
						+ "4) PASS/FAIL 판정 후 작업자·일시 기록\n"
						+ "H2 Console에서는 QUESTION/ANSWER 외 MEMBER 테이블도 함께 확인할 수 있습니다.",
				LocalDateTime.now().minusHours(4));
	}

	private Question saveQuestion(Member author, String subject, String content, LocalDateTime createDate) {
		Question question = new Question();
		question.setAuthor(author);
		question.setSubject(subject);
		question.setContent(content);
		question.setCreateDate(createDate);
		return this.questionRepository.save(question);
	}

	private void saveAnswer(Question question, Member author, String content, LocalDateTime createDate) {
		Answer answer = new Answer();
		answer.setQuestion(question);
		answer.setAuthor(author);
		answer.setContent(content);
		answer.setHidden(false);
		answer.setCreateDate(createDate);
		this.answerRepository.save(answer);
	}

}
