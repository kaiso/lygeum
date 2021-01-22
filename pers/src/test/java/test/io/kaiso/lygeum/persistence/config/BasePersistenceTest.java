/**
* Copyright © Kais OMRI
*    
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*     http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package test.io.kaiso.lygeum.persistence.config;

import mockit.Invocation;
import mockit.Mock;
import mockit.MockUp;
import mockit.internal.injection.BeanExporter;
import mockit.internal.injection.TestedClassInstantiations;
import mockit.internal.state.TestRun;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.h2.tools.Server;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.config.DependencyDescriptor;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

/**
 * @author Kais OMRI (kaiso)
 *
 */
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = { TestPersistenceConfiguration.class })
public class BasePersistenceTest {

    private static Server server;

    @BeforeAll
    public static void init() throws SQLException {
	server = Server.createTcpServer("-trace", "-ifNotExists", "-tcpPort", "9135", "-tcpAllowOthers", "-baseDir", Paths.get("build", "lygeumtestdb").toAbsolutePath().toString()).start();
	
    }

    public static class JmockitFakeBeanFactory extends MockUp<DefaultListableBeanFactory> {

	static Map<String, Object> mocks = new ConcurrentHashMap<>();

	@Mock
	public static Object getBean(@Nonnull Invocation invocation, @Nonnull String name) {
	    TestedClassInstantiations testedClasses = TestRun.getTestedClassInstantiations();
	    System.out.println("1 - bean name " + name);
	    if (testedClasses != null) {
		BeanExporter beanExporter = testedClasses.getBeanExporter();
		Object bean = beanExporter.getBean(name);
		if (bean != null) {
		    mocks.put(bean.getClass().getName(), bean);
		    return bean;
		}
	    }

	    return invocation.proceed();
	}

	@SuppressWarnings("unchecked")
	@Mock
	public static <T> T getBean(@Nonnull Invocation invocation, @Nonnull String name,
		@Nullable Class<T> requiredType) {
	    TestedClassInstantiations testedClasses = TestRun.getTestedClassInstantiations();
	    System.out.println("2 - bean name " + name + " type " + requiredType.getCanonicalName());
	    if (testedClasses != null) {
		BeanExporter beanExporter = testedClasses.getBeanExporter();
		Object bean = beanExporter.getBean(name);
		if (bean == null) {
		    bean = beanExporter.getBean(requiredType);
		}
		if (bean != null) {
		    mocks.put(bean.getClass().getName(), bean);
		    return (T) bean;
		}
	    }

	    return invocation.proceed();
	}

	@SuppressWarnings("unchecked")
	@Mock
	public static <T> T getBean(@Nonnull Invocation invocation, @Nonnull Class<T> requiredType) {
	    TestedClassInstantiations testedClasses = TestRun.getTestedClassInstantiations();
	    System.out.println("3 - bean type " + requiredType.getCanonicalName());
	    if (testedClasses != null) {
		BeanExporter beanExporter = testedClasses.getBeanExporter();
		Object bean = beanExporter.getBean(requiredType);
		if (bean != null) {
		    mocks.put(bean.getClass().getName(), bean);
		    return (T) bean;

		}
	    }

	    return invocation.proceed();
	}

	@Mock
	protected Map<String, Object> findAutowireCandidates(@Nonnull Invocation invocation, @Nullable String beanName,
		Class<?> requiredType, DependencyDescriptor descriptor) {
	    TestedClassInstantiations testedClasses = TestRun.getTestedClassInstantiations();
	    Map<String, Object> proceed = invocation.proceed();

	    try {
		if (testedClasses != null) {
		    BeanExporter beanExporter = testedClasses.getBeanExporter();
		    Object bean = beanExporter.getBean(beanName);
		    if (bean == null) {
			bean = beanExporter.getBean(requiredType);
		    }
		    if (bean != null) {
			mocks.put(bean.getClass().getName(), bean);
		    }
		}
	    } catch (Exception e) {
		e.printStackTrace();
	    }
	    proceed.putAll(mocks);
	    return proceed;
	}
    }

    @AfterAll
    public static void destroy() {
	server.stop();
    }

}
