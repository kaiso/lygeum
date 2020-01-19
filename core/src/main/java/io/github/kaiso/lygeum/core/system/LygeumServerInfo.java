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
package io.github.kaiso.lygeum.core.system;

import java.net.URL;
import java.util.Enumeration;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

import org.springframework.boot.SpringApplication;
import org.springframework.orm.jpa.EntityManagerFactoryInfo;
import org.springframework.stereotype.Component;

import io.github.kaiso.lygeum.core.context.ApplicationContextProvider;

@Component
public class LygeumServerInfo {

	public String getImplementationVersion() {
		String implementationVersion = "";
		try {
			Enumeration<URL> resEnum = SpringApplication.class.getClassLoader().getResources("META-INF/MANIFEST.MF");
			while (resEnum.hasMoreElements()) {
				Attributes mainAttribs = new Manifest(resEnum.nextElement().openStream()).getMainAttributes();
				if (mainAttribs.containsValue("LYGEUM")) {
					implementationVersion = mainAttribs.getValue("Implementation-Version");
					break;
				}
			}
		} catch (Exception e) {
			// Silently ignore wrong manifests on classpath?
		}
		return implementationVersion;
	}

	public String getPersistenceInfo() {
		EntityManagerFactoryInfo jpaInfo = ApplicationContextProvider.getBean(EntityManagerFactoryInfo.class);
		return new StringBuilder().append("\n\t").append("DB Vendor: ").append(System.getProperty("lygeum.db.vendor"))
				.append("\n\t").append("Host: ").append(System.getProperty("lygeum.db.host")).append("\n\t")
				.append("Datasource: ").append(jpaInfo.getDataSource()).append("\n\t").append("Classloader: ")
				.append(jpaInfo.getBeanClassLoader()).toString();
	}

	public String getJvmInformation() {
		return new StringBuilder().append("\n\tName: ").append(System.getProperty("java.vm.name")).append("\n\t")
				.append("Vendor: ").append(System.getProperty("java.vendor")).append("\n\t").append("Version: ")
				.append(System.getProperty("java.version")).append("\n\t").append("Specification: ")
				.append(System.getProperty("java.specification.vendor")).toString();
	}
}
