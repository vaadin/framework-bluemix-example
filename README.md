# Vaadin JPA CRUD

This is an example application that shows how your can build beautiful RIA UIs for your Bluemix backed execution environment (Liberty server and DB2 database).

To build this project, just execute following commands. Before you start, make sure you need to have Java SDK 1.7 (or higher) and Maven installed:

```
git clone https://hub.jazz.net/git/vaadin/vaadin-jpa-app
cd vaadin-jpa-app
mvn install
```

or execute the same goal via your IDE.

In Bluemix you need to prepare an execution environment, that provides a Java EE 6 server for Vaadin UI and a database mapped to "jdbc/vaadindb". The easiest way to accomplish this, is to is to use the Vaadin boilerplate. Just follow the "}>" logo in [Bluemix](http://bluemix.net/) catalog. Manually, you can just create an SQLDB service with a name "vaadindb". Naturally you can also use different name, but then you'll need to modify persistence.xml in src/main/resources/META-INF accordingly.

Once you have the execution environment ready, just issue following command 
```
cf push <app-name> -p target/vaadin-jpa-application.war
```
... and you have your first Vaadin app deployed to Bluemix!

### Local development

If you wan't to develop/debug the application locally, you'll just need to introduce the datasource in your local WAS Liberty Profile development server and deploy it there e.g. via your favorite IDE. The app also works with e.g. in memory Derby server.

### Troubleshooting

**The application don't build properly** 

If you have [Maven](https://maven.apache.org/download.cgi) and Java 7 or later installed, the most common problem is that you are using Mac and your JAVA_HOME environment variable still points to 1.6 version of Java. An easy way to fix this is executing: 
```export JAVA_HOME=`/usr/libexec/java_home -v 1.7` ```
and/or adding that to your .bash_profile file.

Also note, that if you haven't used Maven before, the build may take several minutes on first run as Maven downloads several dependencies used by the application itself and the build.
 
**The deployment fails**

Deploying may take a while with a slow network connection, so be patient. In some cases there might happen an error due to e.g. network communication. Cancelling the deployment with CTRL-C and trying again usually fixes the issue.
