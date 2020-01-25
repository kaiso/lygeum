/**
   * Copyright 2018 Kais OMRI [kais.omri.int@gmail.com]
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
package io.github.kaiso.lygeum.loader.boot;

import java.net.URL;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;

import io.github.kaiso.lygeum.core.context.ApplicationContextProvider;
import io.github.kaiso.lygeum.core.system.LygeumServerInfo;

@SpringBootApplication(scanBasePackages = "io.github.kaiso.lygeum")
public class LygeumBootLoader {

	static {
		URL log4j2File = LygeumBootLoader.class.getClassLoader().getResource("log4j2.yaml");
		try {
			System.setProperty("log4j2.configurationFile", log4j2File.toURI().toString());
		} catch (Exception e) {
			// do nothing
		}
	}

	private static final Logger logger = LoggerFactory.getLogger(LygeumBootLoader.class);

	public static void main(String[] args) {
		AtomicReference<String> port = new AtomicReference<String>(null);
		List<String> filteredArgs = Arrays.asList(args).stream().filter(arg -> {
			if (arg.contains("server.port")) {
				port.set(arg.replace("--server.port=", "").trim());
			}
			return !arg.contains("server.name");
		}).collect(Collectors.toList());
		port.compareAndSet(null, System.getProperty("server.port"));
		if (port.get() == null) {
			filteredArgs.add("--server.port=5000");
			port.set("5000");
		}
		logger.debug("Lygeum starting with options:\n {}", filteredArgs.stream().map(a -> {
			if (a.contains("db-password")) {
				return "--db-password=*****";
			}
			return a;
		}).collect(Collectors.toList()));
		filteredArgs.add(
				"--spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.liquibase.LiquibaseAutoConfiguration");
		SpringApplication app = new SpringApplication(LygeumBootLoader.class);
		addInitHooks(app);
		Properties defaultProperties = new Properties();
		defaultProperties.put("server.error.whitelabel.enabled", "false");
		app.setDefaultProperties(defaultProperties);
		ApplicationContext context = app.run(filteredArgs.toArray(new String[(int) filteredArgs.size()]));
		DateTimeFormatter formatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.FULL)
				.withZone(ZoneId.systemDefault());
		String startupDate = formatter.format(Instant.ofEpochMilli(context.getStartupDate()));
		LygeumServerInfo serverInfo = ApplicationContextProvider.getBean(LygeumServerInfo.class);
		logger.info("Lygeum Version: {}", serverInfo.getImplementationVersion());
		logger.info("\nSystem Info [{}\n]", serverInfo.getJvmInformation());
		logger.info("\nPersistence Info [{}\n]", serverInfo.getPersistenceInfo());
		logger.info("Lygeum HTTP connector listening on port {}", port.get());
		logger.info("Lygeum ready Startup Date {}", startupDate);
	}

	private static void addInitHooks(SpringApplication app) {
		app.addListeners((ApplicationListener<ApplicationEnvironmentPreparedEvent>) event -> {
			String version = event.getEnvironment().getProperty("java.runtime.version");
			logger.info("Running with Java {}", version);
		});
	}

}
