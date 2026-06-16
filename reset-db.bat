@echo off
echo H2 DB 파일을 삭제합니다 (서버가 꺼져 있어야 합니다).
del /Q "%USERPROFILE%\kimdogyeom.mv.db" 2>nul
del /Q "%USERPROFILE%\kimdogyeom.trace.db" 2>nul
echo 완료. Eclipse/터미널에서 서버를 다시 실행하세요.
