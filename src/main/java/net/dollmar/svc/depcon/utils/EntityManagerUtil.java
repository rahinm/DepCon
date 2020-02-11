package net.dollmar.svc.depcon.utils;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;


public class EntityManagerUtil {
	public static final String JPA_UNIT_NAME = "LIBRARIES";

	private static final EntityManagerFactory entityManagerFactory;

	static {
		try {
			entityManagerFactory = Persistence.createEntityManagerFactory(JPA_UNIT_NAME);

		} 
		catch (Throwable ex) {
			System.err.println("Initial EntityManager2Factory creation failed." + ex);
			throw new ExceptionInInitializerError(ex);
		}
	}

	public static EntityManager getEntityManager() {
		return entityManagerFactory.createEntityManager();
	}
}
