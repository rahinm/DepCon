package net.dollmar.svc.depcon;


import static spark.Spark.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import javax.servlet.MultipartConfigElement;
import javax.servlet.http.Part;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.dollmar.svc.depcon.data.Dependencies;
import net.dollmar.svc.depcon.filters.BasicAuthenticationFilter;
import net.dollmar.svc.depcon.filters.Filters;
import net.dollmar.svc.depcon.pages.ApplicationsViewPage;
import net.dollmar.svc.depcon.pages.LibsViewPage;
import net.dollmar.svc.depcon.utils.Utils;
import spark.utils.IOUtils;


public class Main {

	public static final int DEFAULT_PORT = 10080;
	public static final String SVC_NAME = "DepCon";
	public static final String SVC_VERSION = "v1";

    public static final String HOME_PATH = String.format("/%s/%s", Main.SVC_NAME, "home");
    public static final String LIBS_PATH = String.format("/%s/%s", Main.SVC_NAME, "libs");
    public static final String APPS_PATH = String.format("/%s/%s", Main.SVC_NAME, "apps");
    public static final String IMPORT_PATH = String.format("/%s/%s", Main.SVC_NAME, "import");
    
    private static final String TMP_DIR = "data/tmp";
	
	private static Logger logger = LoggerFactory.getLogger(Main.class);

	
	public static void main(String[] args) {
		int serverPort = DEFAULT_PORT;

		if (args.length > 0) {
			if ("-h".equals(args[0]) || "--help".equals(args[0])) {
				System.out.println("Usage: java -jar DepLister-<version>.jar [listening-port]");
				return;
			}
			else {
				serverPort = Integer.parseInt(args[0]);
			}
		}
		
		port(serverPort);

		// root location for static pages (e.g. API Docs)
		staticFiles.location("/static");
		
        logger.info(String.format("Starting server on port: %d", serverPort ));
		logger.info("Available end-points:");
		
        
		// routes
		
		before(new BasicAuthenticationFilter("*"));
		
//		get(HOME_PATH, (req, resp) -> {
//			resp.type("application/html");
//			resp.status(200);
//			return "<h2>Hello from Home</h2>";
//		});		

		get(LIBS_PATH, (req, resp) -> {
			resp.status(200);
			return new LibsViewPage().render(req.queryMap().toMap());
		});		

		
		get(LIBS_PATH + "/:rowId", (req, resp) -> {
			resp.status(200);
			return new LibsViewPage().render(Long.parseLong(req.params(":rowId")));
		});		

		
		get(APPS_PATH, (req, resp) -> {
			resp.status(200);
			return new ApplicationsViewPage().render(req.queryMap().toMap());
		});	
		
		
        post(IMPORT_PATH, (req, resp) -> {
            req.attribute("org.eclipse.jetty.multipartConfig", new MultipartConfigElement(TMP_DIR));
            
            Part appNamePart = req.raw().getPart("app_name");
            Part appVersionPart = req.raw().getPart("app_version");
            Part depFilePart = req.raw().getPart("dep_file");

            String appName = Utils.partToString(appNamePart);
        	String appVersion = Utils.partToString(appVersionPart);
            
        	if (Utils.isEmptyString(appName) 
        			|| Utils.isEmptyString(appVersion) 
        			|| Utils.isEmptyString(depFilePart.getSubmittedFileName())) {
        		resp.status(400);
        		return Utils.buildStyledHtmlPage("Error", "Missing input parameter");
        	}
        	File tmpDir = new File(TMP_DIR);
        	if (!tmpDir.exists()) {
                tmpDir.mkdirs();
            }
        	File importFile = new File(TMP_DIR, depFilePart.getSubmittedFileName());
            try (InputStream inputStream = depFilePart.getInputStream()) {
                OutputStream outputStream = new FileOutputStream(importFile);
                IOUtils.copy(inputStream, outputStream);
                outputStream.close();
            }
            
            try {
            	Dependencies d = new Dependencies(appName, appVersion);
            	d.persist(d.importFile(importFile.getAbsolutePath()));
            }
            catch (Exception e) {
            	resp.status(500);
        		return Utils.buildStyledHtmlPage("Error", e.getMessage());
            }
            return Utils.buildStyledHtmlPage("Success", "File successfully uploaded and imported into database");
        });
		
        //Set up after-filters (called after each get/post)
        after("*", Filters.addGzipHeader);		
	}
}
