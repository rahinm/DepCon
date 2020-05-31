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

import net.dollmar.svc.depcon.dao.ApplicationDao;
import net.dollmar.svc.depcon.entity.Application;

public class RemoveAppPage {

  private static final String PAGE_TOP_PART = 
      "<!DOCTYPE html> <html lang='en'>" + 
      "<head> <meta charset='UTF-8'> <title>DepCon - Utility to remove an application</title> <link rel='stylesheet' type='text/css' href='StyleSheets/DepCon.css' /> </head>" +
      "<body>" +
      "<div style='display:block'>";
  
  
  public static String render(long rowId) {
    Application app = new ApplicationDao().getApplication(rowId);
    if (app == null) {
      return String.format("Error: Application with id=%d does not not exist", rowId);
    }
    StringBuilder sb = new StringBuilder();
    sb.append(PAGE_TOP_PART);
    
    sb.append(String.format("<form action='/DepCon/apps/%d'>", rowId));
    sb.append("<input type='hidden' name='removeApp' value='true' />");
    sb.append(String.format("<label for='removeApp'>OK to remove application [%s, Version: %s] ?  </label>", app.getName(), app.getVersion())); 
    
    sb.append("<button type='submit' id='removeApp'>Remove</button>");
    sb.append("</form> </div> </body> </html>");
    
    return sb.toString();
  }
  
}
