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

package net.dollmar.svc.depcon.entity;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Table(name = "ARTIFACTS")
public class Artifact implements java.io.Serializable, Comparable<Artifact> {
	
	private static final long serialVersionUID = 1495331023666978971L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "art_generator")
	@SequenceGenerator(name="art_generator", sequenceName = "art_seq", initialValue = 1, allocationSize = 20)
	@Column(name = "ID", updatable = false, nullable = false)
	private Long id;
	
	@Column(name = "ARTIFACT_ID", unique = false)
	private String artifactId;
	
	@Column(name = "GROUP_ID", unique = false)
	private String groupId;
	
	@Column(name = "VERSION", unique = false)
	private String version;

	@Column(name = "PACKAGING", unique = false)
	private String packaging;

	@Column(name = "SCOPE", unique = false)
	private String scope;
	
  @ManyToMany(mappedBy = "artifacts")
	Set<Application> usedByApps = new HashSet<>();
	
	
	public long getId() {
		return id;
	}
	
	public void setId(long id) {
		this.id = id;
	}
	

	public String getArtifactId() {
		return artifactId;
	}
	
	public void setArtifactId(String artifactId) {
		this.artifactId = artifactId;
	}
	
	public String getGroupId() {
		return groupId;
	}
	
	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}
	
	public String getVersion() {
		return version;
	}
	
	public void setVersion(String version) {
		this.version = version;
	}
		
	public String getPackaging() {
		return packaging;
	}
	
	public void setPackaging(String packaging) {
		this.packaging = packaging;
	}
	
	public String getScope() {
		return scope;
	}
	
	public void setScope(String scope) {
		this.scope = scope;
	}
	
	
	public Set<Application> getUsedByApps() {
		return usedByApps;
	}

	public void setUsedByApps(Set<Application> usedByApps) {
		this.usedByApps = usedByApps;
	}

	public void addApplication(Application app) {
		usedByApps.add(app);
		app.getArtifacts().add(this);
	}
	
	
//	public void removeApplication(Application app) {
//		usedByApps.remove(app);
//		app.getArtifacts().remove(this);
//	}
	
    @Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((artifactId == null) ? 0 : artifactId.hashCode());
		result = prime * result + ((groupId == null) ? 0 : groupId.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((packaging == null) ? 0 : packaging.hashCode());
		result = prime * result + ((scope == null) ? 0 : scope.hashCode());
		result = prime * result + ((version == null) ? 0 : version.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Artifact other = (Artifact) obj;
		if (artifactId == null) {
			if (other.artifactId != null)
				return false;
		} else if (!artifactId.equals(other.artifactId))
			return false;
		if (groupId == null) {
			if (other.groupId != null)
				return false;
		} else if (!groupId.equals(other.groupId))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (packaging == null) {
			if (other.packaging != null)
				return false;
		} else if (!packaging.equals(other.packaging))
			return false;
		if (scope == null) {
			if (other.scope != null)
				return false;
		} else if (!scope.equals(other.scope))
			return false;
		if (version == null) {
			if (other.version != null)
				return false;
		} else if (!version.equals(other.version))
			return false;
		return true;
	}
 
	public String toString() {
		return String.format("%s | %s | %s | %s | %s" , groupId, artifactId, version, packaging, scope);
	}

	@Override
	public int compareTo(Artifact o) {
		// TODO Auto-generated method stub
		return artifactId.compareTo(((Artifact) o).artifactId);
	}
}
