package net.dollmar.svc.depcon.data;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import net.dollmar.svc.depcon.dao.ApplicationDao;
import net.dollmar.svc.depcon.dao.ArtifactDao;
import net.dollmar.svc.depcon.entity.Application;
import net.dollmar.svc.depcon.utils.DepConException;
import net.dollmar.svc.depcon.utils.EntityBuilder;


public class Dependencies {

	private final static String LINE_PREFIX = "[INFO]";
	
	private String prodName;
	private String prodVersion;
	
	List<String> ignoreFrom = Arrays.asList("biz.thelogicgroup", "com.barclaycard", "org.eclips1");
	
	public Dependencies(final String appName, final String appVersion) {
		this.prodName = appName;
		this.prodVersion = appVersion;
	}
	
	private boolean shouldIgnore(String groupId) {
		return ignoreFrom.stream().anyMatch(i -> groupId.startsWith(i));
	}
	
	
	public Collection<String[]> importFile(final String depFile) {
		Objects.requireNonNull(depFile);
		
		Collection<String[]> li = new ArrayList<>();
		try (Stream<String> stream = Files.lines(Paths.get(depFile))) {
			
			li = stream
				.map(String::trim)
				.filter(line -> line.startsWith(LINE_PREFIX))
				.map(line -> line.substring(line.indexOf(LINE_PREFIX) + LINE_PREFIX.length()).trim().split(":"))
				.filter(a -> (a.length == 5))
				.filter(a -> !shouldIgnore(a[0]))
				.sorted(Comparator.comparing(a -> a[1]))
				.collect(Collectors.toList());
			
			return li;
		}
		catch (IOException e) {
			throw new DepConException("Error: Failed to import dependencies", e);
		}
	}

//	private Collection<String[]> importFile2(final String depFile) {
//		Objects.requireNonNull(depFile);
//		
//		File inp = new File(depFile);
//		
//		if (!inp.exists()) {
//			throw new DepConException("Error: Dependency file does not exist");
//		}
//		Collection<String[]> li = new ArrayList<>();
//		
//		try (BufferedReader br = new BufferedReader(new FileReader(inp))) {
//			String st;
//			while ((st = br.readLine()) != null) {
//				st = st.trim();
//				if (st.isEmpty() || !st.startsWith(LINE_PREFIX)) {
//					continue;
//				}
//				
//				String[] parts = st.substring(st.indexOf(LINE_PREFIX) + LINE_PREFIX.length()).trim().split(":");
//				if (parts.length != 5) {
//					continue;
//				}
//				if (!parts[0].startsWith("biz.thelogicgroup") && !parts[0].startsWith("com.barclaycard")) {
//					li.add(parts);
//				}
//			}
//			return li;
//		}
//		catch (Exception e) {
//			throw new DepConException("Error: Failed to import dependencies", e);
//		}
//	}
	
	private String formatDependency(String[] dep) {
		StringBuilder sb = new StringBuilder();
		sb.append(dep[1]).append(" ==> ");
		for(int i = 0; i < dep.length; i++) {
			sb.append(dep[i]);
			if (i != (dep.length -1)) {
				sb.append(" | ");
			}
		}
		return sb.toString();
	}
	
	
	@SuppressWarnings("unused")
	private void printDependencies(Collection<String[]> deps) {
		
		for(String[] dep : deps) {
			System.out.println(formatDependency(dep));
		}
	}
	
	@SuppressWarnings("unused")
	public void persist(Collection<String[]> deps) {
		ApplicationDao appDao = new ApplicationDao();
	
		if (appDao.getApplication(prodName, prodVersion) != null) {
			throw new DepConException(String.format("Dependencies for application '%s:%s' has already been imported", prodName, prodVersion));
		}
		Application app = appDao.saveApplication(EntityBuilder.buildApplication(prodName, prodVersion));
		
		ArtifactDao artDao = new ArtifactDao();

		for(String[] dep : deps) {
			artDao.saveOrUpdateArtifact(EntityBuilder.buildArtifact(dep), app);
		}
	}	
	
	
	public static void main(String[] args) {
        if (args.length != 3) {
        	System.out.println("Usage: Dependencies app-name app-version dep-file-name");
        }
        else {
        	Dependencies d = new Dependencies(args[0], args[1]);
        	
        	Collection<String[]> deps = d.importFile(args[2]);
        	d.printDependencies(deps);
        	//d.persist(deps);
        }
        System.exit(0);		
	}
	
}
