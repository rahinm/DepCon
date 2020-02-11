DepCon: Dependency Control
==========================

![DepCon](src/main/resources/static/DepCon/images/DepCon-Logo.png)

DepCon is a simple web-based application to list, view and explore all libraries that your software is dependent on. 
It is currently designed for Java software built using the Maven build system. In a future iteration we aim to support
the Gradle build system as well.

Using the `mvn Dependency:list` command against your projects `pom.xml` you create a dependency list file 
for your software and upload the same into DepCon. Once uploaded, DepCon stores your software's dependency information 
in a database and provide you with a set of views to explore how your software is using other library components.

DepCon is built using the SparkJava micro-framework and JPA/Hibernate. It uses the Apache Derby as an embedded database.






