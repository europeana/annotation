package eu.europeana.annotation.web.service.controller.jsonld;

import java.io.IOException;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import eu.europeana.annotation.solr.exceptions.AnnotationServiceException;
import eu.europeana.annotation.statistics.model.AnnotationMetric;
import eu.europeana.annotation.statistics.model.AnnotationStatistics;
import eu.europeana.annotation.statistics.serializer.AnnotationStatisticsSerializer;
import eu.europeana.annotation.statistics.service.AnnotationStatisticsService;
import eu.europeana.annotation.web.exception.InternalServerException;
import eu.europeana.api.common.config.swagger.SwaggerSelect;
import eu.europeana.api.commons.definitions.vocabulary.CommonApiConstants;
import eu.europeana.api.commons.web.exception.ApplicationAuthenticationException;
import eu.europeana.api.commons.web.http.HttpHeaders;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Controller
@SwaggerSelect
@Api(tags = "Annotation Statistics API")
public class AnnotationStatisticsRest extends BaseJsonldRest {

	@Resource
	AnnotationStatisticsService annotationStatisticsService;
    /**
     * Method to generate the statistics for the annotations.
     *
     * @param wsKey
     * @param request
     * @return
     * @throws InternalServerException 
     * @throws AnnotationServiceException 
     */
    @GetMapping(value = "/annotation/statistics", produces = {HttpHeaders.CONTENT_TYPE_JSON_UTF8})
    @ApiOperation(value = "Generate annotations statisticss", nickname = "generateAnnotationStatistics", response = java.lang.Void.class)
    public ResponseEntity<String> generateAnnotationStatistics(
            @RequestParam(value = CommonApiConstants.PARAM_WSKEY, required = true) String wsKey,
            HttpServletRequest request) throws ApplicationAuthenticationException, InternalServerException {
        // authenticate
        verifyReadAccess(request);
    	try {
			return getAnnotationStatistics(request);
		} catch (IOException | AnnotationServiceException e) {
			throw new InternalServerException(e);
		}
    }

    private ResponseEntity<String> getAnnotationStatistics(HttpServletRequest request) throws IOException, AnnotationServiceException {
        // create metric
        AnnotationMetric annoMetric = new AnnotationMetric();
        annotationStatisticsService.getAnnotationsStatistics(annoMetric);
        String json = serializeMetricView(annoMetric);
        return buildUsageStatsResponse(json);
    }

    private ResponseEntity<String> buildUsageStatsResponse(String json) {
        // build response
        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.add(HttpHeaders.ALLOW, HttpHeaders.ALLOW_GET);
        return new ResponseEntity<>(json, headers, HttpStatus.OK);
    }

    private String serializeMetricView(AnnotationMetric annoMetric) throws IOException {
    	AnnotationStatisticsSerializer serializer = new AnnotationStatisticsSerializer();
        return serializer.serialize(annoMetric);
    }

}