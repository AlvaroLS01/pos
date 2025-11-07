FROM registry.tier1.es/tier1-devops/producto/base/jdk:1.8.202

ENV COMERZZIA_HOME="${INSTALL_DIR}"

ENV APP_WAR=comerzzia-api-loyalty-application-2.0.1-SNAPSHOT.war \
    JAVA_OPTS="-Doracle.jdbc.timezoneAsRegion=false -Dfile.encoding=UTF-8"

COPY ["./application/src/main/docker/setup-app.sh", "./application/src/main/docker/comerzzia.xml", "./application/target/${APP_WAR}", "${INSTALL_DIR}/"]

CMD "${INSTALL_DIR}/java/bin/java" ${JAVA_OPTS} -Xmx${JAVA_OPTS_MX}m -DCOMERZZIA_HOME=${INSTALL_DIR} -jar ${APP_WAR}
