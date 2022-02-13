@echo off

set JAVA_EXE=D:\Test\jdk-11.0.14\bin\javaw.exe
set JAR_PATH=D:\Test\web-0.0.1-SNAPSHOT.jar
set SPRING_CFG=D:\Test\config\application.yml
set LOG_CFG=D:\Test\config\logback-spring.xml

start %JAVA_EXE% -jar %JAR_PATH% --spring.config.location=file:%SPRING_CFG% --logging.config=file:%LOG_CFG%

exit