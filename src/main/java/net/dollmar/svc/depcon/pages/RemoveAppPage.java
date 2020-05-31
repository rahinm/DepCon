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
