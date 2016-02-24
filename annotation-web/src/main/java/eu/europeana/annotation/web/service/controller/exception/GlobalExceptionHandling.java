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
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

import eu.europeana.annotation.definitions.model.vocabulary.fields.WebAnnotationFields;
import eu.europeana.annotation.web.exception.HttpException;
import eu.europeana.annotation.web.http.HttpHeaders;
import eu.europeana.annotation.web.service.controller.ApiResponseBuilder;

@ControllerAdvice
public class GlobalExceptionHandling extends ApiResponseBuilder {

	Logger logger = Logger.getLogger(getClass());

	@ExceptionHandler(HttpException.class)
	public ResponseEntity<String> handleHttpException(HttpException ex, HttpServletRequest req,
			HttpServletResponse response) throws IOException {

		// TODO remove the usage of Model and View
		boolean includeErrorStack = Boolean.getBoolean(req.getParameter(WebAnnotationFields.PARAM_INCLUDE_ERROR_STACK));
		ModelAndView res = getValidationReport(req.getParameter(WebAnnotationFields.PARAM_WSKEY), req.getServletPath(),
				null, ex, includeErrorStack);

		return buildErrorResponse(ex, req, response, res, ex.getStatus());

	}

	@ExceptionHandler(RuntimeException.class)
	public ResponseEntity<String> handleException(Exception ex, HttpServletRequest req, HttpServletResponse response)
			throws IOException {

		// TODO remove the usage of Model and View
		boolean includeErrorStack = Boolean.getBoolean(req.getParameter(WebAnnotationFields.PARAM_INCLUDE_ERROR_STACK));
		ModelAndView res = getValidationReport(req.getParameter(WebAnnotationFields.PARAM_WSKEY), req.getServletPath(),
				null, ex, includeErrorStack);

		return buildErrorResponse(ex, req, response, res, HttpStatus.INTERNAL_SERVER_ERROR);

	}

	@ExceptionHandler(MissingServletRequestParameterException.class)
	public ResponseEntity<String> handleMissingRequestParamException(MissingServletRequestParameterException ex, HttpServletRequest req,
			HttpServletResponse response) throws IOException {

		// TODO remove the usage of Model and View
		ModelAndView res = getValidationReport(req.getParameter(WebAnnotationFields.PARAM_WSKEY), req.getServletPath(),
				ex.getMessage(), ex, false);

		return buildErrorResponse(ex, req, response, res, HttpStatus.INTERNAL_SERVER_ERROR);

	}

	protected ResponseEntity<String> buildErrorResponse(Exception ex, HttpServletRequest req,
			HttpServletResponse response, ModelAndView res, HttpStatus status) {
		String body = (String) res.getModel().get("json");

		getLogger().error("An error occured during the invocation of :" + req.getServletPath(), ex);

		response.setContentType(MediaType.APPLICATION_JSON_VALUE);

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
