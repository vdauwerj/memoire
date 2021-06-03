docker build -t jeremyvda/backend-app .
docker tag backend-app:latest backend-app
docker login
docker push jeremyvda/backend-app:latest
