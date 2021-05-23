npm run build
docker build -t front-end-apache .
docker tag front-end-apache:latest group6labo/front-end-apache
docker login
docker push group6labo/front-end-apache:latest
