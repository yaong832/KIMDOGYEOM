package com.mysite.kimdogyeomboard.config;

import java.time.LocalDateTime;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.mysite.kimdogyeomboard.answer.Answer;
import com.mysite.kimdogyeomboard.answer.AnswerComment;
import com.mysite.kimdogyeomboard.answer.AnswerCommentRepository;
import com.mysite.kimdogyeomboard.answer.AnswerLike;
import com.mysite.kimdogyeomboard.answer.AnswerLikeRepository;
import com.mysite.kimdogyeomboard.answer.AnswerRepository;
import com.mysite.kimdogyeomboard.member.Member;
import com.mysite.kimdogyeomboard.member.MemberRole;
import com.mysite.kimdogyeomboard.member.MemberService;
import com.mysite.kimdogyeomboard.question.Question;
import com.mysite.kimdogyeomboard.question.QuestionCategory;
import com.mysite.kimdogyeomboard.question.QuestionRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

	private final MemberService memberService;
	private final QuestionRepository questionRepository;
	private final AnswerRepository answerRepository;
	private final AnswerCommentRepository commentRepository;
	private final AnswerLikeRepository likeRepository;

	@Override
	public void run(String... args) {
		Member admin = this.memberService.createAdmin("kimdogyeom", "1234", "김도겸");
		Member park = this.memberService.ensureSampleMember("parktech", "1234", "박지훈",
				"jihoon.p@example.com", "CVD 공정 3년차", MemberRole.USER);
		Member lee = this.memberService.ensureSampleMember("leeequip", "1234", "이수민",
				"sumin.lee@example.com", "로드락·진공 쪽 자주 봅니다", MemberRole.USER);
		Member han = this.memberService.ensureSampleMember("hanwave", "1234", "한유진",
				"youjin.han@example.com", "웨이퍼 이송 라인", MemberRole.USER);
		Member choi = this.memberService.ensureSampleMember("choisfab", "1234", "최서연",
				"seoyeon.c@example.com", "식각 레시피 정리하는 중", MemberRole.USER);
		Member kang = this.memberService.ensureSampleMember("kangmfc", "1234", "강민재",
				"kangmj@example.com", "MFC 튜닝 질문 환영", MemberRole.USER);
		Member jung = this.memberService.ensureSampleMember("jungetch", "1234", "정다은",
				"daeun.j@example.com", "신입이라 기초 질문 많아요", MemberRole.USER);
		Member yoon = this.memberService.ensureSampleMember("yoonpm", "1234", "윤태호",
				"taeho.y@example.com", "월간 PM 체크리스트 공유합니다", MemberRole.USER);

		if (this.questionRepository.count() > 0) {
			return;
		}

		seedSampleData(admin, park, lee, han, choi, kang, jung, yoon);
	}

	private void seedSampleData(Member admin, Member park, Member lee, Member han,
			Member choi, Member kang, Member jung, Member yoon) {

		saveQuestion(admin, QuestionCategory.OTHER,
				"[공지] 커뮤니티 이용 안내",
				"욕설·광고성 글은 신고해 주세요.\n"
						+ "질문 작성 시 장비명, 알람 코드, 발생 시점을 함께 적어 주시면 답변을 더 빨리 받을 수 있습니다.",
				LocalDateTime.now().minusDays(7), true, 156);

		Question q1 = saveQuestion(park, QuestionCategory.CVD,
				"CVD 챔버 Pressure High 알람 대처 방법",
				"CVD 공정 중 챔버 압력이 설정값보다 높아지며 Pressure High 알람이 발생했습니다.\n"
						+ "로드락 도어 밀림은 없었고, 진공 펌프는 정상 가동 중입니다.\n"
						+ "현장에서 우선 확인할 항목이 궁금합니다.",
				LocalDateTime.now().minusDays(3), false, 42);

		Answer a1 = saveAnswer(q1, admin,
				"1) 챔버 게이지 밸브·배기 라인 막힘 여부를 먼저 확인하세요.\n"
						+ "2) MFC 설정값과 실제 유량 로그를 비교합니다.\n"
						+ "3) 압력 센서 캘리브레이션 이력을 확인하고, 필요 시 PM 후 재캘리브레이션하세요.",
				LocalDateTime.now().minusDays(2));
		saveLike(a1, park);
		saveLike(a1, lee);
		saveLike(a1, kang);
		a1.setLikeCount(3);
		this.answerRepository.save(a1);
		saveComment(a1, kang, "저도 지난달에 MFC 쪽에서 비슷했어요. 로그 먼저 보시는 게 좋습니다.",
				LocalDateTime.now().minusDays(2).plusHours(3));
		finishQuestion(q1, a1, 1);

		saveQuestion(admin, QuestionCategory.ETCH,
				"식각 장비 레시피 파라미터 변경 시 주의사항",
				"신규 웨이퍼 스펙 적용을 위해 RF Power와 가스 유량을 변경하려고 합니다.\n"
						+ "기존 양산 레시피와 혼용 관리할 때 주의할 점이 있을까요?",
				LocalDateTime.now().minusDays(2), false, 88);

		Question q3 = saveQuestion(lee, QuestionCategory.PM,
				"로드락 펌프 이상 진동 원인 문의",
				"로드락 펌프 가동 시 평소보다 진동과 소음이 큽니다.\n"
						+ "2주 전 PM 이후 증상이 나타났고, 진공도는 스펙 안입니다.",
				LocalDateTime.now().minusDays(1), false, 31);

		Answer a3a = saveAnswer(q3, han,
				"저희 라인도 베어링 마모였습니다. 진동 트렌드 로그 한번 확인해 보세요.",
				LocalDateTime.now().minusHours(20));
		Answer a3b = saveAnswer(q3, admin,
				"1) 베어링·블레이드 육안 점검\n2) PM 시 체결 토크 이력 확인\n3) 스펙 초과 시 오버홀 검토",
				LocalDateTime.now().minusHours(18));
		saveLike(a3b, park);
		saveLike(a3b, yoon);
		a3b.setLikeCount(2);
		this.answerRepository.save(a3b);
		finishQuestion(q3, null, 2);

		Question q4 = saveQuestion(park, QuestionCategory.INSPECTION,
				"PM 후 캘리브레이션 절차 확인",
				"월간 PM 후 온도·압력 센서 캘리브레이션 순서와 기록 항목이 궁금합니다.",
				LocalDateTime.now().minusHours(6), false, 15);

		saveAnswer(q4, yoon,
				"워밍업 30분 → 제로/스팬 → 오차 기록 → PASS/FAIL 판정 순서로 합니다.",
				LocalDateTime.now().minusHours(4));
		finishQuestion(q4, null, 1);

		Question q5 = saveQuestion(choi, QuestionCategory.ETCH,
				"RIE 공정에서 CD 편차가 갑자기 커졌어요",
				"동일 레시피인데 최근 2일간 CD 편차가 커졌습니다.\n"
						+ "챔버 클린 주기는 그대로인데, 어떤 항목부터 보면 될까요?",
				LocalDateTime.now().minusHours(10), false, 27);

		saveAnswer(q5, choi,
				"혹시 마지막 PM 때 ESC 청소는 하셨나요? 저는 그때 비슷했습니다.",
				LocalDateTime.now().minusHours(8));
		saveAnswer(q5, admin,
				"RF 매칭 네트워크 로그와 ESC 온도 프로파일을 같이 보시는 걸 추천합니다.",
				LocalDateTime.now().minusHours(7));
		finishQuestion(q5, null, 2);

		saveQuestion(jung, QuestionCategory.OTHER,
				"H2 Console에서 MEMBER 테이블 조회 방법",
				"과제 제출용으로 DB 확인 중인데, H2 Console 접속 후 어떤 SQL을 치면 될까요?",
				LocalDateTime.now().minusHours(3), false, 9);

		Question q7 = saveQuestion(kang, QuestionCategory.CVD,
				"MFC zero calibration 주기 어떻게 잡으시나요",
				"가스 종류별로 캘리브 주기가 다른 것 같은데, 실무에서는 어떤 기준으로 잡으시는지 궁금합니다.",
				LocalDateTime.now().minusHours(14), false, 19);

		saveAnswer(q7, kang,
				"우리는 SiH4 계열은 2주, N2는 4주 정도로 운영 중입니다.",
				LocalDateTime.now().minusHours(12));
		finishQuestion(q7, null, 1);

		Question q8 = saveQuestion(han, QuestionCategory.INSPECTION,
				"EFEM 로봇 암 Z축 떨림 센서 경고",
				"가끔 Z축 떨림 경고가 뜨는데, 실제 이송에는 문제 없어 보입니다. 무시해도 될까요?",
				LocalDateTime.now().minusMinutes(90), false, 6);
		finishQuestion(q8, null, 0);

		Question q9 = saveQuestion(yoon, QuestionCategory.PM,
				"PVD 타겟 수명 관리 어떻게 하시나요",
				"타겟 교체 기준을 사용 시간만 보면 될지, 방전 횟수도 같이 봐야 할지 헷갈립니다.\n"
						+ "실제로 쓰시는 기준이 있으면 공유 부탁드려요.",
				LocalDateTime.now().minusDays(4), false, 53);

		Answer a9 = saveAnswer(q9, park,
				"저희는 kWh 적산값이랑 film thickness drift 같이 봅니다. 둘 중 하나만 보면 놓치는 경우가 있어요.",
				LocalDateTime.now().minusDays(3).plusHours(5));
		saveLike(a9, yoon);
		saveLike(a9, lee);
		a9.setLikeCount(2);
		this.answerRepository.save(a9);
		finishQuestion(q9, a9, 1);

		saveQuestion(park, QuestionCategory.CVD,
				"ALD precursor 라인 퍼지 후에도 압력이 안 떨어져요",
				"퍼지 3회 돌렸는데도 라인 압력이 목표보다 높게 남습니다.\n"
						+ "밸브 시퀀스는 정상으로 보이는데, 어디부터 점검하면 좋을까요?",
				LocalDateTime.now().minusHours(22), false, 11);

		Question q11 = saveQuestion(jung, QuestionCategory.INSPECTION,
				"검사 공정 SEM 이미지 저장 위치 문의",
				"Lot별 SEM 이미지를 서버에 올려두는데, 폴더 구조를 어떻게 나누는 게 관리하기 편할까요?",
				LocalDateTime.now().minusDays(1).minusHours(3), false, 24);

		Answer a11 = saveAnswer(q11, admin,
				"장비명/연월일/Lot ID/Slot 번호 순으로 두면 나중에 찾기 편했습니다.\n"
						+ "파일명에 recipe 버전을 붙이면 비교할 때도 좋아요.",
				LocalDateTime.now().minusDays(1));
		saveComment(a11, jung, "좋은 방법이네요. 바로 적용해 볼게요!", LocalDateTime.now().minusHours(20));
		finishQuestion(q11, a11, 1);

		Question q12 = saveQuestion(han, QuestionCategory.ETCH,
				"챔버 파티클 카운트가 갑자기 올라갔습니다",
				"어제 밤부터 particle count가 평소의 3배 수준입니다.\n"
						+ "웨이퍼 표면에는 눈에 띄는 이상이 없는데, 클린 사이클부터 돌려야 할까요?",
				LocalDateTime.now().minusHours(16), false, 38);

		Answer a12a = saveAnswer(q12, choi,
				"저희는 먼저 liner 교체 이력부터 확인했어요. 그다음 plasma clean 돌렸습니다.",
				LocalDateTime.now().minusHours(14));
		Answer a12b = saveAnswer(q12, admin,
				"gas line moisture 체크와 shower head 상태도 같이 보세요. 로그상 MFC 딜레이가 있으면 힌트가 됩니다.",
				LocalDateTime.now().minusHours(13));
		saveLike(a12b, han);
		saveLike(a12b, choi);
		saveLike(a12b, park);
		a12b.setLikeCount(3);
		this.answerRepository.save(a12b);
		finishQuestion(q12, a12b, 2);

		saveQuestion(lee, QuestionCategory.OTHER,
				"OHT carrier ID 인식 실패가 간헐적으로 발생해요",
				"하루에 2~3번 정도 carrier ID read fail이 뜹니다.\n"
						+ "RF 태그 위치는 문제 없어 보이는데, 통신 로그를 봐야 할까요?",
				LocalDateTime.now().minusHours(8), false, 14);

		Question q14 = saveQuestion(choi, QuestionCategory.ETCH,
				"레시피 버전 이름 규칙 어떻게 정하셨나요",
				"팀마다 recipe 이름이 달라서 혼선이 생깁니다.\n"
						+ "장비명_공정_날짜 이런 식으로 통일하려는데 더 나은 예시가 있을까요?",
				LocalDateTime.now().minusDays(2).minusHours(4), false, 45);

		saveAnswer(q14, kang,
				"TOOL_PROCESS_REV_DATE 형태로 쓰고, major 변경 때만 rev 올립니다.",
				LocalDateTime.now().minusDays(2));
		saveAnswer(q14, yoon,
				"Golden recipe는 _GOLD 접미사 붙여서 구분하고 있어요.",
				LocalDateTime.now().minusDays(1).plusHours(10));
		finishQuestion(q14, null, 2);

		Question q15 = saveQuestion(kang, QuestionCategory.PM,
				"Dry pump 오일 교체 주기 문의",
				"매뉴얼에는 6개월인데, 실제 라인에서는 4개월마다 바꾸는 것 같습니다.\n"
						+ "운전 시간 기준으로 잡는 게 나을까요?",
				LocalDateTime.now().minusDays(3), false, 33);

		Answer a15 = saveAnswer(q15, yoon,
				"운전 시간 + exhaust line 온도 트렌드 같이 보면서 4~5개월 사이에서 조정합니다.",
				LocalDateTime.now().minusDays(2).plusHours(8));
		finishQuestion(q15, a15, 1);

		saveQuestion(park, QuestionCategory.OTHER,
				"Ion implanter beam current drift 관련",
				"아침 점검 때는 정상인데, 오후에 current가 조금씩 떨어집니다.\n"
						+ "source 쪽 문제일 가능성이 클까요?",
				LocalDateTime.now().minusHours(5), false, 7);

		Question q17 = saveQuestion(han, QuestionCategory.PM,
				"FOUP 내부 필터 교체 주기 공유해 주세요",
				"필터 막힘 의심되는데 교체 기록이 없네요. 보통 몇 개월 기준으로 교체하시나요?",
				LocalDateTime.now().minusDays(5), false, 61);

		saveAnswer(q17, lee,
				"라인 환경마다 다른데, 우리는 3개월에 한 번씩 교체합니다.",
				LocalDateTime.now().minusDays(4));
		finishQuestion(q17, null, 1);

		Question q18 = saveQuestion(lee, QuestionCategory.OTHER,
				"야간 근무 때 SCADA 알람이 너무 많이 와요",
				"같은 알람이 반복해서 뜨는데, 우선순위를 어떻게 정리하면 좋을까요?\n"
						+ "필터링 설정 경험이 있으면 알려주세요.",
				LocalDateTime.now().minusHours(12), false, 29);

		saveAnswer(q18, admin,
				"Critical/Warning/Info 3단계로 나누고, 동일 알람은 10분 내 중복 억제 설정을 추천합니다.",
				LocalDateTime.now().minusHours(10));
		saveAnswer(q18, park,
				"야간에는 문자 알림은 critical만 받게 해두었습니다.",
				LocalDateTime.now().minusHours(9));
		finishQuestion(q18, null, 2);

		Question q19 = saveQuestion(kang, QuestionCategory.INSPECTION,
				"진공 게이지 값이 장비마다 조금씩 달라요",
				"같은 기준으로 캘리브했는데도 tool 간 offset이 있습니다.\n"
						+ "reference gauge로 비교 교정하시나요?",
				LocalDateTime.now().minusDays(6), false, 40);

		Answer a19 = saveAnswer(q19, admin,
				"표준 gauge로 cross-check하고, offset은 보정 테이블에 기록해 두었습니다.",
				LocalDateTime.now().minusDays(5));
		finishQuestion(q19, a19, 1);

		saveQuestion(jung, QuestionCategory.OTHER,
				"신입 교육용 체크리스트 공유 부탁드립니다",
				"다음 주부터 라인 투입인데, 첫 주에 확인해야 할 항목을 정리하고 싶습니다.\n"
						+ "실무에서 쓰는 간단한 리스트가 있을까요?",
				LocalDateTime.now().minusMinutes(40), false, 4);

		Question q21 = saveQuestion(choi, QuestionCategory.CVD,
				"He carrier gas 누설 점검 방법",
				"He leak check를 할 때 soap water 말고 더 정확한 방법이 있을까요?\n"
						+ "핀포인트 누설이 의심됩니다.",
				LocalDateTime.now().minusHours(18), false, 22);

		saveAnswer(q21, lee,
				"Helium leak detector 쓰면 작은 누설도 잡히더라고요. connector 체결부부터 보세요.",
				LocalDateTime.now().minusHours(16));
		saveAnswer(q21, kang,
				"연결 후 5분 정도 가압 유지하면서 drift 보는 것도 도움 됩니다.",
				LocalDateTime.now().minusHours(15));
		finishQuestion(q21, null, 2);

		Question q22 = saveQuestion(yoon, QuestionCategory.PM,
				"월간 PM 체크리스트 엑셀 템플릿 있으신 분",
				"팀 내부용으로 PM 항목·담당자·완료 시간을 기록하려고 합니다.\n"
						+ "쓰시는 양식이 있으면 구조만 참고하고 싶습니다.",
				LocalDateTime.now().minusHours(2), false, 12);
		finishQuestion(q22, null, 0);
	}

	private void finishQuestion(Question question, Answer accepted, int visibleCount) {
		question.setAcceptedAnswer(accepted);
		question.setVisibleAnswerCount(visibleCount);
		this.questionRepository.save(question);
	}

	private Question saveQuestion(Member author, QuestionCategory category, String subject, String content,
			LocalDateTime createDate, boolean pinned, int viewCount) {
		Question question = new Question();
		question.setAuthor(author);
		question.setCategory(category);
		question.setSubject(subject);
		question.setContent(content);
		question.setCreateDate(createDate);
		question.setPinned(pinned);
		question.setViewCount(viewCount);
		question.setVisibleAnswerCount(0);
		return this.questionRepository.save(question);
	}

	private Answer saveAnswer(Question question, Member author, String content, LocalDateTime createDate) {
		Answer answer = new Answer();
		answer.setQuestion(question);
		answer.setAuthor(author);
		answer.setContent(content);
		answer.setHidden(false);
		answer.setLikeCount(0);
		answer.setCreateDate(createDate);
		return this.answerRepository.save(answer);
	}

	private void saveLike(Answer answer, Member member) {
		AnswerLike like = new AnswerLike();
		like.setAnswer(answer);
		like.setMember(member);
		like.setCreateDate(LocalDateTime.now());
		this.likeRepository.save(like);
	}

	private void saveComment(Answer answer, Member author, String content, LocalDateTime createDate) {
		AnswerComment comment = new AnswerComment();
		comment.setAnswer(answer);
		comment.setAuthor(author);
		comment.setContent(content);
		comment.setCreateDate(createDate);
		this.commentRepository.save(comment);
	}

}
