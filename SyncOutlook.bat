@Echo Off
title OutLookDownLoad

set LIBPATH=lib
set JAVA_HOME=D:\Java\jdk1.8.0_45
set JAVA=%JAVA_HOME%/bin/java

Rem ============= CLASSPATH ===================
Echo Generating classpath ...
set CLASSPATH=config
set CLASSPATH=%CLASSPATH%;./outlookdownload-1.0.0.jar
set CLASSPATH=%CLASSPATH%;./lib/moyocore_x64.dll
echo "OutLookDownLoad started."
%JAVA% -Djava.library.path=./lib -cp %CLASSPATH% -jar outlookdownload-1.0.0.jar

