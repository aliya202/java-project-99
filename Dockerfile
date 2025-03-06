FROM eclipse-temurin:20-jdk

WORKDIR /app

COPY app/ .

RUN chmod +x gradlew

RUN ./gradlew installDist

CMD ["./build/install/app/bin/app"]