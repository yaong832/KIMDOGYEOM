# KIMDOGYEOM Board

**Kimdogyeom Board** — Spring Boot 기반 반도체 장비 Q&A 커뮤니티 (기말 대체 과제)

## 프로젝트 정보

| 항목 | 내용 |
|------|------|
| 프로젝트명 | KIMDOGYEOM |
| 패키지 | `com.mysite.kimdogyeomboard` |
| 개발자 | KIM DO GYEOM |
| 주제 | 반도체 장비 Q&A 커뮤니티 |
| GitHub | https://github.com/yaong832/KIMDOGYEOM |

## 기술 스택

- Java 17, Spring Boot 4.0.3, Gradle
- Spring Security, Thymeleaf, Spring Data JPA, **H2 Database**
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
- 관리자: 답변 숨김 / 숨김 해제
- 403/404 안내 페이지, 샘플 테스트 데이터

## 실행 방법

```bash
.\gradlew.bat bootRun
```

| 화면 | URL |
|------|-----|
| 메인 | http://localhost:8080/ |
| 로그인 | http://localhost:8080/member/login |
| H2 Console | http://localhost:8080/h2-console |

### H2 접속
- JDBC URL: `jdbc:h2:~/kimdogyeom;AUTO_SERVER=TRUE`
- Username: `sa` / Password: (비움)
- DB 파일: `C:\Users\{사용자}\kimdogyeom.mv.db`

### 테스트 계정
| 아이디 | 비밀번호 | 등급 |
|--------|----------|------|
| kimdogyeom | 1234 | 관리자 |
| parktech | 1234 | 일반회원 |
| leeequip | 1234 | 일반회원 |

## 제출 문서

기말 과제 제출용 상세 설명: **`과제설명서.md`**
