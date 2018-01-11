@echo off

"%~dp0\terminal.bat" -memory 512m -main adams.terminal.Main -title ADAMS-Addons -env-modifier adams.core.management.WekaHomeEnvironmentModifier
