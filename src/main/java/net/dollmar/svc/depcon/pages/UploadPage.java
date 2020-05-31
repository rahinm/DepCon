package net.dollmar.svc.depcon.pages;

import java.util.Collections;
import java.util.List;

import net.dollmar.svc.depcon.dao.ApplicationDao;

public class UploadPage {
  
  
  private static final String PAGE_TOP_PART = 
      "<!DOCTYPE html> <html lang='en'>" + 
      "<head> <meta charset='UTF-8'> <title>DepCon - Utility to upload dependency list file</title> <link rel='stylesheet' type='text/css' href='StyleSheets/DepCon.css' /> </head>" +
      "<body> <h2>Fill the form below to upload your application's dependency list file</h2>" +
      "<h3><i>Important: Ensure that the file was created using 'mvn dependency:list' command</i></h3>" +
      "<hr>" +
      "<div style='display:block'>" +
      " <form action='/DepCon/import' method='post' enctype='multipart/form-data'>" +
      "  <label for='app_name'>Application name (<i>select existing name from dropdown list</i>): </label>" +
      "  <input type='text' list='apps' id='app_name' name='app_name' placeholder='application name ...'>" + 
      "  <datalist id='apps'>";
  

  private static final String PAGE_BOTTOM_PART =
      "</datalist>" +
      "<br>" +
      "<label for='app_version'>Application version: </label>" +
      "<input type='text' id='app_version' name='app_version' placeholder='application version ...'>" +
      "<br>" +
      "<label for='dep_file'>Select a dependency list file: </label>" +
      "<input type='file' id='dep_file' name='dep_file' accept='.txt'/>" +
      "<input type='submit' id='buttonUpload' value='Upload'/>" +
      "<br>" +
      "</form> </div> </body> </html>";
  
  
  public static String render() {
    
    StringBuilder sb = new StringBuilder();
    sb.append(PAGE_TOP_PART);

    List<String> appNames = new ApplicationDao().getAllApplicationNames();
    Collections.sort(appNames);
    
    for (String name : appNames) {
      sb.append(String.format("  <option value='%s'>\n", name));
    }
    sb.append(PAGE_BOTTOM_PART);
    
    return sb.toString();
  }

}
