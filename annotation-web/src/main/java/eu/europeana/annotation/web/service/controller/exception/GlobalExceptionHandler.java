package eu.europeana.annotation.web.service.controller.exception;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import eu.europeana.annotation.web.exception.response.BatchUploadException;
import eu.europeana.annotation.web.model.AnnotationOperationResponse;
import eu.europeana.api.commons.config.i18n.I18nService;
import eu.europeana.api.commons.error.EuropeanaApiErrorResponse;
import eu.europeana.api.commons.web.controller.exception.AbstractExceptionHandlingController;
import eu.europeana.api.commons.web.model.ApiResponse;

@ControllerAdvice
@ConditionalOnWebApplication
public class GlobalExceptionHandler extends AbstractExceptionHandlingController {

	@Resource
	I18nService i18nService;

	protected I18nService getI18nService() {
		return i18nService;
	}
	
	@Override
	public ApiResponse buildErrorResponse(String errorMessage, String action, String apiKey) {

		AnnotationOperationResponse response = new AnnotationOperationResponse(apiKey, action);
		response.success = false;
		response.error = errorMessage;
		return response;
	}
	
	@Override
	protected ApiResponse getErrorReport(String apiKey, String action, Throwable th, boolean includeErrorStack) {
		// TODO Auto-generated method stub
		AnnotationOperationResponse response = (AnnotationOperationResponse) super.getErrorReport(apiKey, action, th, includeErrorStack);
		
		if(th instanceof BatchUploadException)
			response.setOperationReport(((BatchUploadException)th).getOperationReport());

		return response;
	}
	
	  /*
	   * This is the exception thrown when there is a type mismatch, e.g.: 
	   * "Failed to convert value of type [java.lang.String] to required type [java.lang.Long]; nested exception is java.lang.NumberFormatException:...
	   */
	  @ExceptionHandler(MethodArgumentTypeMismatchException.class)
	  public ResponseEntity<EuropeanaApiErrorResponse> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e, HttpServletRequest httpRequest) {
		  EuropeanaApiErrorResponse response = new EuropeanaApiErrorResponse
				  .Builder(httpRequest, e, true)
		          .setStatus(HttpStatus.BAD_REQUEST.value())
		          .setError("Error in parsing request parameter (wrong type).")
		          .setMessage(e.getMessage())
		          .build();

		  return ResponseEntity.status(response.getStatus())
				  .contentType(MediaType.APPLICATION_JSON)
				  .body(response);
	  }

}
