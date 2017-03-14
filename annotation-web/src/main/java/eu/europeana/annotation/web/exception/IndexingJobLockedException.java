package eu.europeana.annotation.web.exception;

import org.springframework.http.HttpStatus;

public class IndexingJobLockedException extends HttpException {
	
	private static final long serialVersionUID = -7985477678792399001L;
	public static final String ERROR_MESSAGE_LOCKED = "Unable to fulfil request because indexing job is locked: ";

	public IndexingJobLockedException(String action) {
		super(ERROR_MESSAGE_LOCKED + action, HttpStatus.LOCKED);
	}

	

}
