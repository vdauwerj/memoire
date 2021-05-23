npm run build
docker build -t front-end-apache .
docker tag front-end-apache:latest thesis/front-end-apache
docker login
docker push thesis/front-end-apache:latest
