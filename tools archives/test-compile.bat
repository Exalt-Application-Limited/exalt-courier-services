@echo off
set JAVA_HOME=C:\Program Files\Microsoft\jdk-17.0.15.10-hotspot
set PATH=%JAVA_HOME%\bin;C:\Users\frich\Desktop\Micro-Social-Ecommerce-Ecosystems\apache-maven-3.9.9\bin;%PATH%

echo Testing branch-courier-app...
cd branch-courier-app
call mvn clean compile -q
if %ERRORLEVEL% EQU 0 (echo PASS) else (echo FAIL)
cd ..

echo Testing commission-service...
cd commission-service
call mvn clean compile -q
if %ERRORLEVEL% EQU 0 (echo PASS) else (echo FAIL)
cd ..

echo Testing routing-service...
cd routing-service
call mvn clean compile -q
if %ERRORLEVEL% EQU 0 (echo PASS) else (echo FAIL)
cd ..