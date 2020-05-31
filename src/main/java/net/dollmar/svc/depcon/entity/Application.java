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

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Table(name = "APPLICATIONS")
public class Application implements java.io.Serializable, Comparable<Application> {

	private static final long serialVersionUID = 5279242006099780076L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "app_generator")
	@SequenceGenerator(name="app_generator", sequenceName = "app_seq", initialValue = 1, allocationSize = 20)
	@Column(name = "ID", updatable = false, nullable = false)	
	private Long id;

	@Column(name = "NAME", unique = false)
	private String name;

	@Column(name = "VERSION", unique = false)
	private String version;

  @ManyToMany(cascade = {
      CascadeType.PERSIST,
      CascadeType.MERGE
  })
  @JoinTable(
      name = "used_by",
      joinColumns = @JoinColumn(name = "app_id"),
      inverseJoinColumns = @JoinColumn(name = "art_id"))
	Set<Artifact> artifacts = new HashSet<>();


	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}


	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public Set<Artifact> getArtifacts() {
		return artifacts;
	}

	public void setArtifacts(Set<Artifact> artifacts) {
		this.artifacts = artifacts;
	}

  public void addArtifact(Artifact art) {
    artifacts.add(art);
    art.getUsedByApps().add(this);
  }
  
  
//	public void removeArtifact(Artifact art) {
//	  artifacts.remove(art);
//	  art.getUsedByApps().remove(this);
//	}
//
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
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
		Application other = (Application) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (version == null) {
			if (other.version != null)
				return false;
		} else if (!version.equals(other.version))
			return false;
		return true;
	}

	
	public String toString() {
		return String.format("%s [Version: %s]", name, version);
	}
	
	@Override
	public int compareTo(Application o) {
		return name.compareTo(((Application) o).name);
	}
}
