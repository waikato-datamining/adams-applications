@echo off

java -Djava.awt.headless=true -cp "%~dp0\..\lib\*" -Xmx512m adams.terminal.Main
