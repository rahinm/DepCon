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
