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
package io.github.kaiso.lygeum.api.handler;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import io.github.kaiso.lygeum.core.properties.exception.PropertiesConvertionException;
import io.github.kaiso.lygeum.core.security.exception.ResourceAccessDeniedException;
import io.github.kaiso.lygeum.core.security.exception.UnauthorizedException;

/**
 * @author Kais OMRI (kaiso)
 *
 */
@ControllerAdvice
public class GlobalControllerExceptionHandler {

	@ResponseStatus(value = HttpStatus.CONFLICT, reason = "Data integrity violation") // 409
	@ExceptionHandler(DataIntegrityViolationException.class)
	public void handleConflict() {
		// Nothing to do
	}

	@ExceptionHandler(UnauthorizedException.class)
	public ResponseEntity<Object> unauthorized(HttpServletResponse resp, Exception ex) throws IOException {
		return buildErrorResponse(resp, ex, HttpStatus.UNAUTHORIZED);
	}

	@ExceptionHandler(IllegalArgumentException.class)
	public ResponseEntity<Object> illegalArgument(HttpServletResponse resp, Exception ex) throws IOException {
		return buildErrorResponse(resp, ex, HttpStatus.UNPROCESSABLE_ENTITY);
	}

	@ExceptionHandler(PropertiesConvertionException.class)
	public ResponseEntity<Object> failedConversion(HttpServletResponse resp, Exception ex) throws IOException {
		return buildErrorResponse(resp, ex, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@ResponseStatus(value = HttpStatus.FORBIDDEN, reason = "Unsafficient privileges") // 409
	@ExceptionHandler(ResourceAccessDeniedException.class)
	public void accessDenied() {
		// Nothing to do
	}

	private ResponseEntity<Object> buildErrorResponse(HttpServletResponse resp, Exception ex, HttpStatus status)
			throws IOException {
		Map<String, String> map = new HashMap<>();
		map.put("status", status.name());
		map.put("message", ex.getMessage());
		map.put("type", "error");
		resp.sendError(status.value(), ex.getMessage());
		return new ResponseEntity<>(map, new HttpHeaders(), status);
	}
}
