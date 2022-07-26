FROM eclipse-temurin
RUN mkdir /opt/app
COPY target/java-sv2-adv-project-DomjanGabor-0.0.1-SNAPSHOT.jar /opt/app/invoicekeeper.jar
CMD ["java", "-jar", "/opt/app/invoicekeeper.jar"]