@echo off
echo ========================================
echo BeeTracker Build Script
echo ========================================

echo.
echo Cleaning previous build...
if exist bin rmdir /s /q bin
mkdir bin

echo.
echo Compiling Java source files...
javac -d bin src/*.java

if %ERRORLEVEL% EQU 0 (
    echo.
    echo Compilation successful!
    echo.
    echo To run the application, use: run.bat
    echo Or execute: java -cp bin Main
) else (
    echo.
    echo Compilation failed!
    exit /b 1
)