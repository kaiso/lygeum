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
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import io.github.kaiso.lygeum.core.system.LygeumServerInfo;

@SpringBootApplication(scanBasePackages = "io.github.kaiso.lygeum")
public class LygeumBootLoader implements ApplicationRunner {

	static {
		URL log4j2File = LygeumBootLoader.class.getClassLoader().getResource("log4j2.yaml");
		try {
			System.setProperty("log4j2.configurationFile", log4j2File.toURI().toString());
		} catch (Exception e) {
			// do nothing
		}
	}

	private static final Logger logger = LoggerFactory.getLogger(LygeumBootLoader.class);

	@Autowired
	private LygeumServerInfo serverInfo;

	@Override
	public void run(ApplicationArguments args) throws Exception {

		String printableArgs = Arrays.toString(args.getSourceArgs());
		logger.debug("Lygeum starting with options:\n {}", printableArgs);
		logger.info("Lygeum Version: {}", serverInfo.getImplementationVersion());
	}

	public static void main(String[] args) {
		List<String> filteredArgs = Arrays.asList(args).stream().filter(arg -> {
			return !arg.contains("server.name");
		}).collect(Collectors.toList());
		filteredArgs.add(
				"--spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.liquibase.LiquibaseAutoConfiguration");
		SpringApplication app = new SpringApplication(LygeumBootLoader.class);
		Properties defaultProperties = new Properties();
		defaultProperties.put("server.error.whitelabel.enabled", "false");
		app.setDefaultProperties(defaultProperties);
		ApplicationContext context = app.run(filteredArgs.toArray(new String[(int) filteredArgs.size()]));
		DateTimeFormatter formatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.FULL)
				.withZone(ZoneId.systemDefault());
		String startupDate = formatter.format(Instant.ofEpochMilli(context.getStartupDate()));
		logger.info("Lygeum ready Startup Date {}", startupDate);
	}

}
