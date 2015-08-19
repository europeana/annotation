package eu.europeana.annotation.web.service.controller;

import org.apache.log4j.Logger;
import org.springframework.web.servlet.ModelAndView;

import eu.europeana.annotation.web.model.AnnotationOperationResponse;
import eu.europeana.api2.utils.JsonWebUtils;

public class ApiResponseBuilder {

	Logger logger = Logger.getLogger(getClass());
	
	public Logger getLogger() {
		return logger;
	}

	public AnnotationOperationResponse buildErrorResponse(String errorMessage,
			String action, String apiKey) {
		AnnotationOperationResponse response;
		response = new AnnotationOperationResponse(
				apiKey, action);
		
		response.success = false;
		response.error = errorMessage;
		return response;
	}

	/**
	 * This method generates validation error report.
	 * @param apiKey
	 * @param action
	 * @param errorMessage
	 * @return
	 */
	protected ModelAndView getValidationReport(String apiKey, String action, String errorMessage, Throwable th) {
		
		AnnotationOperationResponse response = new AnnotationOperationResponse(
				apiKey, action);
		
		String message = "";
		
		if (errorMessage != null)
			message+= " " + errorMessage;
		if (th !=null)
			message += th.toString();
		if(th != null && th.getCause() !=null && th != th.getCause())
			message+= " " + th.getCause().toString();
		
		response = buildErrorResponse(message, response.action, response.apikey);

		ModelAndView ret = JsonWebUtils.toJson(response, null);
		
		return ret;
	}

	/**
	 * This method is employed for positive report - without errors.
	 * @param apiKey
	 * @param action
	 * @param message
	 * @return
	 */
	protected ModelAndView getReport(String apiKey, String action, String message) {
		AnnotationOperationResponse response = new AnnotationOperationResponse(
				apiKey, action);
		response = buildResponse(message, response.action, response.apikey);
		ModelAndView ret = JsonWebUtils.toJson(response, null);
		return ret;
	}
	
	public AnnotationOperationResponse buildResponse(String message,
			String action, String apiKey) {
		AnnotationOperationResponse response;
		response = new AnnotationOperationResponse(
				apiKey, action);
		
		response.success = true;
		response.setStatus(message);
		return response;
	}

}
