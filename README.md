# KIMDOGYEOM Board

**Kimdogyeom Board** — Spring Boot 기반 반도체 장비 Q&A 게시판 (기말 대체 과제)

## 프로젝트 정보

| 항목 | 내용 |
|------|------|
| 프로젝트명 | KIMDOGYEOM |
| 패키지 | `com.mysite.kimdogyeomboard` |
| 개발자 | KIM DO GYEOM |
| 주제 | 반도체 장비 Q&A 게시판 |

## 기술 스택

- Java 17, Spring Boot 4.0.3, Gradle
- Thymeleaf, Spring Data JPA, H2 Database
- Bootstrap 5, Lombok, DevTools

## 구현 기능

- 게시글 목록 / 상세 / 등록 / 수정 / 삭제
- 답변 등록 (Question 1:N Answer)
- Bootstrap UI, layout.html 공통 레이아웃
- H2 Console DB 확인

## 실행 방법

```bash
.\gradlew.bat bootRun
```

- 메인: http://localhost:8080/
- H2 Console: http://localhost:8080/h2-console
  - JDBC URL: `jdbc:h2:~/kimdogyeom`
  - Username: `sa` / Password: (비움)

## 가산점 개선 요소

- 본인 이름 기반 게시판 제목 (Kimdogyeom Board)
- 반도체 장비 Q&A 주제 및 히어로 배너
- 네비게이션 바, 푸터
- 목록 답변 개수, 빈 목록 안내
- 최신글 우선 정렬
- 반도체 테마 컬러 디자인
