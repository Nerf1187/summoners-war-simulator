@echo off
set minVersion=25
for /f "tokens=2 delims== " %%i in ('java -XshowSettings:properties -version 2^>^&1 ^| find "java.version"') do (
    set "javaVersion=%%i"
    goto Check
)

:Check
rem Extract the major version number (before the first dot)
for /f "tokens=1 delims=." %%j in ("%javaVersion%") do set "majorVersion=%%j"

rem Compare the major version number
if %majorVersion% GEQ %minVersion% (
    echo PASS,%majorVersion%,%minVersion%
) else (
    echo FAIL,%majorVersion%,%minVersion%
)