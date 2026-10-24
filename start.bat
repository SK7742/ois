@echo off
setlocal
title Oxford International School - CSMS

cd /d "%~dp0"

:: Check arguments for execution mode
if "%1"=="" goto :mode_run
if /i "%1"=="run" goto :mode_run
if /i "%1"=="start" goto :mode_run
if /i "%1"=="stop" goto :mode_stop
if /i "%1"=="restart" goto :mode_restart
if /i "%1"=="status" goto :mode_status

echo ===============================================================================
echo                OXFORD INTERNATIONAL SCHOOL - CSMS
echo ===============================================================================
echo  Usage:
echo    start.bat           - Run the application (Default / No args)
echo    start.bat stop      - Stop the running application
echo    start.bat restart   - Stop and restart the application
echo    start.bat status    - Check if application is running
echo ===============================================================================
exit /b 1

:mode_stop
echo ===============================================================================
echo                OXFORD INTERNATIONAL SCHOOL - CSMS [STOP]
echo ===============================================================================
powershell -ExecutionPolicy Bypass -File "%~dp0stop.ps1"
exit /b 0

:mode_status
echo ===============================================================================
echo                OXFORD INTERNATIONAL SCHOOL - CSMS [STATUS]
echo ===============================================================================
powershell -NoProfile -Command "if (Get-NetTCPConnection -LocalPort 8080 -State Listen -ErrorAction SilentlyContinue) { Write-Host '[STATUS] Oxford CSMS is RUNNING on http://localhost:8080/' -ForegroundColor Green } else { Write-Host '[STATUS] Oxford CSMS is STOPPED (Port 8080 free).' -ForegroundColor Yellow }"
exit /b 0

:mode_restart
echo ===============================================================================
echo               OXFORD INTERNATIONAL SCHOOL - CSMS [RESTART]
echo ===============================================================================
echo [INFO] Stopping current instance...
powershell -ExecutionPolicy Bypass -File "%~dp0stop.ps1"
echo [INFO] Waiting 2 seconds before restarting...
timeout /t 2 /nobreak >nul
echo.
goto :mode_run

:mode_run
echo ===============================================================================
echo                OXFORD INTERNATIONAL SCHOOL - CSMS
echo                 Harpur, Raxaul, East Champaran, Bihar
echo ===============================================================================
echo  Authorized By  : Anil Kumar Gupt [Management]
echo  Lead Architect : Sundram Kumar
echo  Target Scale   : 1,500 - 2,000 Students
echo ===============================================================================
echo.

:: 1. Verify Java
where java >nul 2>nul
if errorlevel 1 (
    echo [ERROR] Java is not detected in your system PATH.
    echo Please ensure Java 17, 21, or 23 is installed and added to PATH.
    pause
    exit /b 1
)

:: 2. Display Java Version
for /f "tokens=*" %%v in ('java -version 2^>^&1') do (
    echo [INFO] %%v
    goto :java_checked
)
:java_checked

echo [INFO] Project Directory: %CD%
echo.

:: 3. Check if already running on port 8080
powershell -NoProfile -Command "if (Get-NetTCPConnection -LocalPort 8080 -State Listen -ErrorAction SilentlyContinue) { exit 10 } else { exit 0 }"
if errorlevel 10 (
    echo [WARNING] Oxford CSMS is already running on http://localhost:8080/
    echo [INFO] Run "start.bat restart" to restart, or "start.bat stop" to terminate.
    echo.
    start http://localhost:8080/
    exit /b 0
)

:: 4. Launch Browser in Background after short delay
start /b cmd /c "timeout /t 4 /nobreak >nul & start http://localhost:8080/"

:: 5. Start Application
if exist "backend\target\oxford-csms-backend-1.0.0-SNAPSHOT.jar" goto :run_jar
goto :run_mvn

:run_jar
echo [INFO] Starting pre-compiled Oxford CSMS executable JAR...
echo [INFO] Portal URL: http://localhost:8080/
echo [INFO] Press Ctrl+C in this window or run "start.bat stop" to stop the server.
echo ===============================================================================
echo.
java -Dfile.encoding=UTF-8 -jar "backend\target\oxford-csms-backend-1.0.0-SNAPSHOT.jar"
goto :eof

:run_mvn
echo [INFO] Pre-compiled JAR not found. Running via Maven Spring Boot plugin...
where mvn >nul 2>nul
if errorlevel 1 (
    echo [ERROR] Maven was not found in PATH to compile the project.
    pause
    exit /b 1
)
echo [INFO] Portal URL: http://localhost:8080/
echo [INFO] Press Ctrl+C in this window or run "start.bat stop" to stop the server.
echo ===============================================================================
echo.
mvn spring-boot:run -f backend\pom.xml
goto :eof
