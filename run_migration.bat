@echo off
echo Running Google OAuth Migration...
echo.

REM Check if psql is available
where psql >nul 2>nul
if %errorlevel% neq 0 (
    echo ERROR: PostgreSQL psql command not found!
    echo Please make sure PostgreSQL is installed and psql is in your PATH
    pause
    exit /b 1
)

REM Database connection parameters
set DB_HOST=localhost
set DB_PORT=5432
set DB_NAME=QuitSmokingDB
set DB_USER=postgres
set DB_PASSWORD=12345

echo Connecting to database: %DB_NAME% on %DB_HOST%:%DB_PORT%
echo.

REM Run the migration
psql -h %DB_HOST% -p %DB_PORT% -d %DB_NAME% -U %DB_USER% -f google_oauth_accounts_migration.sql

if %errorlevel% equ 0 (
    echo.
    echo SUCCESS: Migration completed successfully!
    echo Google OAuth accounts table has been created.
) else (
    echo.
    echo ERROR: Migration failed!
    echo Please check your database connection and try again.
)

echo.
pause 