@echo off
echo Building Courier Services...
echo.

set JAVA_HOME=C:\Program Files\Microsoft\jdk-17.0.15.10-hotspot
set MAVEN_HOME=C:\Users\frich\Desktop\Micro-Social-Ecommerce-Ecosystems\apache-maven-3.9.9
set PATH=%JAVA_HOME%\bin;%MAVEN_HOME%\bin;%PATH%

echo Using Java: %JAVA_HOME%
echo Using Maven: %MAVEN_HOME%
echo.

echo ============================================
echo Building branch-courier-app...
echo ============================================
cd branch-courier-app
call mvn clean compile
if %ERRORLEVEL% NEQ 0 (
    echo FAILED: branch-courier-app compilation failed
) else (
    echo SUCCESS: branch-courier-app compiled successfully
)
cd ..

echo.
echo ============================================
echo Building commission-service...
echo ============================================
cd commission-service
call mvn clean compile
if %ERRORLEVEL% NEQ 0 (
    echo FAILED: commission-service compilation failed
) else (
    echo SUCCESS: commission-service compiled successfully
)
cd ..

echo.
echo ============================================
echo Building courier-management...
echo ============================================
cd courier-management
call mvn clean compile
if %ERRORLEVEL% NEQ 0 (
    echo FAILED: courier-management compilation failed
) else (
    echo SUCCESS: courier-management compiled successfully
)
cd ..

echo.
echo ============================================
echo Building routing-service...
echo ============================================
cd routing-service
call mvn clean compile
if %ERRORLEVEL% NEQ 0 (
    echo FAILED: routing-service compilation failed
) else (
    echo SUCCESS: routing-service compiled successfully
)
cd ..

echo.
echo ============================================
echo Building tracking-service...
echo ============================================
cd tracking-service
call mvn clean compile
if %ERRORLEVEL% NEQ 0 (
    echo FAILED: tracking-service compilation failed
) else (
    echo SUCCESS: tracking-service compiled successfully
)
cd ..

echo.
echo ============================================
echo Build Summary
echo ============================================
echo Check above for any FAILED services
echo.
pause