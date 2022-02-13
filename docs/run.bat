@echo off

for /f %%i in ('dir /b *.jar') do (
  set JAR_PATH=%%i
)

for /R %cd% %%i in (*javaw.exe) do (
  set JAVAW_PATH=%%i
)

start %JAVAW_PATH% -jar %JAR_PATH%

exit