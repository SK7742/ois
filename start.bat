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
echo    start.bat           - Check requirements, auto-start dependencies, and run app
echo    start.bat stop      - Stop the running Docker container and free port 8080
echo    start.bat restart   - Restart the Docker container
echo    start.bat status    - Check system requirements, container, and port 8080
echo    start.bat logs      - Stream live logs from the container
echo ===============================================================================
exit /b 1

:mode_stop
echo ===============================================================================
echo            OXFORD INTERNATIONAL SCHOOL - CSMS [STOP]
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
echo           OXFORD INTERNATIONAL SCHOOL - CSMS [STATUS DASHBOARD]
echo ===============================================================================
echo 1. Checking MySQL Database on Port 3306...
powershell -NoProfile -Command "if ((Test-NetConnection -ComputerName localhost -Port 3306 -WarningAction SilentlyContinue).TcpTestSucceeded) { Write-Host '   [OK] Local MySQL is ACTIVE on port 3306' -ForegroundColor Green } else { Write-Host '   [WARN] Local MySQL is NOT responding on port 3306' -ForegroundColor Red }"

echo 2. Checking Docker Engine Daemon...
docker info >nul 2>&1
if errorlevel 1 (
    echo    [WARN] Docker daemon is NOT running.
) else (
    echo    [OK] Docker daemon is ACTIVE and responsive.
    echo.
    echo Container Status:
    docker ps --filter "name=oxford-csms-app"
)
echo.
echo 3. Checking Web Application on Port 8080...
powershell -NoProfile -Command "if (Get-NetTCPConnection -LocalPort 8080 -State Listen -ErrorAction SilentlyContinue) { Write-Host '   [OK] Port 8080 is ACTIVE (http://localhost:8080/)' -ForegroundColor Green } else { Write-Host '   [IDLE] Port 8080 is free / stopped.' -ForegroundColor Yellow }"
exit /b 0

:mode_logs
echo ===============================================================================
echo            OXFORD INTERNATIONAL SCHOOL - CSMS [LIVE LOGS]
echo ===============================================================================
docker logs -f oxford-csms-app
exit /b 0

:mode_restart
echo ===============================================================================
echo           OXFORD INTERNATIONAL SCHOOL - CSMS [RESTART]
echo ===============================================================================
echo [INFO] Stopping current container...
docker compose down
echo [INFO] Waiting 2 seconds before restarting...
ping 127.0.0.1 -n 3 >nul
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

:: =============================================================================
:: REQUIREMENT 1: DOCKER CLI DETECTION
:: =============================================================================
echo [1/4] Checking Docker CLI availability...
where docker >nul 2>nul
if not errorlevel 1 goto :docker_cli_ready

if exist "C:\Program Files\Docker\Docker\resources\bin\docker.exe" (
    set "PATH=%PATH%;C:\Program Files\Docker\Docker\resources\bin"
)
where docker >nul 2>nul
if not errorlevel 1 goto :docker_cli_ready

echo [ERROR] Docker CLI was not found in your system PATH.
echo Please ensure Docker Desktop is installed.
pause
exit /b 1

:docker_cli_ready
echo       [OK] Docker CLI is available.

:: =============================================================================
:: REQUIREMENT 2: LOCAL MYSQL DATABASE (PORT 3306)
:: =============================================================================
echo [2/4] Checking Local MySQL Database on port 3306...
powershell -NoProfile -Command "if ((Test-NetConnection -ComputerName localhost -Port 3306 -WarningAction SilentlyContinue).TcpTestSucceeded) { exit 0 } else { exit 1 }"
if not errorlevel 1 goto :mysql_ready

echo       [WARN] MySQL is not responding on port 3306. Attempting to start MySQL service...
net start MySQL >nul 2>&1
net start MySQL80 >nul 2>&1
net start MySQL84 >nul 2>&1
ping 127.0.0.1 -n 3 >nul

powershell -NoProfile -Command "if ((Test-NetConnection -ComputerName localhost -Port 3306 -WarningAction SilentlyContinue).TcpTestSucceeded) { exit 0 } else { exit 1 }"
if not errorlevel 1 goto :mysql_started_ok

echo       [WARNING] MySQL service could not be auto-started.
echo       Please ensure your local MySQL server is running on port 3306 - schema: school.
echo       Press any key to retry or continue once MySQL is active.
pause
goto :mysql_done

