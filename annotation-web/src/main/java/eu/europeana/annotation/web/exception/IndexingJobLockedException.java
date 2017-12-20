package eu.europeana.annotation.web.exception;

import org.springframework.http.HttpStatus;

import eu.europeana.api.common.config.I18nConstants;
import eu.europeana.api.commons.web.exception.HttpException;

public class IndexingJobLockedException extends HttpException {
	
	private static final long serialVersionUID = -7985477678792399001L;

	public IndexingJobLockedException(String action) {
		super(null, I18nConstants.API_WRITE_LOCK, new String[]{action}, HttpStatus.LOCKED);
	}

	

}
