npm run build
docker build -t jeremyvda/front-end-apache .
docker tag front-end-apache:latest thesis/front-end-apache
docker login
docker push jeremyvda/front-end-apache:latest
