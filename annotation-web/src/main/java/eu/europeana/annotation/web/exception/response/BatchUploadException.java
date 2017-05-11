package eu.europeana.annotation.web.exception.response;

import java.util.HashMap;

import org.springframework.http.HttpStatus;

import eu.europeana.annotation.web.exception.HttpException;
import eu.europeana.annotation.web.model.BatchOperationStep;
import eu.europeana.annotation.web.model.BatchReportable;
import eu.europeana.annotation.web.model.BatchUploadStatus;

public class BatchUploadException extends HttpException{

	private static final long serialVersionUID = 3050674865876453650L;
	
	private BatchUploadStatus operationReport;

	public static final String BATCH_UPLOAD_FAILED = "Batch upload failed!";
		
	public BatchUploadException(){
		super(BATCH_UPLOAD_FAILED, HttpStatus.BAD_REQUEST);
	}
	
	public BatchUploadException(String message, BatchUploadStatus batchReport){
		super(message, HttpStatus.BAD_REQUEST);
		this.operationReport = batchReport;
	}
	
	public BatchUploadException(String message, BatchUploadStatus batchReport, HttpStatus status){
		super(message, status);
		this.operationReport = batchReport;
	}
	
	public BatchUploadException(String message, BatchUploadStatus batchReport, HttpStatus status, Throwable th){
		super(message, status, th);
		this.operationReport = batchReport;
	}

	public BatchReportable getOperationReport() {
		return operationReport;
	}
}

