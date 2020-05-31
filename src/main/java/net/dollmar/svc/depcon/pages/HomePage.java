/*
Copyright 2020 Mohammad A. Rahin                                                                                                          

Licensed under the Apache License, Version 2.0 (the "License");                                                                           
you may not use this file except in compliance with the License.                                                                          
You may obtain a copy of the License at                                                                                                   
    http://www.apache.org/licenses/LICENSE-2.0                                                                                            
Unless required by applicable law or agreed to in writing, software                                                                       
distributed under the License is distributed on an "AS IS" BASIS,                                                                         
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.                                                                  
See the License for the specific language governing permissions and                                                                       
limitations under the License.       
 */

package net.dollmar.svc.depcon.pages;

public class HomePage {
  
  private static final String PAGE_INFO =
      "<?xml version='1.0'?>\n" + 
      "<html> <head> <title>DepCon - Dependency control </title> <link rel='stylesheet' type='text/css' href='StyleSheets/DepCon.css' />" +
      "<meta http-equiv='cache-control' content='no-cache'/> </head>" +
      "<body bgcolor='#b8d6ca' leftmargin='10' rightmargin='10' topmargin='20'>" +
      
      "<p> <img src='images/DepCon-Logo.png' alt='DepCon'> </p>" + 
      
      "<p> DepCon is a simple web-based application to list, view and explore all libraries that your software is dependent on." + 
      "It is currently designed for Java software built using the Maven build system. In a future iteration we aim to support " +
      "the Gradle build system as well. <br><br>" +
      
      "Using the <b>mvn Dependency:list</b> command against your project's <b>pom.xml</b> file you create a dependency list file " + 
      "for your software and upload the same into DepCon. Once uploaded, DepCon stores your software's dependency information in " + 
      "a database and provide you with a set of views to explore how your software is using other library components. <br><br>" +
      
      "DepCon is built using the SparkJava micro-framework and JPA/Hibernate. It uses the Apache Derby as an embedded database. </p>" +
      "</body> </html>";
  
  
  public static String render() {
    return PAGE_INFO;
  }
}
