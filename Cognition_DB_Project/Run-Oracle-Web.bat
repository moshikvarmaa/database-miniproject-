@echo off
setlocal
cd /d "%~dp0"
set "JDBC_JAR=C:\Users\VARMAA\.vscode\extensions\oracle.sql-developer-26.3.0-win32-x64\dbtools\sqlcl\lib\ojdbc11-23.26.2.0.0.jar"
if not exist "%JDBC_JAR%" (
  echo Oracle JDBC driver not found at:
  echo %JDBC_JAR%
  echo Edit JDBC_JAR in this file to match your ojdbc jar path.
  pause
  exit /b 1
)
set "ORACLE_USER=COGNITION"
set /p "ORACLE_PASSWORD=Enter your COGNITION Oracle password: "
set "ORACLE_URL=jdbc:oracle:thin:@//localhost:1521/FREEPDB1"
if not exist bin mkdir bin
javac -cp "%JDBC_JAR%" -d bin java\src\CognitionWebServer.java
if errorlevel 1 (echo Compile failed.& pause & exit /b 1)
"%JAVA_HOME%\bin\java.exe" -version >nul 2>&1
if errorlevel 1 (
  java -cp "bin;%JDBC_JAR%" CognitionWebServer
) else (
  "%JAVA_HOME%\bin\java.exe" -cp "bin;%JDBC_JAR%" CognitionWebServer
)
pause
