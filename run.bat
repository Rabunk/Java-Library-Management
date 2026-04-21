@echo off
REM Compile and run Library Management System
set "JAVA_HOME=C:\Program Files\Java\jdk-24"
set "PATH=%JAVA_HOME%\bin;%PATH%"
echo.
echo ===== Library Management System =====
echo.

REM Check if Maven is installed
where mvn >nul 2>nul
if %errorlevel% neq 0 (
    echo ERROR: Maven not found in PATH!
    echo Please install Maven or add it to your system PATH.
    echo Download Maven from: https://maven.apache.org/download.cgi
    pause
    exit /b 1
)

REM Check if Java is installed
where java >nul 2>nul
if %errorlevel% neq 0 (
    echo ERROR: Java not found in PATH!
    echo Please install Java 11 or higher.
    pause
    exit /b 1
)

REM Show Java version
echo Java version:
java -version
echo.

REM Clean and build
echo Building project...
call mvn clean compile package

if %errorlevel% neq 0 (
    echo ERROR: Build failed!
    pause
    exit /b 1
)

echo.
echo ===== Build successful! =====
echo.
echo Starting application...
echo.

REM Run the application
java -cp target/library-management-1.0-jar-with-dependencies.jar com.library.gui.MainFrame

pause
