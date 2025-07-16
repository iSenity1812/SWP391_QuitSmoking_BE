Write-Host "Running Google OAuth Migration..." -ForegroundColor Green
Write-Host ""

# Check if psql is available
try {
    $null = Get-Command psql -ErrorAction Stop
} catch {
    Write-Host "ERROR: PostgreSQL psql command not found!" -ForegroundColor Red
    Write-Host "Please make sure PostgreSQL is installed and psql is in your PATH" -ForegroundColor Yellow
    Read-Host "Press Enter to exit"
    exit 1
}

# Database connection parameters
$DB_HOST = "localhost"
$DB_PORT = "5432"
$DB_NAME = "QuitSmokingDB"
$DB_USER = "postgres"
$DB_PASSWORD = "12345"

Write-Host "Connecting to database: $DB_NAME on $DB_HOST`:$DB_PORT" -ForegroundColor Cyan
Write-Host ""

# Set PGPASSWORD environment variable
$env:PGPASSWORD = $DB_PASSWORD

# Run the migration
try {
    $result = psql -h $DB_HOST -p $DB_PORT -d $DB_NAME -U $DB_USER -f "google_oauth_accounts_migration.sql" 2>&1
    
    if ($LASTEXITCODE -eq 0) {
        Write-Host ""
        Write-Host "SUCCESS: Migration completed successfully!" -ForegroundColor Green
        Write-Host "Google OAuth accounts table has been created." -ForegroundColor Green
    } else {
        Write-Host ""
        Write-Host "ERROR: Migration failed!" -ForegroundColor Red
        Write-Host "Error output: $result" -ForegroundColor Red
    }
} catch {
    Write-Host ""
    Write-Host "ERROR: Migration failed!" -ForegroundColor Red
    Write-Host "Exception: $($_.Exception.Message)" -ForegroundColor Red
}

Write-Host ""
Read-Host "Press Enter to continue" 