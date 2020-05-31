/*
Copyright 2017 Mohammad A. Rahin                                                                                                          

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

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import net.dollmar.svc.depcon.Main;
import net.dollmar.svc.depcon.dao.ApplicationDao;
import net.dollmar.svc.depcon.entity.Application;
import net.dollmar.svc.depcon.utils.DepConException;
import net.dollmar.svc.depcon.utils.Utils;

public class ApplicationsViewPage {


  public String render(Map<String, String[]> qm) {
    ApplicationDao appDao = new ApplicationDao();

    if (qm == null || qm.isEmpty()) {
      return buildHtmlTableForLibs("All dependent libraries", appDao.getAllApplications());
    }

    boolean toRemove = false;
    long rowId = -1;

    Set<Entry<String, String[]>> entries = qm.entrySet();
    for(Entry<String, String[]> entry : entries) {
      switch (entry.getKey()) {
      case "action":
        switch (entry.getValue()[0]) {
        case "remove":
          toRemove = true;
          break;
        case "list":
          toRemove = false;
        default:
          return Utils.buildStyledHtmlPage("Error", "Invalid 'action' parameter value: " + entry.getValue()[0]);
        }
        break;
      case "appId":
        rowId = Long.parseLong(entry.getValue()[0]);
        break;
      default:
        return Utils.buildStyledHtmlPage("Error", "Invalid query parameter: " + entry.getKey());
      }
    }
    if (toRemove) {
      return Utils.buildStyledHtmlPage("Remove Application", RemoveAppPage.render(rowId));
    }
    else {
      return render(rowId, null);
    }
  }



  public String render(long rowId, Map<String, String[]> qm) {
    if (qm == null || qm.isEmpty()) {
      HashMap<String, String[]> args = new HashMap<>();
      args.put("appId", (String[])Arrays.asList(Long.toString(rowId)).toArray());

      return new LibsViewPage().render(args);
    }
    boolean toRemove = false;
    Set<Entry<String, String[]>> entries = qm.entrySet();
    for(Entry<String, String[]> entry : entries) {
      switch (entry.getKey()) {
      case "removeApp":
        toRemove = Boolean.parseBoolean(entry.getValue()[0]);
        break;
      default:
        return Utils.buildStyledHtmlPage("Error", "Invalid query parameter: " + entry.getKey());
      }
    }
    if (!toRemove) {
      return Utils.buildStyledHtmlPage("Cancelled", "Remove application cancelled.");
    }
    else {
      ApplicationDao appDao = new ApplicationDao();
      Application app = appDao.getApplication(rowId);
      if (app == null) {
        return Utils.buildStyledHtmlPage("Error", String.format("Application with id=%d does not exist.", rowId));
      }
      try {
        appDao.deleteApplication(rowId);
        
        return Utils.buildStyledHtmlPage("Success", 
            String.format("Application [%s, Version: %s] removed. Please refresh Applications View page.", app.getName(), app.getVersion()));
      }
      catch (DepConException dce) {
        return "Error: " + dce.getMessage();
      }      
    }
  }


  private String buildHtmlTableForLibs(final String title, final Collection<Application> apps) {

    StringBuilder sb = new StringBuilder();
    sb.append(String.format("<h3>Number of registered applications = %d</h3>", apps.size()));

    sb.append("<input type='text' id='appName' name='search_input' onkeyup='searchApplication()' placeholder='Search for applications ...'>");

    sb.append("<table id ='appsTable' class='sortable' border='1'>");
    sb.append("<thead><tr><th>Id</th>");
    sb.append("<th>Application Name</th>");
    sb.append("<th>Version</th>");
    sb.append("<th>Remove</th></tr></thead>");

    if (apps != null) {
      // sort the collection for better presentation
      List<Application> appList = Utils.sort(apps);
      sb.append("<tbody>");
      for (Application app : appList) {
        String aid = "" + app.getId();
        sb.append("<tr><td>").append(createLinkForPopup(aid)).append("</td>");
        sb.append("<td>").append(app.getName()).append("</td>");
        sb.append("<td>").append(app.getVersion()).append("</td>");
        sb.append("<td>").append(createLinkForRemoval(aid)).append("</td>");
      }
      sb.append("</tbody>");
    }

    sb.append("</table>");
    sb.append("<br>");

    return Utils.buildStyledHtmlPage(title, sb.toString());
  }


  private String createLinkForPopup(String rowId) {
    String url = String.format("%s?appId=%s", Main.LIBS_PATH, rowId);

    StringBuilder sb = new StringBuilder();
    sb.append(String.format("<a href=\"%s\" ", url)).append("target=\"popup\" ");
    sb.append(
        String.format("onclick=\"window.open('%s', 'popup', 'width=1000, height=600'); return false;\">", url));
    sb.append(rowId).append("</a>");
    return sb.toString();
  }

  private String createLinkForRemoval(String rowId) {
    String url = String.format("%s?appId=%s&action=remove", Main.APPS_PATH, rowId);

    StringBuilder sb = new StringBuilder();
    sb.append(String.format("<a href=\"%s\" ", url)).append("target=\"popup\" ");
    sb.append(
        String.format("onclick=\"window.open('%s', 'popup', 'width=1000, height=600'); return false;\">", url));
    sb.append("Remove").append("</a>");
    return sb.toString();
  }

}
