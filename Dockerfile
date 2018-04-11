FROM navikt/java:8
COPY target/betsys-agresso-integrasjonskomponent*.jar /app/app.jar
COPY pain.001.001.03.xsd /app/pain.001.001.03.xsd