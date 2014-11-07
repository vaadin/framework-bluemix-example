# Vaadin JPA CRUD

This is an example application that shows how your can build beautiful RIA UIs for your Bluemix backed execution environment (Liberty server and DB2 database).

To build this project, just execute command (needs Maven to be installed):

```
mvn install
```

or execute the same goal via your IDE.

In Bluemix you need to prepare an execution environment that provides Java EE 6 services for Vaadin and the database mapped into "jdbc/customerdb". The easiest way to accomplish this, is to create it with Vaadin boilerplate.

Once you have the execution environment ready, just do the "cf push <execution-env-name> -p target/vaadin-jpa-application.war" and you have your first Vaadin app deployed to Bluemix!


