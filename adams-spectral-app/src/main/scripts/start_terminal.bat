@echo off

java -Djava.awt.headless=true -cp "%~dp0\..\lib\*" -Xmx2g adams.terminal.Main -title ADAMS-Spectral
