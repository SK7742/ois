@echo off
setlocal
title Oxford International School - Configure Superuser Admin

cd /d "%~dp0"

where python >nul 2>nul
if errorlevel 1 (
    echo [ERROR] Python is not found in your PATH.
    echo Please install Python 3.10+ or add python.exe to your environment PATH.
    pause
    exit /b 1
)

python "%~dp0configure_superuser.py" %*
exit /b %ERRORLEVEL%
