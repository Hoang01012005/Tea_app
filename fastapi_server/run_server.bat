@echo off
title TeaCare FastAPI Backend Server
cd /d "%~dp0"
echo ====================================================
echo Starting TeaCare FastAPI Backend (DenseNet-121)
echo Host: http://127.0.0.1:8000
echo Android Emulator Base URL: http://10.0.2.2:8000
echo ====================================================
python main.py
pause
