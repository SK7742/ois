@echo off
setlocal
title Oxford International School - CSMS (Docker)

cd /d "%~dp0"

:: Check arguments for execution mode
if "%1"=="" goto :mode_run
if /i "%1"=="run" goto :mode_run
if /i "%1"=="start" goto :mode_run
if /i "%1"=="stop" goto :mode_stop
if /i "%1"=="restart" goto :mode_restart
if /i "%1"=="status" goto :mode_status
if /i "%1"=="logs" goto :mode_logs

echo ===============================================================================
echo                OXFORD INTERNATIONAL SCHOOL - CSMS [DOCKER]
echo ===============================================================================
echo  Usage:
echo    start.bat           - Build and run the containerized app (Default / No args)
echo    start.bat stop      - Stop the running Docker container
echo    start.bat restart   - Restart the Docker container
echo    start.bat status    - Check container and port 8080 status
echo    start.bat logs      - Stream live logs from the container
echo ===============================================================================
exit /b 1

:mode_stop
echo ===============================================================================
echo            OXFORD INTERNATIONAL SCHOOL - CSMS [DOCKER STOP]
echo ===============================================================================
docker info >nul 2>&1
if not errorlevel 1 (
    echo [INFO] Stopping Docker container...
    docker compose down
)
powershell -ExecutionPolicy Bypass -File "%~dp0stop.ps1"
exit /b 0

:mode_status
echo ===============================================================================
echo           OXFORD INTERNATIONAL SCHOOL - CSMS [DOCKER STATUS]
echo ===============================================================================
docker info >nul 2>&1
if errorlevel 1 (
    echo [STATUS] Docker daemon is NOT running.
) else (
    echo Container Status:
    docker ps --filter "name=oxford-csms-app"
)
echo.
powershell -NoProfile -Command "if (Get-NetTCPConnection -LocalPort 8080 -State Listen -ErrorAction SilentlyContinue) { Write-Host '[STATUS] Port 8080 is ACTIVE (http://localhost:8080/)' -ForegroundColor Green } else { Write-Host '[STATUS] Port 8080 is IDLE (Free).' -ForegroundColor Yellow }"
exit /b 0

:mode_logs
echo ===============================================================================
echo            OXFORD INTERNATIONAL SCHOOL - CSMS [LIVE LOGS]
echo ===============================================================================
docker logs -f oxford-csms-app
exit /b 0

:mode_restart
echo ===============================================================================
echo           OXFORD INTERNATIONAL SCHOOL - CSMS [DOCKER RESTART]
echo ===============================================================================
echo [INFO] Stopping current container...
docker compose down
echo [INFO] Waiting 2 seconds before restarting...
timeout /t 2 /nobreak >nul
echo.
goto :mode_run

:mode_run
echo ===============================================================================
echo                OXFORD INTERNATIONAL SCHOOL - CSMS
echo                 Harpur, Raxaul, East Champaran, Bihar
echo ===============================================================================
echo  Architecture   : Docker Container (App) + Local MySQL Host (Port 3306)
echo  Authorized By  : Anil Kumar Gupt [Management]
echo  Lead Architect : Shivam Kumar
echo  Target Scale   : 1,500 - 2,000 Students
echo ===============================================================================
echo.

:: 1. Verify Docker CLI
where docker >nul 2>nul
if errorlevel 1 (
    echo [ERROR] Docker was not found in your system PATH.
    echo Please install Docker Desktop and ensure docker CLI is accessible.
    pause
    exit /b 1
)

:: 2. Verify Docker Daemon is Running (Auto-launch if stopped)
docker info >nul 2>&1
if errorlevel 1 (
    echo [INFO] Docker daemon is not active. Attempting to start Docker Desktop...
    if exist "C:\Program Files\Docker\Docker\Docker Desktop.exe" (
        start "" "C:\Program Files\Docker\Docker\Docker Desktop.exe"
    )
    echo [INFO] Waiting for Docker engine to initialize (up to 45 seconds)...
    for /L %%i in (1,1,25) do (
        timeout /t 2 /nobreak >nul
        docker info >nul 2>&1
        if not errorlevel 1 goto :docker_ready
        <nul set /p=.
    )
    echo.
    echo [WARNING] Docker engine is taking longer to initialize.
    echo Please ensure Docker Desktop is open and shows "Engine running", then press any key to retry.
    pause
    docker info >nul 2>&1
    if errorlevel 1 (
        echo [ERROR] Docker daemon is still not reachable. Aborting.
        exit /b 1
    )
)
:docker_ready

echo [INFO] Docker daemon is active and ready.
echo [INFO] Project Directory: %CD%
echo [INFO] Database Topology: Local MySQL host (port 3306) -> Containerized App (port 8080)
echo.

:: 3. Ensure target JAR exists (build if missing)
if not exist "backend\target\oxford-csms-backend-1.0.0-SNAPSHOT.jar" (
    echo [INFO] Application JAR not found. Building with Maven on host...
    call mvn clean package -DskipTests -f backend\pom.xml
    if errorlevel 1 (
        echo [ERROR] Maven build failed.
        pause
        exit /b 1
    )
)

:: 4. Launch Browser in Background after container starts up
start /b cmd /c "timeout /t 6 /nobreak >nul & start http://localhost:8080/"

:: 5. Build and Start Container via Docker Compose
echo [INFO] Building and starting Oxford CSMS container...
echo ===============================================================================
docker compose up --build -d

echo.
echo [INFO] Container started. Live status:
docker ps --filter "name=oxford-csms-app"
echo.
echo ===============================================================================
echo  Application URL: http://localhost:8080/
echo  Health Check   : http://localhost:8080/actuator/health
echo ===============================================================================
echo  - To view live logs : run "start.bat logs" or "docker logs -f oxford-csms-app"
echo  - To stop container : run "start.bat stop" or "docker compose down"
echo ===============================================================================
echo.
echo Streaming startup logs (press Ctrl+C anytime to exit log view, container remains running)...
docker logs -f oxford-csms-app
exit /b 0
