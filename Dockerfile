# TODO: Add base image
# TODO: Set workdir
# TODO: Copy the compiled jar
# TODO: Copy the start.sh script
# TODO: Make start.sh executable
# TODO: Set the start command
FROM openjdk:17-bullseye
WORKDIR /app
COPY ./build/libs/H10E01-Containers-1.0.0.jar ./app.jar
COPY ./start.sh ./
CMD chmod 770 start.sh
CMD ./start.sh