:mysql_started_ok
echo       [OK] Local MySQL service started successfully on port 3306.
goto :mysql_done

:mysql_ready
echo       [OK] Local MySQL host database is active on port 3306.

:mysql_done

:: =============================================================================
:: REQUIREMENT 3: DOCKER DAEMON & ENGINE AUTO-START
:: =============================================================================
echo [3/4] Checking Docker Engine daemon...
docker info >nul 2>&1
if not errorlevel 1 goto :docker_daemon_ready

echo       [WARN] Docker daemon is not active. Auto-starting Docker Desktop...

if exist "C:\Program Files\Docker\Docker\Docker Desktop.exe" (
    explorer.exe "C:\Program Files\Docker\Docker\Docker Desktop.exe"
) else if exist "%ProgramFiles%\Docker\Docker\Docker Desktop.exe" (
    explorer.exe "%ProgramFiles%\Docker\Docker\Docker Desktop.exe"
) else if exist "C:\Program Files\Docker\Docker\resources\com.docker.backend.exe" (
    start "" "C:\Program Files\Docker\Docker\resources\com.docker.backend.exe"
)

echo       [INFO] Waiting for Docker engine to become responsive...
set /a attempts=0

:docker_wait_loop
set /a attempts+=1
ping 127.0.0.1 -n 3 >nul
docker info >nul 2>&1
if not errorlevel 1 goto :docker_daemon_ready
<nul set /p=.
if %attempts% lss 35 goto :docker_wait_loop

echo.
echo       [WARNING] Docker engine is taking longer to initialize.
echo       Please ensure Docker Desktop is open and shows "Engine running".
echo       Press any key to retry connecting to Docker, or Ctrl+C to abort.
pause >nul
docker info >nul 2>&1
if errorlevel 1 (
    echo       [ERROR] Docker daemon is still not reachable. Aborting.
    exit /b 1
)

:docker_daemon_ready
echo.
echo       [OK] Docker daemon is online and responsive!

:: =============================================================================
:: REQUIREMENT 4: APPLICATION ARTIFACT & CONTAINER LAUNCH
:: =============================================================================
echo [4/4] Verifying application build and starting container...

if exist "backend\target\oxford-csms-backend-1.0.0-SNAPSHOT.jar" goto :jar_ready

echo       [INFO] Application JAR not found. Building with Maven on host...
call mvn package -DskipTests -f backend\pom.xml
if not errorlevel 1 goto :jar_ready

echo [ERROR] Maven build failed.
pause
exit /b 1

:jar_ready
echo       [INFO] Launching Oxford CSMS Docker container...
docker compose up -d --build

echo       [INFO] Waiting for application to initialize on http://localhost:8080/ ...
set /a h_attempts=0

:health_wait_loop
set /a h_attempts+=1
ping 127.0.0.1 -n 3 >nul
powershell -NoProfile -Command "$r = try { (Invoke-WebRequest -Uri 'http://localhost:8080/actuator/health' -TimeoutSec 2 -UseBasicParsing).StatusCode } catch { 0 }; if ($r -eq 200) { exit 0 } else { exit 1 }"
if not errorlevel 1 goto :app_is_healthy
<nul set /p=.
if %h_attempts% lss 30 goto :health_wait_loop

:app_is_healthy
echo.
echo ===============================================================================
echo                OXFORD INTERNATIONAL SCHOOL - CSMS IS RUNNING!
echo ===============================================================================
echo  Application Portal : http://localhost:8080/
echo  Health Endpoint    : http://localhost:8080/actuator/health
echo -------------------------------------------------------------------------------
echo  Lead Architect     : Er. Shivam Kumar (shivam.kumar / Shivam@123)
echo  Administrator      : Anil Kumar Gupt (admin / Admin@123)
echo  Teacher Portal     : Dr. Satish Sharma (teacher.sharma / Teacher@123)
echo  Student Portal     : Aarav Kumar (student.aarav / Student@123)
echo -------------------------------------------------------------------------------
echo  Control Commands:
echo    start.bat stop     - Stop the application and free port 8080
echo    start.bat restart  - Restart the application
echo    start.bat logs     - View live container logs
echo    start.bat status   - View system and container health status
echo ===============================================================================
echo.

:: Automatically open browser
start http://localhost:8080/

exit /b 0
