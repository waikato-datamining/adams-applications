@echo off

"%~dp0\launcher.bat" -main adams.gui.Main -memory 512m -title ADAMS-Addons -env-modifier adams.core.management.WekaHomeEnvironmentModifier
