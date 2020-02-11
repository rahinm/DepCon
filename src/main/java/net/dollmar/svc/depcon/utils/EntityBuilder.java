package net.dollmar.svc.depcon.utils;

import java.util.Objects;

import net.dollmar.svc.depcon.entity.Application;
import net.dollmar.svc.depcon.entity.Artifact;

public class EntityBuilder {

	public static Artifact buildArtifact(String[] ad) {
		Objects.requireNonNull(ad);
		
		Artifact art = new Artifact();
		art.setGroupId(ad[0]);
		art.setArtifactId(ad[1]);
		art.setPackaging(ad[2]);
		art.setVersion(ad[3]);
		art.setScope(ad[4]);
		
		return art;
	}
	
	
	public static Application buildApplication(String name, String version) {
		Application app = new Application();
		app.setName(name);
		app.setVersion(version);
		
		return app;
	}

}
