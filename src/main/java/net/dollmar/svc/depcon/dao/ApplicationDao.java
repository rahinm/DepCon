package net.dollmar.svc.depcon.dao;

import java.util.List;
import java.util.Objects;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import net.dollmar.svc.depcon.entity.Application;
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
		String queryString = "SELECT a FROM Application a JOIN FETCH a.artifacts l WHERE a.id = :rowId";
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
		String queryString = "SELECT a FROM Application a JOIN FETCH a.artifacts l WHERE a.name = :name AND a.version = :version";
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
	
	public void deleteApplication(long id) {
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
