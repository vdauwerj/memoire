FROM httpd:2.4
WORKDIR /usr/local/apache2/conf
COPY ./httpd.conf .
WORKDIR /usr/local/apache2/htdocs
COPY ./dist/web-app .
EXPOSE 8000

