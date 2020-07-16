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
package test.io.github.kaiso.lygeum.api.util;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.springframework.test.web.servlet.MvcResult;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.github.kaiso.lygeum.core.context.ApplicationContextProvider;

/**
 * @author Kais OMRI (kaiso)
 *
 */
public class PrintUtils {

	public static void printResponse(MvcResult result) throws UnsupportedEncodingException {
		System.out.println("Response : ");
		System.out.println("Status : " + result.getResponse().getStatus());
		System.out.println("Error : " + result.getResponse().getErrorMessage());
		System.out.println("Body : " + result.getResponse().getContentAsString());
	}

	public static String json(Object o) throws IOException {
		return ApplicationContextProvider.getBean(ObjectMapper.class).writeValueAsString(o);
	}

}
