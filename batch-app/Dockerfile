FROM amazoncorretto:11
LABEL maintainer="kazusato"

RUN yum install -y glibc-langpack-ja
ENV LANG ja_JP.UTF8
ENV LC_ALL ja_JP.UTF8
RUN ln -sf /usr/share/zoneinfo/Japan /etc/localtime

VOLUME /tmp

COPY newrelic/newrelic.jar /app/newrelic/
COPY newrelic/newrelic-api.jar /app/newrelic/
COPY newrelic/newrelic.yml /app/newrelic/
RUN chmod 755 -R /app/newrelic

ARG JAR_FILE
COPY ${JAR_FILE} app.jar

ENTRYPOINT ["java", \
 "-javaagent:/app/newrelic/newrelic.jar",\
 "-verbose:gc", \
 "-Xlog:gc*:stdout:time,uptime,level,tags", \
 "-Djava.security.egd=file:/dev/./urandom", \
 "-jar", \
 "/app.jar"]
