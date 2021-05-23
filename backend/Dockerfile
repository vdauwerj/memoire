FROM openjdk:8
RUN mkdir /opt/app
WORKDIR /opt/app
ADD ./build/libs/application-0.1.5.jar /opt/app
CMD  java -jar application-0.1.5.jar
EXPOSE 8080

