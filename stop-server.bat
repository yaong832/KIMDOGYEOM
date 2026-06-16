@echo off
cd /d "%~dp0"
setlocal enabledelayedexpansion

echo 8080 포트 사용 프로세스 확인 중...
set FOUND=0

for /f "tokens=5" %%a in ('netstat -ano ^| findstr ":8080" ^| findstr "LISTENING"') do (
    set PID=%%a
    set FOUND=1
    echo 종료 중: PID !PID!
    taskkill /PID !PID! /F >nul 2>&1
)

if !FOUND!==0 (
    echo 8080 포트를 사용 중인 프로세스가 없습니다.
) else (
    echo 8080 포트가 비었습니다. Eclipse에서 서버를 다시 실행하세요.
)

endlocal
pause
