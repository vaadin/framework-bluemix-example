# Vaadin JPA CRUD

This is an example application that shows how your can build beautiful RIA UIs for your Bluemix backed execution environment (Liberty server and DB2 database).

To build this project, just execute following commands. They include checkout, you need to have java and Maven installed:

```
git clone https://hub.jazz.net/git/mattitahvonen/vaadin-jpa-app
cd vaadin-jpa-app
mvn install
```

or execute the same goal via your IDE.

In Bluemix you need to prepare an execution environment that provides Java EE 6 services for Vaadin and the database mapped into "jdbc/vaadindb". The easiest way to accomplish this, is to create it with Vaadin boilerplate. Manually, you can just create an SQLDB service with a name "vaadindb". Naturally you can also use different name, but then you'll need to modify persistence.xml in src/main/resources/META-INF accordingly.

Once you have the execution environment ready, just do the 
```
cf push <execution-env-name> -p target/vaadin-jpa-application.war
```
 and you have your first Vaadin app deployed to Bluemix!


### Local development

If you wan't to develop/debug the application locally, you'll just need to introduce the datasource in your local WAS Liberty Profile development server and deploy it there e.g. via your favorite IDE. The app also works with e.g. in memory Derby server.
