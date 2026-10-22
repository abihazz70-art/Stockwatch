@echo off
cd /d "%~dp0"
echo Compiling...
javac -d out src\stockwatch\*.java
if errorlevel 1 (
    echo Compile failed. Make sure Java JDK is installed.
    pause
    exit /b 1
)
echo.
echo Starting Stock Watchlist ^& Analyzer...
echo.
java -cp out stockwatch.Main
echo.
pause
