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
package io.github.kaiso.lygeum.api.handler;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import io.github.kaiso.lygeum.api.resources.OperationResult;
import io.github.kaiso.lygeum.api.resources.OperationResult.OperationResultCode;
import io.github.kaiso.lygeum.core.properties.exception.PropertiesConvertionException;
import io.github.kaiso.lygeum.core.security.exception.ResourceAccessDeniedException;
import io.github.kaiso.lygeum.core.security.exception.UnauthorizedException;

/** @author Kais OMRI (kaiso) */
@ControllerAdvice
public class GlobalControllerExceptionHandler {

  @ResponseStatus(value = HttpStatus.CONFLICT, reason = "Data integrity violation") // 409
  @ExceptionHandler(DataIntegrityViolationException.class)
  public void handleConflict() {
    // Nothing to do
  }

  @ExceptionHandler(UnauthorizedException.class)
  public ResponseEntity<OperationResult> unauthorized(HttpServletResponse resp, Exception ex)
      throws IOException {
    return buildErrorResponse(resp, ex, HttpStatus.UNAUTHORIZED);
  }

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<OperationResult> illegalArgument(HttpServletResponse resp, Exception ex)
      throws IOException {
    return buildErrorResponse(resp, ex, HttpStatus.UNPROCESSABLE_ENTITY);
  }

  @ExceptionHandler(PropertiesConvertionException.class)
  public ResponseEntity<OperationResult> failedConversion(HttpServletResponse resp, Exception ex)
      throws IOException {
    return buildErrorResponse(resp, ex, HttpStatus.INTERNAL_SERVER_ERROR);
  }

  @ExceptionHandler(HttpMediaTypeNotAcceptableException.class)
  public ResponseEntity<OperationResult> handleHttpMediaTypeNotAcceptableException(
      HttpServletResponse resp, Exception ex) throws IOException {
    return buildErrorResponse(resp, ex, HttpStatus.UNSUPPORTED_MEDIA_TYPE);
  }

  @ResponseStatus(value = HttpStatus.FORBIDDEN, reason = "Unsafficient privileges") // 409
  @ExceptionHandler(ResourceAccessDeniedException.class)
  public void accessDenied() {
    // Nothing to do
  }

  private ResponseEntity<OperationResult> buildErrorResponse(
      HttpServletResponse resp, Exception ex, HttpStatus status) throws IOException {
    OperationResultCode code;
    switch (status) {
      case UNAUTHORIZED:
        code = OperationResultCode.VALIDATION_FAILURE;
        break;
      case UNPROCESSABLE_ENTITY:
        code = OperationResultCode.VALIDATION_FAILURE;
        break;
      default:
        code = OperationResultCode.INTERNAL_ERROR;
        break;
    }
    return new ResponseEntity<>(
        OperationResult.builder().withCode(code).withMessage(ex.getMessage()).build(),
        new HttpHeaders(),
        status);
  }
}
