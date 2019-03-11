FROM navikt/java:8
COPY target/betsys-agresso-integrasjonskomponent*.jar /app/app.jar
ENV JAVA_OPTS="'-Dlogback.configurationFile=logback-remote.xml'"
COPY pain.001.001.03.xsd /app/pain.001.001.03.xsd