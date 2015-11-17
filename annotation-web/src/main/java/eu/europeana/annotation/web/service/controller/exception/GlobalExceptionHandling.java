package eu.europeana.annotation.web.service.controller.exception;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

import eu.europeana.annotation.definitions.model.WebAnnotationFields;
import eu.europeana.annotation.web.exception.HttpException;
import eu.europeana.annotation.web.http.HttpHeaders;
import eu.europeana.annotation.web.service.controller.ApiResponseBuilder;

@ControllerAdvice
public class GlobalExceptionHandling extends ApiResponseBuilder{

	Logger logger = Logger.getLogger(getClass());
	
	@ExceptionHandler(HttpException.class)
	public ResponseEntity<String> handleHttpException(HttpException ex, HttpServletRequest req, HttpServletResponse response)
			throws IOException {

		getLogger().error("An error occured during the invocation of :" + req.getServletPath(), ex);
		
		ModelAndView res = getValidationReport(req.getParameter(WebAnnotationFields.PARAM_WSKEY), req.getServletPath(), null, ex);
//		response.sendError(ex.getStatus().value());
		response.setContentType(MediaType.APPLICATION_JSON_VALUE);
		//return res;
		
		MultiValueMap<String, String> headers = new LinkedMultiValueMap<String, String>(5);
		headers.add(HttpHeaders.VARY, HttpHeaders.ACCEPT);
		headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
		//headers.add(HttpHeaders.ETAG, "" + storedAnnotation.getLastUpdate().hashCode());
		//headers.add(HttpHeaders.LINK, HttpHeaders.VALUE_LD_RESOURCE);

		//TODO remove the usage of Model and View
		ResponseEntity<String> responseEntity = new ResponseEntity<String>((String)res.getModel().get("json"), headers, ex.getStatus());
		
		return responseEntity;
	}

	@ExceptionHandler(RuntimeException.class)
	public ResponseEntity<String> handleException(Exception ex, HttpServletRequest req, HttpServletResponse response)
			throws IOException {

		getLogger().error("An unexpected runtime error occured during the invocation of :" + req.getServletPath(), ex);

		//TODO: add AccessController to handle default spring exceptions: http://docs.spring.io/spring/docs/current/spring-framework-reference/htmlsingle/#mvc-ann-rest-spring-mvc-exceptions  
		ModelAndView res = getValidationReport(req.getParameter(WebAnnotationFields.PARAM_WSKEY), req.getServletPath(), null, ex);
		//response.sendError(HttpStatus.INTERNAL_SERVER_ERROR.value());
		response.setContentType(MediaType.APPLICATION_JSON_VALUE);
		//return res;
		
		MultiValueMap<String, String> headers = new LinkedMultiValueMap<String, String>(5);
		headers.add(HttpHeaders.VARY, HttpHeaders.ACCEPT);
		headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
		//headers.add(HttpHeaders.ETAG, "" + storedAnnotation.getLastUpdate().hashCode());
		//headers.add(HttpHeaders.LINK, HttpHeaders.VALUE_LD_RESOURCE);

		//TODO remove the usage of Model and View
		ResponseEntity<String> responseEntity = new ResponseEntity<String>((String)res.getModel().get("json"), headers, HttpStatus.INTERNAL_SERVER_ERROR);
		
		return responseEntity;
	}
}
