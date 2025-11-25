@echo off
echo ========================================
echo BeeTracker Application
echo ========================================

echo.
echo Starting BeeTracker...
java -cp bin Main

if %ERRORLEVEL% NEQ 0 (
    echo.
    echo Application failed to start!
    echo Make sure you have built the project first using build.bat
    pause
)