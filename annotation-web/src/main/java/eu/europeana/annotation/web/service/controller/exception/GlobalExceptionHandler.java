package eu.europeana.annotation.web.service.controller.exception;

import javax.annotation.Resource;

import org.springframework.web.bind.annotation.ControllerAdvice;

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
	
//	Logger logger = Logger.getLogger(getClass());
//	
//	final static Map<Class<? extends Exception>, HttpStatus> statusCodeMap = new HashMap<Class<? extends Exception>, HttpStatus>(); 
//	//see DefaultHandlerExceptionResolver.doResolveException
//	static {
//		statusCodeMap.put(NoSuchRequestHandlingMethodException.class, HttpStatus.NOT_FOUND);
//		statusCodeMap.put(HttpRequestMethodNotSupportedException.class, HttpStatus.METHOD_NOT_ALLOWED);
//		statusCodeMap.put(HttpMediaTypeNotSupportedException.class, HttpStatus.UNSUPPORTED_MEDIA_TYPE);
//		statusCodeMap.put(HttpMediaTypeNotAcceptableException.class, HttpStatus.NOT_ACCEPTABLE);
//		statusCodeMap.put(MissingServletRequestParameterException.class, HttpStatus.BAD_REQUEST);
//		statusCodeMap.put(ServletRequestBindingException.class, HttpStatus.BAD_REQUEST);
//		statusCodeMap.put(ConversionNotSupportedException.class, HttpStatus.INTERNAL_SERVER_ERROR);
//		statusCodeMap.put(TypeMismatchException.class, HttpStatus.BAD_REQUEST);
//		statusCodeMap.put(HttpMessageNotReadableException.class, HttpStatus.BAD_REQUEST);
//		statusCodeMap.put(HttpMessageNotWritableException.class, HttpStatus.INTERNAL_SERVER_ERROR);
//		statusCodeMap.put(MethodArgumentNotValidException.class, HttpStatus.BAD_REQUEST);
//		statusCodeMap.put(MissingServletRequestPartException.class, HttpStatus.BAD_REQUEST);
//		statusCodeMap.put(BindException.class, HttpStatus.BAD_REQUEST);
//		statusCodeMap.put(NoHandlerFoundException.class, HttpStatus.NOT_FOUND);
//	}
//	
//	@ExceptionHandler(HttpException.class)
//	public ResponseEntity<String> handleHttpException(HttpException ex, HttpServletRequest req,
//			HttpServletResponse response) throws IOException {
//
//		boolean includeErrorStack = new Boolean(req.getParameter(WebAnnotationFields.PARAM_INCLUDE_ERROR_STACK));
//		AnnotationOperationResponse res = getValidationReport(req.getParameter(WebAnnotationFields.PARAM_WSKEY), req.getServletPath(),
//				null, ex, includeErrorStack);
//
//		logger.debug(res.getError(), ex);
//		return buildErrorResponse(res, ex.getStatus());
//
//	}
//
//	@ExceptionHandler(RuntimeException.class)
//	public ResponseEntity<String> handleException(Exception ex, HttpServletRequest req, HttpServletResponse response)
//			throws IOException {
//
//		boolean includeErrorStack =new Boolean(req.getParameter(WebAnnotationFields.PARAM_INCLUDE_ERROR_STACK));
//		AnnotationOperationResponse res = getValidationReport(req.getParameter(WebAnnotationFields.PARAM_WSKEY), req.getServletPath(),
//				null, ex, includeErrorStack);
//
//		logger.debug(res.getError(), ex);
//		return buildErrorResponse(res, HttpStatus.INTERNAL_SERVER_ERROR);
//
//	}
//
//	@ExceptionHandler({ServletException.class, NestedRuntimeException.class, MethodArgumentNotValidException.class, BindException.class})
//	public ResponseEntity<String> handleMissingRequestParamException(Exception ex, HttpServletRequest req,
//			HttpServletResponse response) throws IOException {
//
//		
//		boolean includeErrorStack = new Boolean(req.getParameter(WebAnnotationFields.PARAM_INCLUDE_ERROR_STACK));
//		
//		HttpStatus statusCode = statusCodeMap.get(ex.getClass());
//		if(statusCode == null)
//			statusCode = HttpStatus.INTERNAL_SERVER_ERROR;
//		
//		AnnotationOperationResponse res = getValidationReport(req.getParameter(WebAnnotationFields.PARAM_WSKEY), req.getServletPath(),
//				ex.getMessage(), ex, includeErrorStack);
//
//		logger.debug(res.getError(), ex);
//		return buildErrorResponse(res, statusCode);
//	}
//
//	protected ResponseEntity<String> buildErrorResponse(AnnotationOperationResponse res 
//			, HttpStatus status) {
//
//		String body = JsonWebUtils.toJson(res);
//
//		MultiValueMap<String, String> headers = buildHeadersMap();
//
//		ResponseEntity<String> responseEntity = new ResponseEntity<String>(body, headers, status);
//		return responseEntity;
//	}
//
//	protected MultiValueMap<String, String> buildHeadersMap() {
//		MultiValueMap<String, String> headers = new LinkedMultiValueMap<String, String>(5);
//		headers.add(HttpHeaders.VARY, HttpHeaders.ACCEPT);
//		headers.add(HttpHeaders.CONTENT_TYPE, HttpHeaders.CONTENT_TYPE_JSON_UTF8);
//		// headers.add(HttpHeaders.ETAG, "" +
//		// storedAnnotation.getLastUpdate().hashCode());
//		// headers.add(HttpHeaders.LINK, HttpHeaders.VALUE_LD_RESOURCE);
//		return headers;
//	}
//
//	@Override
//	protected I18nService getI18nService() {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public ApiResponse buildErrorResponse(String errorMessage, String action, String apiKey) {
//		// TODO Auto-generated method stub
//		return null;
//	}

//	protected I18nService getI18nService() {
//		return i18nService;
//	}

}
