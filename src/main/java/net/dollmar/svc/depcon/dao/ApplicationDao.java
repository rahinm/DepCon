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

import java.util.Collection;
import java.util.List;
import java.util.Objects;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import net.dollmar.svc.depcon.Main;
import net.dollmar.svc.depcon.data.UserContext;
import net.dollmar.svc.depcon.entity.Application;
import net.dollmar.svc.depcon.entity.Artifact;
import net.dollmar.svc.depcon.utils.DepConException;
import net.dollmar.svc.depcon.utils.EntityManagerUtil;

public class ApplicationDao {

	
	private void close(EntityManager em) {
		if (em != null) {
			em.close();
		}
	}
	
	public boolean testDatabase() {
		try {
			getAllApplications("test");
			return true;
		}
		catch (Exception e) {
			return false;
		}
	}
	
	
	public List<Application> getAllApplications() {
		EntityManager em = EntityManagerUtil.getEntityManager();
		try {
			em.getTransaction().begin();
			@SuppressWarnings("unchecked")
			List<Application> keys = em.createQuery("SELECT a FROM Application a").getResultList();
			em.getTransaction().commit();
			
			return keys;
		}
		catch (Exception e) {
			em.getTransaction().rollback();
			throw new DepConException(500, "Failed to retrieve applications from database.", e);
		}
		finally {
			close(em);
		}
	}	
	

	 public List<String> getAllApplicationNames() {
	    EntityManager em = EntityManagerUtil.getEntityManager();
	    try {
	      em.getTransaction().begin();
	      @SuppressWarnings("unchecked")
	      List<String> names = em.createQuery("SELECT DISTINCT a.name FROM Application a").getResultList();
	      em.getTransaction().commit();
	      
	      return names;
	    }
	    catch (Exception e) {
	      em.getTransaction().rollback();
	      throw new DepConException(500, "Failed to retrieve application names from database.", e);
	    }
	    finally {
	      close(em);
	    }
	  } 

	
	public List<Application> getAllApplications(final String name) {
		String queryString = "SELECT a FROM Application a WHERE a.name = :name";
		EntityManager em = EntityManagerUtil.getEntityManager();
		try {
			em.getTransaction().begin();
			TypedQuery<Application> query = em.createQuery(queryString, Application.class);
			query.setParameter("name", name);
			
			List<Application> keys = query.getResultList();
			em.getTransaction().commit();
			
			return keys;
		}
		catch (Exception e) {
			em.getTransaction().rollback();
			throw new DepConException(500, "Failed to retrieve applications from database.", e);
		}
		finally {
			close(em);
		}
	}
	
	public Application getApplication(long rowId) {
		String queryString = "SELECT a FROM Application a LEFT JOIN FETCH a.artifacts l WHERE a.id = :rowId";
		EntityManager em = EntityManagerUtil.getEntityManager();
		try {
			em.getTransaction().begin();
			TypedQuery<Application> query = em.createQuery(queryString, Application.class);
			query.setParameter("rowId", rowId);
			
			List<Application> result = query.getResultList();
			em.getTransaction().commit();
			
			return (result == null || result.size() == 0) ? null : result.get(0);
		}
		catch (DepConException se) {
			throw se;
		}
		catch (Exception e) {
			em.getTransaction().rollback();
			throw new DepConException(500, String.format("Failed to retrieve application '%d' from database.", rowId), e);
		}
		finally {
			close(em);
		}
	}
	
	
	public Application getApplication(String name, String version) {
		String queryString = "SELECT a FROM Application a LEFT JOIN FETCH a.artifacts l WHERE a.name = :name AND a.version = :version";
		EntityManager em = EntityManagerUtil.getEntityManager();
		try {
			em.getTransaction().begin();
			TypedQuery<Application> query = em.createQuery(queryString, Application.class);
			query.setParameter("name", name);
			query.setParameter("version", version);
			
			List<Application> result = query.getResultList();
			em.getTransaction().commit();
			
			return (result == null || result.size() == 0) ? null : result.get(0);
		}
		catch (DepConException se) {
			throw se;
		}
		catch (Exception e) {
			em.getTransaction().rollback();
			throw new DepConException(500, String.format("Failed to retrieve application '%s:%s' from database.", 
					name, version), e);
		}
		finally {
			close(em);
		}
	}
	
	
	public Application saveApplication(Application app) {
		Objects.requireNonNull(app);
		
		Application existing = null;
		try {
			existing = getApplication(app.getName(), app.getVersion());
		}
		catch (DepConException e) {
			//
		}
		if (existing != null) {
			throw new DepConException(409, String.format("Application '%s:%s' already exists", 
					app.getName(), app.getVersion()));
		}
		
		EntityManager em = EntityManagerUtil.getEntityManager();
		try {
			em.getTransaction().begin();
			Application ns = em.merge(app);
			em.getTransaction().commit();
			
			return ns;
		}
		catch (Exception e) {
			em.getTransaction().rollback();
			throw new DepConException(500, String.format("Failed to persist application '%s:%s'.", 
					app.getName(), app.getVersion()), e);
		}
		finally {
			close(em);
		}
	}	
	
	
  public Application saveApplication(Application app, Collection<Artifact> arts) {
    Objects.requireNonNull(app);
    
    Application existing = null;
    try {
      existing = getApplication(app.getName(), app.getVersion());
    }
    catch (DepConException e) {
      //
    }
    if (existing != null) {
      throw new DepConException(409, String.format("Application '%s:%s' already exists", 
          app.getName(), app.getVersion()));
    }
    
    EntityManager em = EntityManagerUtil.getEntityManager();
    try {
      em.getTransaction().begin();
      for (Artifact art : arts) {
        app.addArtifact(art);
      }
      Application ns = em.merge(app);
      em.getTransaction().commit();
      
      return ns;
    }
    catch (Exception e) {
      em.getTransaction().rollback();
      throw new DepConException(500, String.format("Failed to persist application '%s:%s'.", 
          app.getName(), app.getVersion()), e);
    }
    finally {
      close(em);
    }
  } 
  
  
	
	public void deleteApplication(long id) {
    if (!Main.SU_ROLE_NAME.equals(UserContext.getConext().getSubject().getRole())) {
      throw new DepConException(403, "You do not have permission to perform this operation.");
    }
		EntityManager em = EntityManagerUtil.getEntityManager();
		try {
			em.getTransaction().begin();
			Application app = (Application) em.find(Application.class, id);

			em.remove(app);
			em.getTransaction().commit();
		}
		catch (Exception e) {
			em.getTransaction().rollback();
			throw new DepConException(500, String.format("Failed to delete Application '%d' data from database.",  id), e);
		}
		finally {
			close(em);
		}
	}	
	
}
