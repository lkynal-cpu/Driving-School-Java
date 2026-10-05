@echo off
cd /d "%~dp0"
if not exist out mkdir out
dir /s /b src\*.java > sources.txt
javac -d out @sources.txt
if errorlevel 1 goto end
java -cp out ug.ac.vu.g13.app.Main
:end
del sources.txt
