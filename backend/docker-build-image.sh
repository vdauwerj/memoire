docker build -t backend-app .
docker tag backend-app:latest thesis/backend-app
docker login
docker push thesis/backend-app:latest
