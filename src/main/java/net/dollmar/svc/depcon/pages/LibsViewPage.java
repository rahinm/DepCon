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

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import net.dollmar.svc.depcon.Main;
import net.dollmar.svc.depcon.dao.ApplicationDao;
import net.dollmar.svc.depcon.dao.ArtifactDao;
import net.dollmar.svc.depcon.data.Subject;
import net.dollmar.svc.depcon.data.UserContext;
import net.dollmar.svc.depcon.entity.Application;
import net.dollmar.svc.depcon.entity.Artifact;
import net.dollmar.svc.depcon.utils.Utils;

public class LibsViewPage {


	public String render(Map<String, String[]> qm) {
	  Subject sub = UserContext.getConext().getSubject();
	  
		ArtifactDao artDao = new ArtifactDao();

		if (qm == null || qm.isEmpty()) {
			return buildHtmlTableForLibs("List of dependent libraries", artDao.getAllArtifacts());
		}
		String scopes[];
		Application app;
		Set<Entry<String, String[]>> entries = qm.entrySet();
		for(Entry<String, String[]> entry : entries) {
			switch (entry.getKey()) {
			case "scopes":
				scopes = entry.getValue()[0].split(",");
				break;
			case "appId":
				ApplicationDao appDao = new ApplicationDao(); 
				app = appDao.getApplication(Long.parseLong(entry.getValue()[0]));
				return buildHtmlTableForLibs(String.format("Dependent libraries for application: %s", app.toString()), 
						app.getArtifacts());
			}
		}
		return buildHtmlTableForLibs("Dependent Library Details", artDao.getAllArtifacts());
	}

	public String render(long rowId) {
		ArtifactDao artDao = new ArtifactDao();

		return buildHtmlForSingleLib(artDao.getArtifact(rowId));
	}


	private String buildHtmlTableForLibs(final String title, final Collection<Artifact> libs) {

		StringBuilder sb = new StringBuilder();
		sb.append(String.format("<h3>Number of dependent libraries = %d</h3>", libs.size()));
		
		sb.append("<input type='text' id='libraryName' name='search_input' onkeyup='searchLibrary()' placeholder='Search for artifacts/libraries ...'>");
		
		sb.append("<table id ='libsTable' class='sortable' border='1'>");
		sb.append("<thead><tr>");
		sb.append("<th>Id</th>");
		sb.append("<th>Artifact Id</th>");
		sb.append("<th>Group Id</th>");
		sb.append("<th>Version</th>");
		sb.append("<th>Packaging</th>");
		sb.append("<th>Scope</th>");
		sb.append("</tr></thead>");

		if (libs != null) {
			// sort the collection for better presentation
			List<Artifact> libsList = Utils.sort(libs);
			sb.append("<tbody>");
			for (Artifact lib : libsList) {
				String aid = "" + lib.getId();
				sb.append("<tr>");
				sb.append("<td>").append(createLinkForPopup(aid)).append("</td>");
				sb.append("<td>").append(lib.getArtifactId()).append("</td>");
				sb.append("<td>").append(lib.getGroupId()).append("</td>");
				sb.append("<td>").append(lib.getVersion()).append("</td>");
				sb.append("<td>").append(lib.getPackaging()).append("</td>");
				sb.append("<td>").append(lib.getScope()).append("</td>");
				sb.append("</tr>");
			}
			sb.append("</tbody>");
		}

		sb.append("</table>");
		sb.append("<br>");

		return Utils.buildStyledHtmlPage(title, sb.toString());
	}


	private String createLinkForPopup(String rowId) {
		String url = String.format("%s/%s", Main.LIBS_PATH, rowId);

		StringBuilder sb = new StringBuilder();
		sb.append(String.format("<a href=\"%s\" ", url)).append("target=\"popup\" ");
		sb.append(
				String.format("onclick=\"window.open('%s', 'popup', 'width=1000, height=500'); return false;\">", url));
		sb.append(rowId).append("</a>");
		return sb.toString();
	}




	private String buildHtmlForSingleLib(Artifact lib) {
		StringBuilder sb = new StringBuilder();
		String pageHeader;

		if (lib == null) {
			pageHeader = "Error";
			sb.append(String.format("Library object is null"));
		}
		else {
			pageHeader = String.format("Details for library: '%s'", lib.getArtifactId());
			
			sb.append("<table border='1'>");
			sb.append("<thead><tr><th>Field</th>");
			sb.append("<th>Value</th></tr></thead>");

			sb.append("<tbody>");
			sb.append("<tr><td>Id</td><td>" + lib.getId() + "</td></tr>");
			sb.append("<tr><td>Artifact Id</td><td>" + lib.getArtifactId() + "</td></tr>");
			sb.append("<tr><td>Group Id</td><td>" + lib.getGroupId() + "</td></tr>");
			sb.append("<tr><td>Version</td><td>" + lib.getVersion() + "</td></tr>");
			sb.append("<tr><td>Packaging</td><td>" + lib.getPackaging() + "</td></tr>");
			sb.append("<tr><td>Scope </td><td>" + lib.getScope() + "</td></tr>");
			sb.append("</tbody>");
			sb.append("</table>");
			if (lib.getUsedByApps() != null || lib.getUsedByApps().size() != 0) {
				sb.append("<h2>Used in Applications: </h2>");
				sb.append("<table border='1'>");
				sb.append("<thead><tr><th>Id</th>");
				sb.append("<th>Application Name</th>");
				sb.append("<th>Version</th></tr></thead>");

				sb.append("<tbody>");
				for (Application app: lib.getUsedByApps()) {
					sb.append("<tr><td>").append(app.getId()).append("</td>");
					sb.append("<td>").append(app.getName()).append("</td>");
					sb.append("<td>").append(app.getVersion()).append("</td></tr>");
				}
				sb.append("</tbody>");
				sb.append("</table>");
			}
		}
		sb.append("<br>");
		return Utils.buildStyledHtmlPage(pageHeader, sb.toString());
	}
}
