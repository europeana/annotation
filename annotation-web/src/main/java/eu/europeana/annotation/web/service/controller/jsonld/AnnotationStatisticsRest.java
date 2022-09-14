package eu.europeana.annotation.web.service.controller.jsonld;

import java.io.IOException;
import java.util.Date;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import eu.europeana.annotation.solr.exceptions.AnnotationServiceException;
import eu.europeana.annotation.statistics.model.AnnotationMetric;
import eu.europeana.annotation.statistics.serializer.AnnotationStatisticsSerializer;
import eu.europeana.annotation.statistics.service.AnnotationStatisticsService;
import eu.europeana.annotation.web.exception.InternalServerException;
import eu.europeana.annotation.web.service.SearchServiceUtils;
import eu.europeana.api.commons.definitions.vocabulary.CommonApiConstants;
import eu.europeana.api.commons.web.exception.HttpException;
import eu.europeana.api.commons.web.http.HttpHeaders;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
//@SwaggerSelect
@Api(tags = "Annotation Statistics", description=" ")
public class AnnotationStatisticsRest extends BaseJsonldRest {

	@Resource(name="annotationStatisticsService")
	AnnotationStatisticsService annotationStatisticsService;
    /**
     * Method to generate the statistics for the annotations.
     *
     * @param wsKey
     * @param request
     * @return
     * @throws HttpException 
     * @throws AnnotationServiceException 
     */
    @GetMapping(value = "/annotation/stats", produces = {HttpHeaders.CONTENT_TYPE_JSON_UTF8})
    @ApiOperation(value = "Generate annotations statisticss", nickname = "generateAnnotationStatistics", response = java.lang.Void.class)
    public ResponseEntity<String> generateAnnotationStatistics(
            @RequestParam(value = CommonApiConstants.PARAM_WSKEY, required = true) String wsKey,
            HttpServletRequest request) throws HttpException {
        // authenticate
        verifyReadAccess(request);
    	return getAnnotationStatistics(request);
    }

    private ResponseEntity<String> getAnnotationStatistics(HttpServletRequest request) throws HttpException {
        // create metric
        AnnotationMetric annoMetric = new AnnotationMetric();
        annoMetric.setCreated(new Date());
        try {
          annotationStatisticsService.getAnnotationsStatistics(annoMetric);
        } catch (AnnotationServiceException e) {
          throw SearchServiceUtils.convertSearchException("verify statistics computation queries", e);
        } 
        String json = serializeMetricView(annoMetric);
        return buildUsageStatsResponse(json);
    }

    private ResponseEntity<String> buildUsageStatsResponse(String json) {
        // build response
        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.add(HttpHeaders.ALLOW, HttpHeaders.ALLOW_GET);
        return new ResponseEntity<>(json, headers, HttpStatus.OK);
    }

    private String serializeMetricView(AnnotationMetric annoMetric) throws HttpException {
    	AnnotationStatisticsSerializer serializer = new AnnotationStatisticsSerializer();
    	try {
          return serializer.serialize(annoMetric);
    	} catch (IOException e) {
          throw new InternalServerException(e);
        }
    }

}