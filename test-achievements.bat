@echo off
echo Testing Achievement API...
echo.

echo 1. Getting all achievements...
curl -s http://localhost:8080/api/achievements
echo.
echo.

echo 2. Getting achievement by ID 1...
curl -s http://localhost:8080/api/achievements/1
echo.
echo.

echo 3. Creating a new test achievement...
curl -s -X POST http://localhost:8080/api/achievements ^
  -H "Content-Type: application/json" ^
  -d "{\"name\": \"Test Achievement\", \"iconUrl\": \"/icons/test.png\", \"criteria\": \"{\\\"type\\\": \\\"daysWithoutSmoking\\\", \\\"value\\\": 5}\", \"description\": \"Test achievement description\"}"
echo.
echo.

echo 4. Getting all achievements again to see the new one...
curl -s http://localhost:8080/api/achievements
echo.
echo.

echo Test completed!
pause 