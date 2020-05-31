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

package net.dollmar.svc.depcon.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.servlet.http.Part;

import spark.utils.IOUtils;

public class Utils {

	
	public static boolean isEmptyString(final String s) {
		return s == null || s.isEmpty();
	}
	
	public static <T extends Comparable<? super T>> List<T> sort(Collection<T> coll) {
		List<T> sorted = new ArrayList<>(coll);
		Collections.sort(sorted);
		
		return sorted;
	}
	
	public static String partToString(Part part) {
		try (InputStream inputStream = part.getInputStream()) {
			return IOUtils.toString(inputStream);
		}
		catch (IOException e) {
			return "";
		}
	}	
	
	public static String buildStyledHtmlPage(final String header, final String content) {
		StringBuilder sb = new StringBuilder();

		sb.append("<html><head>");
		sb.append("<script src='js/sorttable.js' type='text/javascript'></script>");
		sb.append("<script src='js/search.js' type='text/javascript'></script>");
		sb.append("<link rel=\"stylesheet\" type=\"text/css\" href=\"/DepCon/StyleSheets/DepCon.css\"/>");
		sb.append("</head>");
		sb.append("<body bgcolor=\"#b8d6ca\">");
		sb.append(String.format("<h2>%s</h2>", header));
		sb.append(content);
		sb.append("</body>");
		sb.append("</html>");

		return sb.toString();
	}	
}
