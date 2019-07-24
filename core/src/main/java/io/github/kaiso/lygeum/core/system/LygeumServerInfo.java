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

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

import javax.annotation.PostConstruct;

import org.springframework.boot.SpringApplication;
import org.springframework.stereotype.Component;

@Component
public class LygeumServerInfo {

	private String implementationVersion;

	@PostConstruct
	public void init() {
		try {
			Enumeration<URL> resEnum = SpringApplication.class.getClassLoader().getResources("META-INF/MANIFEST.MF");
			while (resEnum.hasMoreElements()) {
				extractManifestInformation(resEnum);
			}
		} catch (IOException e) {
			// Silently ignore wrong manifests on classpath?
		}
	}

	private void extractManifestInformation(Enumeration<URL> resEnum) {
		try {
			URL url = resEnum.nextElement();

			InputStream is = url.openStream();

			Manifest manifest = new Manifest(is);
			Attributes mainAttribs = manifest.getMainAttributes();
			if (mainAttribs.containsValue("LYGEUM")) {
				implementationVersion = mainAttribs.getValue("Implementation-Version");
			}
		} catch (Exception e) {
			// Silently ignore wrong manifests on classpath?
		}
	}

	public String getImplementationVersion() {
		return implementationVersion;
	}
}
