package eu.europeana.annotation.web.service;

import org.springframework.http.HttpStatus;
import eu.europeana.annotation.solr.exceptions.AnnotationServiceException;
import eu.europeana.annotation.solr.service.impl.SolrAnnotationUtils;
import eu.europeana.api.common.config.I18nConstants;
import eu.europeana.api.commons.web.exception.HttpException;

public abstract class SearchServiceUtils {

  public static HttpException convertSearchException(String debugInfo, AnnotationServiceException e) {
    if (SolrAnnotationUtils.isMalformedQueryException(e.getCause())) {
      return new HttpException(I18nConstants.SOLR_MALFORMED_QUERY_EXCEPTION,
          I18nConstants.SOLR_MALFORMED_QUERY_EXCEPTION, new String[] {debugInfo},
          HttpStatus.BAD_REQUEST, null);
    } else {
      return new HttpException(I18nConstants.SOLR_EXCEPTION, I18nConstants.SOLR_EXCEPTION,
          new String[] {debugInfo}, HttpStatus.GATEWAY_TIMEOUT, e);
    }
  }
}
