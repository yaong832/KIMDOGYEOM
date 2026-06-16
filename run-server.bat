@echo off
cd /d "%~dp0"
setlocal enabledelayedexpansion

echo ========================================
echo  KIMDOGYEOM 서버 시작 (bootRun)
echo  브라우저: http://localhost:8080/
echo  종료: 이 창에서 Ctrl+C
echo ========================================
echo.

for /f "tokens=5" %%a in ('netstat -ano ^| findstr ":8080" ^| findstr "LISTENING"') do (
    echo [경고] 8080 포트가 이미 사용 중입니다. PID=%%a
    echo stop-server.bat 을 먼저 실행하거나 Eclipse 서버를 종료하세요.
    pause
    exit /b 1
)

call gradlew.bat bootRun
if errorlevel 1 (
    echo.
    echo [오류] 서버 시작에 실패했습니다.
    pause
)
