package io.github.kaiso.lygeum.core.context;

import javax.persistence.EntityManager;

import org.springframework.context.ApplicationContext;

public class ApplicationContextProvider {

	private static ApplicationContext applicationContext;

	public static void setApplicationContext(ApplicationContext applicationContext) {
		ApplicationContextProvider.applicationContext = applicationContext;
	}

	public static EntityManager getEntityManager() {
		return applicationContext.getAutowireCapableBeanFactory().getBean(EntityManager.class);
	}

}
