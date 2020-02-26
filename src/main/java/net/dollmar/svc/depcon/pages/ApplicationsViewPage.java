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

import java.util.Collection;
import java.util.List;
import java.util.Map;

import net.dollmar.svc.depcon.Main;
import net.dollmar.svc.depcon.dao.ApplicationDao;
import net.dollmar.svc.depcon.entity.Application;
import net.dollmar.svc.depcon.utils.Utils;

public class ApplicationsViewPage {


	public String render(Map<String, String[]> qm) {
		ApplicationDao appDao = new ApplicationDao();

		return buildHtmlTableForLibs("List of registered applications", appDao.getAllApplications());
	}


	@SuppressWarnings("unchecked")
	private String buildHtmlTableForLibs(final String title, final Collection<Application> apps) {

		StringBuilder sb = new StringBuilder();
		sb.append(String.format("<h3>Number of registered applications = %d</h3>", apps.size()));

		sb.append("<table border='1'>");
		sb.append("<tr><th>Id</th>");
		sb.append("<th>Application Name</th>");
		sb.append("<th>Version</th></tr>");

		if (apps != null) {
			// sort the collection for better presentation
			List<Application> appList = Utils.sort(apps);
			for (Application app : appList) {
				String aid = "" + app.getId();
				sb.append("<tr><td>").append(createLinkForPopup(aid)).append("</td>");
				sb.append("<td>").append(app.getName()).append("</td>");
				sb.append("<td>").append(app.getVersion()).append("</td></tr>");
			}
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

}
