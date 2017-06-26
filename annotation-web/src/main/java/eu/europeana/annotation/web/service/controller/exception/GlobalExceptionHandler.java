package eu.europeana.annotation.web.service.controller.exception;

import javax.annotation.Resource;

import org.springframework.web.bind.annotation.ControllerAdvice;

import eu.europeana.annotation.web.exception.response.BatchUploadException;
import eu.europeana.annotation.web.model.AnnotationOperationResponse;
import eu.europeana.api.commons.config.i18n.I18nService;
import eu.europeana.api.commons.web.controller.exception.AbstractExceptionHandlingController;
import eu.europeana.api.commons.web.model.ApiResponse;

@ControllerAdvice
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

}
