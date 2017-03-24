# Vaadin JPA CRUD

This is an example application that shows how your can build rich UIs for your Bluemix backed execution environment (Liberty server and a relational database) with the opensource Vaadin Framework.

### Automatic deployment to Bluemix

Go to the Bluemix console. From the Catalog, choose ElephantSQL and create a service with name *vaadindb*. The free *tiny turtle* plan is fine for testing. Alternatively you can create a full execution environment from the Catalog, by choosing Vaadin boilerplate.

[![Deploy to Bluemix](https://bluemix.net/deploy/button.png)](https://bluemix.net/deploy)

When you click this button, Bluemix will clone this repository to a private Bluemix DevOps Services project, create a pipeline to compile the source, create the necessary database service (see `manifest.yml`) and then push the application.

To continue developing your app, clone the repository that was created for you from the hub.jazz.net and then just commit and push your changes. Commit triggers a new build by the DevOps chain and deploys a new version of your app.

### Manual deployment to Bluemix

Go to the Bluemix console. From the Catalog, choose ElephantSQL and create a service with name *vaadindb*. The free *tiny turtle* plan is fine for testing.

Before you start, make sure you have Java SDK 1.8 (or higher) and Maven installed. Also [install cloudfoundry command line tools](https://console.ng.bluemix.net/docs/cli/index.html) and configure them for Bluemix.

To build the project, just execute the following commands in order:

```
git clone https://hub.jazz.net/git/vaadin/vaadin-jpa-app
cd vaadin-jpa-app
mvn install
```

or execute the same goal via your IDE.

In Bluemix you need to prepare an execution environment, that provides a Java EE 7 server and a database mapped to "jdbc/vaadindb". You can do this manually from the console with Liberty server and an ElephantSQL (PostgreSQL) database connected to it. Name the database "vaadindb" and Bluemix will automatically configure driver, DataSource and setup a proper JNDI entry the database.

Alternatively you can trust the magic in manifest.yml file to create the required execution environement.

```
cf push <app-name> -p target/vaadin-jpa-application.war
```
... and you have your first Vaadin app deployed to Bluemix!

### Local development

If you want to develop/debug the application locally, you'll just need to introduce the data source in your local WAS Liberty Profile development server and deploy it there e.g. via your favorite IDE. Virtually any DB works, so no need to install database or connect to a local database. E.g. an in memory Derby server works just fine, simple instructions below.

* Download and place a derby.jar file to usr/shared/resources/derby/derby.jar into your Liberty server directory.
* Enable required features and a Derby based datasource by configuring your development server's server.xml (most likely usr/servers/defaultServer/server.xml in your Liberty server directory). It could look like this:
```
<server description="new server">
  <!-- Enable features, jpa-2.0, cdi-1.0 and servlet-3.0 are required, but handier to just
       enable the whole webProfile specification -->
  <featureManager>
    <feature>localConnector-1.0</feature>
    <feature>webProfile-6.0</feature>
  </featureManager>
  <!-- To access this server from a remote client add a host attribute to 
		the following element, e.g. host="*" -->
  <httpEndpoint httpPort="9080" httpsPort="9443" id="defaultHttpEndpoint"/>
  <!-- JDBC Driver configuration -->
  <jdbcDriver id="DerbyEmbedded" libraryRef="DerbyLib"/>
  <library filesetRef="DerbyFileset" id="DerbyLib"/>
  <fileset dir="${shared.resource.dir}/derby" id="DerbyFileset" includes="derby.jar"/>
  <!-- Configure an in-memory db for the vaadin app configuration -->
  <dataSource id="jdbc/vaadindb" jdbcDriverRef="DerbyEmbedded" jndiName="jdbc/vaadindb" transactional="true">
    <properties createDatabase="create" databaseName="memory:jpasampledatabase"/>
  </dataSource>
</server>
```

If you are using Eclipse, it might be bit picky about configuring the project properly based on the pom.xml. Via IntelliJ the deployment works usually easier. Couple of Eclipse related tips to setup the project:
 
 * Import to your workspace using File->Import->Maven->Existing Maven project
 * If Eclipse creates an "ear project" in addition to the war project, you can just delete the ear project
 * Make one full build with "Run as-> Maven install" after import to get client side resources prepared
 * In case Eclipse asks to modify server.xml during deployment, just ignore it. For some reason Eclipse may "detect" that project needs services it really don't need. webProfile-6.0 in server xml (and a connector) is enough.
 * In case it still don't work, get a fresh Eclipse, install only latest Liberty Profile plugins and try again. Some Eclipse plugins may disturb the process.

### Troubleshooting

**The application doesn't build properly** 

If you have [Maven](https://maven.apache.org/download.cgi) and Java 8 or later installed, the most common problem is that you are using a Macintosh and your JAVA_HOME environment variable still points to version 1.6 of Java. An easy way to fix this is executing: 
```export JAVA_HOME=`/usr/libexec/java_home -v 1.7` ```
and/or adding that to your .bash_profile file.

Also note, that if you haven't used Maven before, the build may take several minutes during the first run as Maven downloads several dependencies used by the application itself and the build.
 
**The deployment fails**

Deploying and setting up the database may take a while with a slow network connection, so be patient. In some cases there might happen an error due to e.g. network communication. Canceling the deployment with CTRL-C and trying again usually fixes the issue.