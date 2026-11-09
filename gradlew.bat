@echo off
setlocal

set APP_HOME=%~dp0
set APP_HOME=%APP_HOME:~0,-1%

set DEFAULT_JVM_OPTS=-Xmx4096m -Xms256m

if "%JAVA_HOME%" == "" (
    set JAVA=java
) else (
    set JAVA=%JAVA_HOME%\bin\java
)

set WRAPPER_JAR=%APP_HOME%\gradle\wrapper\gradle-wrapper.jar

if not exist "%WRAPPER_JAR%" (
    echo Gradle wrapper JAR not found at %WRAPPER_JAR%
    exit /b 1
)

%JAVA% -Xmx512m -cp "%WRAPPER_JAR%" org.gradle.wrapper.GradleWrapperMain %*