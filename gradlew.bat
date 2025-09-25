@echo off
REM ------------------------------------------------------------------------------
REM Gradle startup script for Windows
REM ------------------------------------------------------------------------------

@echo off
setlocal

set DIRNAME=%~dp0
if "%DIRNAME%" == "" set DIRNAME=.

set APP_HOME=%DIRNAME%

set DEFAULT_JVM_OPTS=

"%JAVA_HOME%\bin\java" %DEFAULT_JVM_OPTS% -classpath "%APP_HOME%\gradle\wrapper\gradle-wrapper.jar" org.gradle.wrapper.GradleWrapperMain %*
