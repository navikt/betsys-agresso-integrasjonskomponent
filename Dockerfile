FROM navikt/java:8
COPY target/betsys-agresso-integrasjonskomponent*.jar /app/app.jar
ENV JAVA_OPTS="${JAVA_OPTS} -Dlogging.config=classpath:logback-remote.xml"
COPY pain.001.001.03.xsd /app/pain.001.001.03.xsd