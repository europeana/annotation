package eu.europeana.annotation.web.service.controller.exception;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.ConversionNotSupportedException;
import org.springframework.beans.TypeMismatchException;
import org.springframework.core.NestedRuntimeException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.validation.BindException;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.multiaction.NoSuchRequestHandlingMethodException;

import eu.europeana.annotation.definitions.model.vocabulary.WebAnnotationFields;
import eu.europeana.annotation.web.exception.HttpException;
import eu.europeana.annotation.web.http.HttpHeaders;
import eu.europeana.annotation.web.model.AnnotationOperationResponse;
import eu.europeana.annotation.web.service.controller.ApiResponseBuilder;
import eu.europeana.api2.utils.JsonWebUtils;

@ControllerAdvice
public class GlobalExceptionHandling extends ApiResponseBuilder {

	Logger logger = Logger.getLogger(getClass());
	
	final static Map<Class<? extends Exception>, HttpStatus> statusCodeMap = new HashMap<Class<? extends Exception>, HttpStatus>(); 
	//see DefaultHandlerExceptionResolver.doResolveException
	static {
		statusCodeMap.put(NoSuchRequestHandlingMethodException.class, HttpStatus.NOT_FOUND);
		statusCodeMap.put(HttpRequestMethodNotSupportedException.class, HttpStatus.METHOD_NOT_ALLOWED);
		statusCodeMap.put(HttpMediaTypeNotSupportedException.class, HttpStatus.UNSUPPORTED_MEDIA_TYPE);
		statusCodeMap.put(HttpMediaTypeNotAcceptableException.class, HttpStatus.NOT_ACCEPTABLE);
		statusCodeMap.put(MissingServletRequestParameterException.class, HttpStatus.BAD_REQUEST);
		statusCodeMap.put(ServletRequestBindingException.class, HttpStatus.BAD_REQUEST);
		statusCodeMap.put(ConversionNotSupportedException.class, HttpStatus.INTERNAL_SERVER_ERROR);
		statusCodeMap.put(TypeMismatchException.class, HttpStatus.BAD_REQUEST);
		statusCodeMap.put(HttpMessageNotReadableException.class, HttpStatus.BAD_REQUEST);
		statusCodeMap.put(HttpMessageNotWritableException.class, HttpStatus.INTERNAL_SERVER_ERROR);
		statusCodeMap.put(MethodArgumentNotValidException.class, HttpStatus.BAD_REQUEST);
		statusCodeMap.put(MissingServletRequestPartException.class, HttpStatus.BAD_REQUEST);
		statusCodeMap.put(BindException.class, HttpStatus.BAD_REQUEST);
		statusCodeMap.put(NoHandlerFoundException.class, HttpStatus.NOT_FOUND);
	}
	
	@ExceptionHandler(HttpException.class)
	public ResponseEntity<String> handleHttpException(HttpException ex, HttpServletRequest req,
			HttpServletResponse response) throws IOException {

		boolean includeErrorStack = new Boolean(req.getParameter(WebAnnotationFields.PARAM_INCLUDE_ERROR_STACK));
		AnnotationOperationResponse res = getValidationReport(req.getParameter(WebAnnotationFields.PARAM_WSKEY), req.getServletPath(),
				null, ex, includeErrorStack);

		logger.debug(res.getError(), ex);
		return buildErrorResponse(res, ex.getStatus());

	}

	@ExceptionHandler(RuntimeException.class)
	public ResponseEntity<String> handleException(Exception ex, HttpServletRequest req, HttpServletResponse response)
			throws IOException {

		boolean includeErrorStack =new Boolean(req.getParameter(WebAnnotationFields.PARAM_INCLUDE_ERROR_STACK));
		AnnotationOperationResponse res = getValidationReport(req.getParameter(WebAnnotationFields.PARAM_WSKEY), req.getServletPath(),
				null, ex, includeErrorStack);

		logger.debug(res.getError(), ex);
		return buildErrorResponse(res, HttpStatus.INTERNAL_SERVER_ERROR);

	}

	@ExceptionHandler({ServletException.class, NestedRuntimeException.class, MethodArgumentNotValidException.class, BindException.class})
	public ResponseEntity<String> handleMissingRequestParamException(Exception ex, HttpServletRequest req,
			HttpServletResponse response) throws IOException {

		
		boolean includeErrorStack = new Boolean(req.getParameter(WebAnnotationFields.PARAM_INCLUDE_ERROR_STACK));
		
		HttpStatus statusCode = statusCodeMap.get(ex.getClass());
		if(statusCode == null)
			statusCode = HttpStatus.INTERNAL_SERVER_ERROR;
		
		AnnotationOperationResponse res = getValidationReport(req.getParameter(WebAnnotationFields.PARAM_WSKEY), req.getServletPath(),
				ex.getMessage(), ex, includeErrorStack);

		logger.debug(res.getError(), ex);
		return buildErrorResponse(res, statusCode);
	}

	protected ResponseEntity<String> buildErrorResponse(AnnotationOperationResponse res 
			, HttpStatus status) {

		String body = JsonWebUtils.toJson(res);

		MultiValueMap<String, String> headers = buildHeadersMap();

		ResponseEntity<String> responseEntity = new ResponseEntity<String>(body, headers, status);
		return responseEntity;
	}

	protected MultiValueMap<String, String> buildHeadersMap() {
		MultiValueMap<String, String> headers = new LinkedMultiValueMap<String, String>(5);
		headers.add(HttpHeaders.VARY, HttpHeaders.ACCEPT);
		headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
		// headers.add(HttpHeaders.ETAG, "" +
		// storedAnnotation.getLastUpdate().hashCode());
		// headers.add(HttpHeaders.LINK, HttpHeaders.VALUE_LD_RESOURCE);
		return headers;
	}

}
