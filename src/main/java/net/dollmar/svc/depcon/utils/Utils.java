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
