DepCon: Dependency Control
==========================

![DepCon](src/main/resources/static/DepCon/images/DepCon-Logo.png)

DepCon is a simple web-based application to list, view and explore all libraries that your software is dependent on. 
It is currently designed for Java software built using the Maven build system. In a future iteration we aim to support
the Gradle build system as well.

Using the `mvn Dependency:list` command against your projects `pom.xml` you create a dependency list file 
for your software application and upload the same into DepCon. Once uploaded, DepCon stores your software's dependency 
information in a database and provide you with a set of views to explore how your software is using other library components.

DepCon is built using the SparkJava micro-framework and JPA/Hibernate. It uses Apache Derby as an embedded database.

Running
-------
You have a single fat jar file `DepCon.jar`. Run this application using the command below,

`java -jar DepCon.jar`

When run the application will create a directory `data` where the embedded database files will be saved.
 
Configuration
-------------
You may set the following DepCon specific Java properties in a file `DepCon.properties` in the `config` directory relative
to where DepCon.jar resides.

| Property Name                | Description                                 | Note                              |
|------------------------------|-------------------------------------------- |-----------------------------------|
| depcon.listener.port         | listener port number                        | Optional [default: 10080]         |
| depcon.network.security      | set to true to enable TLS transport         | Optional [default: false]         |
| depcon.keystore.filename     | Java keystore file name for TLS support     | Conditional [when TLS is enabled] |
| depcon.keystore.password     | Password for the Java keystore file         | Conditional [when TLS is enabled] |
| depcon.ignore.artifacts.from | Comma separated list of group ids to ignore | Optional                          | 

Users Authentication
--------------------
DepCon enforces HTTP Basic Authorization to authenticate users. You can use the below command to create users.
(The below assumes you are running the command in the directory where DepCon.jar is present).

`java -cp DepCon.jar net.dollmar.svc.depcon.utils.CreateUser`

Answer few questions and user identities will be created in a file `config/users.dat`.

Uploading Dependency Files
-------------------------
There are two ways you may upload an application's dependency files.

### Web GUI ###
Select the `Import Dependency List` from the right-hand side pane of the Web GUI. On the form that is presented
enter your application name and verison and then click on the `Choose file` button to select the dependency list
file you have created. Finally click on the large green coloured `Upload` button.

### Using cUrl ###
You may use cUrl to upload your application's dependency file by using a command like below:

`curl -u <user-name>:<password> -F app_name=<application-name> -F app_version=<app-version> -F dep_file=@<dependency-list-file-name> <DepCon-URL>/import`

where you must replace the values within angle brackets (`<..>`) with correct and valid values.

License
-------
This application is free to use by anyone without any restrcition and is released as a open source software under 
Apache License Version 2.0. Please browse to https://www.apache.org/licenses/LICENSE-2.0 for details of the provisions 
of this license.



