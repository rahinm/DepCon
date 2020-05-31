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

package net.dollmar.svc.depcon;


import static spark.Spark.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.Security;
import java.util.Properties;

import javax.servlet.MultipartConfigElement;
import javax.servlet.http.Part;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.dollmar.svc.depcon.dao.ApplicationDao;
import net.dollmar.svc.depcon.data.Dependencies;
import net.dollmar.svc.depcon.filters.BasicAuthenticationFilter;
import net.dollmar.svc.depcon.filters.Filters;
import net.dollmar.svc.depcon.pages.ApplicationsViewPage;
import net.dollmar.svc.depcon.pages.HomePage;
import net.dollmar.svc.depcon.pages.LibsViewPage;
import net.dollmar.svc.depcon.pages.UploadPage;
import net.dollmar.svc.depcon.utils.DepConException;
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
  public static final String UPLOAD_PATH = String.format("/%s/%s", Main.SVC_NAME, "upload");

	private static final String TMP_DIR = "data/tmp";
	private static final String CFG_FILE = "config/DepCon.properties";

	private static Logger logger = LoggerFactory.getLogger(Main.class);


	private static void configure() {
		File configDir = new File(CFG_FILE).getParentFile();
		if (!configDir.exists()) {
			configDir.mkdirs();
		}		
		Properties props = new Properties();
		try (InputStream input = new FileInputStream(CFG_FILE)) {
			props.load(input);
			for (String p : props.stringPropertyNames()) {
				System.setProperty(p, props.getProperty(p));
			}
		}
		catch (IOException e) {
			System.err.println(String.format("WARN: Failed to load configuration data [Reason: %s]", e.getMessage()));
		}		
	}

	public static void main(String[] args) {
		configure();
		String hostName = "localhost";
		try {
			hostName = InetAddress.getLocalHost().getCanonicalHostName();
		}
		catch (UnknownHostException e) {
			// DO nothing
		}
		String scheme = "http";
		int serverPort = Integer.getInteger("depcon.listener.port", DEFAULT_PORT);
		port(serverPort);
		if (Boolean.getBoolean("depcon.network.security")) {
			// disable insecure algorithms
			Security.setProperty("jdk.tls.disabledAlgorithms",
					"SSLv3, TLSv1, TLSv1.1, RC4, MD5withRSA, DH keySize < 1024, EC keySize < 224, DES40_CBC, RC4_40, 3DES_EDE_CBC");
			
			String keyStoreName = System.getProperty("depcon.keystore.filename");
			String keyStorePassword = System.getProperty("depcon.keystore.password");

			if (Utils.isEmptyString(keyStoreName) || Utils.isEmptyString(keyStorePassword)) {
				System.err.println("ERROR: Keystore name or password is not set.");
				return;
			}
			scheme = "https";
			secure(keyStoreName, keyStorePassword, null, null);
		}

		// root location for static pages (e.g. API Docs)
		staticFiles.location("/static");

		logger.info(String.format("Starting server on port: %d", serverPort ));
		logger.info("Application available at ==> " 
				+ String.format("%s://%s:%d/DepCon/index.html", 
						scheme,
						hostName,
						serverPort));

		// routes

		before(new BasicAuthenticationFilter("*"));

    get(HOME_PATH, (req, resp) -> {
      resp.status(200);
      return HomePage.render();
    });

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


    get(APPS_PATH + "/:rowId", (req, resp) -> {
      resp.status(200);
      return new ApplicationsViewPage().render(Long.parseLong(req.params(":rowId")), req.queryMap().toMap());
    });   

    
    delete(APPS_PATH + "/:rowId", (req, resp) -> {
		  long id = Long.parseLong(req.params(":rowId"));
		  ApplicationDao appDao = new ApplicationDao();
		  try {
		    appDao.deleteApplication(id);
		    resp.status(204);
		    
		    return String.format("Application [id=%d] successfully deleted", id);
		  }
		  catch (DepConException dce) {
		    resp.status(dce.getCode());
	      return "Error: " + dce.getMessage();
		  }
		});
		
		get(UPLOAD_PATH, (req, resp) -> {
		  resp.status(200);
		  return UploadPage.render();
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
