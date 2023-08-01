FROM adoptopenjdk:11-jre-hotspot as builder
WORKDIR /workspace/
ARG JAR_FILE=build/ms/*.jar
COPY ${JAR_FILE} application.jar
RUN java -Djarmode=layertools -DLOGS_DIR=/etc/ff/logs -jar application.jar extract

FROM adoptopenjdk:11-jre-hotspot
COPY --from=builder /workspace/dependencies/ ./
COPY --from=builder /workspace/spring-boot-loader/ ./
COPY --from=builder /workspace/snapshot-dependencies/ ./
#https://github.com/moby/moby/issues/37965#issuecomment-426853382
RUN true
COPY --from=builder /workspace/application/ ./
ENTRYPOINT ["java", "org.springframework.boot.loader.JarLauncher"]
