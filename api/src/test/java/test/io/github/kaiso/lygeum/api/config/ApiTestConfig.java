/**
 * Copyright © Kais OMRI
 *
 * <p>Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * <p>http://www.apache.org/licenses/LICENSE-2.0
 *
 * <p>Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing permissions and
 * limitations under the License.
 */
package test.io.github.kaiso.lygeum.api.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import io.github.kaiso.lygeum.core.config.HttpConfig;
import io.github.kaiso.lygeum.core.config.JSONConfig;

/** @author Kais OMRI (kaiso) */
@Configuration
@Import({JSONConfig.class, HttpConfig.class})
@ComponentScan(basePackages = {"io.github.kaiso.lygeum.core.context"})
public class ApiTestConfig {}
