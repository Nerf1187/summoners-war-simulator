@echo off
for /f "tokens=1,2,3 delims=," %%A in ('call ..\versionCheck.bat') do (
    set "status=%%A"
    set "userVersion=%%B"
    set "minVersion=%%C"

    if "%status%"=="PASS" or "%status%"=="FAIL" (
        goto Check
    )
)

:Check
if "%status%"=="PASS" (
    goto Run
) else (
    goto End
)

:Run
java -jar --enable-preview "Main".jar
pause
exit /b

:End
echo [0;31mError: Invalid Java version detected, please update to a supported version
echo Your Java Version: %userVersion%
echo Minimum Java requirement: %minVersion%[0m
pause
exit /b