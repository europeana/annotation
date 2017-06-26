package eu.europeana.annotation.web.exception.response;

import org.springframework.http.HttpStatus;

import eu.europeana.annotation.web.model.BatchReportable;
import eu.europeana.annotation.web.model.BatchUploadStatus;
import eu.europeana.api.common.config.I18nConstants;
import eu.europeana.api.commons.web.exception.HttpException;

public class BatchUploadException extends HttpException{

	private static final long serialVersionUID = 3050674865876453650L;
	
	private BatchUploadStatus operationReport;
		
//	public BatchUploadException(){
//		super(null, I18nConstants.BATCH_UPLOAD_FAILED, HttpStatus.BAD_REQUEST);
//	}
	
	public BatchUploadException(String message, BatchUploadStatus batchReport){
		super(message, I18nConstants.BATCH_UPLOAD_FAILED, HttpStatus.BAD_REQUEST);
		this.operationReport = batchReport;
	}
	
	public BatchUploadException(String message, BatchUploadStatus batchReport, HttpStatus status){
		super(message, I18nConstants.BATCH_UPLOAD_FAILED, status);
		this.operationReport = batchReport;
	}
	
	public BatchUploadException(String message, BatchUploadStatus batchReport, HttpStatus status, Throwable th){
		super(message, I18nConstants.BATCH_UPLOAD_FAILED, null, status, th);
		this.operationReport = batchReport;
	}

	public BatchReportable getOperationReport() {
		return operationReport;
	}
}

