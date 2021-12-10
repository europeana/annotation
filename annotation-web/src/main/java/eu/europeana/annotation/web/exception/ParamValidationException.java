package eu.europeana.annotation.web.exception;

import eu.europeana.api.commons.error.EuropeanaApiException;
import org.springframework.http.HttpStatus;

public class ParamValidationException extends EuropeanaApiException {

	private static final long serialVersionUID = 4024572522422810528L;

	public ParamValidationException(String msg) {
		super(msg);
	}

	@Override
	public boolean doLog() {
		return false;
	}

	@Override
	public boolean doLogStacktrace() {
		return false;
	}

	@Override
	public HttpStatus getResponseStatus() {
		return HttpStatus.BAD_REQUEST;
	}
}
