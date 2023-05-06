call ./gradlew clean build
call docker build -t eist-ngrok ./
call docker compose up -d
