# KIMDOGYEOM Board

**Kimdogyeom Board** — Spring Boot 기반 반도체 장비 Q&A 커뮤니티 (기말 대체 과제)

## 프로젝트 정보

| 항목 | 내용 |
|------|------|
| 프로젝트명 | KIMDOGYEOM |
| 패키지 | `com.mysite.kimdogyeomboard` |
| 개발자 | KIM DO GYEOM |
| 주제 | 반도체 장비 Q&A 커뮤니티 |

## 기술 스택

- Java 17, Spring Boot 4.0.3, Gradle
- Spring Security, Thymeleaf, Spring Data JPA, H2
- Bootstrap 5, Lombok, DevTools

## 구현 기능

### 필수 (지시서)
- 게시글 목록 / 상세 / 등록 / 수정 / 삭제
- 답변 등록 (Question 1:N Answer, cascade 삭제)
- Bootstrap UI, layout.html, H2 Console

### 추가 (커뮤니티)
- 회원가입 / 로그인 / 로그아웃
- 등급: 일반회원(USER), 관리자(ADMIN)
- 작성자 권한 기반 수정·삭제
- 관리자: 답변 숨김(블라인드) / 숨김 해제
- 답변 수정·삭제, 상태 뱃지, 통계

## 실행 방법

```bash
.\gradlew.bat bootRun
```

| 화면 | URL |
|------|-----|
| 메인 | http://localhost:8080/ |
| 로그인 | http://localhost:8080/member/login |
| 회원가입 | http://localhost:8080/member/signup |
| H2 Console | http://localhost:8080/h2-console |

### H2 접속
- JDBC URL: `jdbc:h2:~/kimdogyeom;AUTO_SERVER=TRUE`
- Username: `sa` / Password: (비움)

### 테스트 계정
| 아이디 | 비밀번호 | 등급 |
|--------|----------|------|
| kimdogyeom | 1234 | 관리자 |

## 제출 문서

상세 설명은 `과제설명서.md` 참고.
