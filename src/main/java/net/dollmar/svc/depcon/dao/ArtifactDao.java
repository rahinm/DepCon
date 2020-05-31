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

package net.dollmar.svc.depcon.dao;

import java.util.List;
import java.util.Objects;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import net.dollmar.svc.depcon.entity.Application;
import net.dollmar.svc.depcon.entity.Artifact;
import net.dollmar.svc.depcon.utils.DepConException;
import net.dollmar.svc.depcon.utils.EntityManagerUtil;


public class ArtifactDao {

	
	private void close(EntityManager em) {
		if (em != null) {
			em.close();
		}
	}
	
	public boolean testDatabase() {
		try {
			getAllArtifacts("test");
			return true;
		}
		catch (Exception e) {
			return false;
		}
	}
	
	
	public List<Artifact> getAllArtifacts() {
		EntityManager em = EntityManagerUtil.getEntityManager();
		try {
			em.getTransaction().begin();
			@SuppressWarnings("unchecked")
			List<Artifact> keys = em.createQuery("SELECT a FROM Artifact a").getResultList();
			em.getTransaction().commit();
			
			return keys;
		}
		catch (Exception e) {
			em.getTransaction().rollback();
			throw new DepConException(500, "Failed to retrieve artifacts from database.", e);
		}
		finally {
			close(em);
		}
	}	
	

	public List<Artifact> getAllArtifacts(final String scope) {
		String queryString = "SELECT a FROM Artifact a WHERE a.scope = :scope";
		EntityManager em = EntityManagerUtil.getEntityManager();
		try {
			em.getTransaction().begin();
			TypedQuery<Artifact> query = em.createQuery(queryString, Artifact.class);
			query.setParameter("scope", scope);
			
			List<Artifact> keys = query.getResultList();
			em.getTransaction().commit();
			
			return keys;
		}
		catch (Exception e) {
			em.getTransaction().rollback();
			throw new DepConException(500, "Failed to retrieve artifacts from database.", e);
		}
		finally {
			close(em);
		}
	}
	
	
	
	public Artifact getArtifact(long rowId) {
		String queryString = "SELECT a FROM Artifact a LEFT JOIN FETCH a.usedByApps u WHERE a.id = :rowId";
		EntityManager em = EntityManagerUtil.getEntityManager();
		try {
			em.getTransaction().begin();
			TypedQuery<Artifact> query = em.createQuery(queryString, Artifact.class);
			query.setParameter("rowId", rowId);

			List<Artifact> result = query.getResultList();
			em.getTransaction().commit();
			
			return (result == null || result.size() == 0) ? null : result.get(0);
		}
		catch (DepConException se) {
			throw se;
		}
		catch (Exception e) {
			em.getTransaction().rollback();
			throw new DepConException(500, String.format("Failed to retrieve artifact '%d' from database.", rowId), e);
		}
		finally {
			close(em);
		}
	}
	
	
	public Artifact getArtifact(String groupId, String artifactId, String version) {
		String queryString = "SELECT a FROM Artifact a LEFT JOIN FETCH a.usedByApps u WHERE a.artifactId = :artifactId AND a.groupId = :groupId AND a.version = :version";
		EntityManager em = EntityManagerUtil.getEntityManager();
		try {
			em.getTransaction().begin();
			TypedQuery<Artifact> query = em.createQuery(queryString, Artifact.class);
			query.setParameter("artifactId", artifactId);
			query.setParameter("groupId", groupId);
			query.setParameter("version", version);
			
			List<Artifact> result = query.getResultList();
			em.getTransaction().commit();
			
			return (result == null || result.size() == 0) ? null : result.get(0);
		}
		catch (DepConException se) {
			throw se;
		}
		catch (Exception e) {
			em.getTransaction().rollback();
			throw new DepConException(500, String.format("Failed to retrieve artifact '%s:%s:%s' from database.", 
					groupId, artifactId, version), e);
		}
		finally {
			close(em);
		}
	}
	
	
  public Artifact getArtifact(final Artifact art) {
    Objects.requireNonNull(art);
    
    String queryString = "SELECT a FROM Artifact a LEFT JOIN FETCH a.usedByApps u WHERE a.artifactId = :artifactId AND a.groupId = :groupId AND a.version = :version AND a.scope = :scope";
    EntityManager em = EntityManagerUtil.getEntityManager();
    try {
      em.getTransaction().begin();
      TypedQuery<Artifact> query = em.createQuery(queryString, Artifact.class);
      query.setParameter("artifactId", art.getArtifactId());
      query.setParameter("groupId", art.getGroupId());
      query.setParameter("version", art.getVersion());
      query.setParameter("scope", art.getScope());
      
      List<Artifact> result = query.getResultList();
      em.getTransaction().commit();
      
      return (result == null || result.size() == 0) ? null : result.get(0);
    }
    catch (DepConException se) {
      throw se;
    }
    catch (Exception e) {
      em.getTransaction().rollback();
      throw new DepConException(500, String.format("Failed to retrieve artifact '%s:%s:%s:%s' from database.", 
          art.getGroupId(), art.getArtifactId(), art.getVersion(), art.getScope()), e);
    }
    finally {
      close(em);
    }
  }

  
  public Artifact saveArtifact(Artifact art) {
		Objects.requireNonNull(art);
		
		Artifact existing = getArtifact(art.getGroupId(), art.getArtifactId(), art.getVersion());
		if (existing != null) {
			throw new DepConException(409, String.format("Artifact '%s:%s:%s' already exists", 
					art.getGroupId(), art.getArtifactId(), art.getVersion()));
		}
		
		EntityManager em = EntityManagerUtil.getEntityManager();
		try {
			em.getTransaction().begin();
			Artifact na = em.merge(art);
			em.getTransaction().commit();
			
			return na;
		}
		catch (Exception e) {
			em.getTransaction().rollback();
			throw new DepConException(500, String.format("Failed to persist artifact '%s:%s:%s'.", 
					art.getGroupId(), art.getArtifactId(), art.getVersion()), e);
		}
		finally {
			close(em);
		}
	}	
	
	
	public Artifact saveOrUpdateArtifact(Artifact art, Application ...apps) {
		Objects.requireNonNull(art);
		
		Artifact targetArt = getArtifact(art.getGroupId(), art.getArtifactId(), art.getVersion());
		if (targetArt == null) {
			targetArt = art;
		}
		EntityManager em = EntityManagerUtil.getEntityManager();
		try {
			em.getTransaction().begin();
			for (Application app: apps) {
				targetArt.addApplication(app);
			}
			Artifact ns = em.merge(targetArt);
			em.getTransaction().commit();
			
			return ns;
		}
		catch (Exception e) {
			em.getTransaction().rollback();
			throw new DepConException(500, String.format("Failed to persist artifact '%s:%s:%s'.", 
					art.getGroupId(), art.getArtifactId(), art.getVersion()), e);
		}
		finally {
			close(em);
		}
	}
	
	
	public void deleteArtifact(long id) {
		EntityManager em = EntityManagerUtil.getEntityManager();
		try {
			em.getTransaction().begin();
			Artifact art = (Artifact) em.find(Artifact.class, id);
			em.remove(art);
			em.getTransaction().commit();
		}
		catch (Exception e) {
			em.getTransaction().rollback();
			throw new DepConException(500, String.format("Failed to delete artifact '%d' data from database.",  id), e);
		}
		finally {
			close(em);
		}
	}	
	
}
