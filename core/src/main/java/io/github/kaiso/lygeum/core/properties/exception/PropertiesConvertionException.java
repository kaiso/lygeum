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
package io.github.kaiso.lygeum.core.properties.exception;

/**
 * @author Kais OMRI (kaiso)
 *
 */
public class PropertiesConvertionException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public PropertiesConvertionException() {
		super();
	}

	public PropertiesConvertionException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public PropertiesConvertionException(String message, Throwable cause) {
		super(message, cause);
	}

	public PropertiesConvertionException(String message) {
		super(message);
	}

	public PropertiesConvertionException(Throwable cause) {
		super(cause);
	}

}
