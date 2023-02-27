package eu.europeana.annotation.statistics.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.response.json.BucketBasedJsonFacet;
import org.apache.solr.client.solrj.response.json.BucketJsonFacet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import eu.europeana.annotation.config.AnnotationConfiguration;
import eu.europeana.annotation.definitions.model.vocabulary.AnnotationScenarioTypes;
import eu.europeana.annotation.solr.exceptions.AnnotationServiceException;
import eu.europeana.annotation.solr.service.SolrAnnotationService;
import eu.europeana.annotation.solr.vocabulary.SolrAnnotationConstants;
import eu.europeana.annotation.statistics.model.AnnotationMetric;
import eu.europeana.annotation.statistics.model.AnnotationStatisticsClientsScenarios;
import eu.europeana.annotation.statistics.model.AnnotationStatisticsScenarios;
import eu.europeana.annotation.statistics.model.AnnotationStatisticsUsersScenarios;

public class AnnotationStatisticsService {

  @Autowired
  @Qualifier(AnnotationConfiguration.BEAN_SOLR_ANNO_SERVICE)
  SolrAnnotationService solrService;

  Logger logger = LogManager.getLogger(getClass());

  public void getAnnotationsStatistics(AnnotationMetric annoMetric)
      throws AnnotationServiceException {
    // getting the annotations statistics for the scenarios
    setStatisticsPerScenario(annoMetric);

    // getting the annotations statistics for the users, total
    setStatsPerUser(annoMetric);

    // getting the annotations statistics for the clients, total
    setStatsPerClient(annoMetric);

    // getting the annotations statistics for the users, per scenario
    setStatsPerUserAndScenario(annoMetric);

    // getting the annotations statistics for the clients, per scenario
    setStatsPerClientAndScenario(annoMetric);
  }

  private void setStatsPerClientAndScenario(AnnotationMetric annoMetric)
      throws AnnotationServiceException {
    Map<String, Map<String, Long>> numAnnotations;
    numAnnotations =
        solrService.getStatisticsByFieldAndScenario(SolrAnnotationConstants.GENERATOR_URI);
    List<AnnotationStatisticsClientsScenarios> annotationStatisticsClientsScenarios =
        getStatisticsPerClientScenario(numAnnotations);
    annoMetric.setClientsScenarios(annotationStatisticsClientsScenarios);
  }

  private void setStatsPerUserAndScenario(AnnotationMetric annoMetric)
      throws AnnotationServiceException {
    Map<String, Map<String, Long>> numAnnotations =
        solrService.getStatisticsByFieldAndScenario(SolrAnnotationConstants.CREATOR_URI);
    List<AnnotationStatisticsUsersScenarios> annotationStatisticsUsersScenarios =
        getStatisticsPerUserScenario(numAnnotations);
    annoMetric.setUsersScenarios(annotationStatisticsUsersScenarios);
  }

  private void setStatsPerClient(AnnotationMetric annoMetric)
      throws AnnotationServiceException {
    QueryResponse annoFacetStatsJson;
    annoFacetStatsJson = solrService.getStatisticsByField(SolrAnnotationConstants.GENERATOR_URI);
    BucketBasedJsonFacet generatorBucketBasedFacets = annoFacetStatsJson.getJsonFacetingResponse()
        .getBucketBasedFacets(SolrAnnotationConstants.GENERATOR_URI);
    if (generatorBucketBasedFacets != null) {
      Map<String, Long> annotationStatisticsClients =
          facetBucketsToMap(generatorBucketBasedFacets.getBuckets());
      annoMetric.setClientsTotal(annotationStatisticsClients);
    }
  }

  private void setStatsPerUser(AnnotationMetric annoMetric) throws AnnotationServiceException {
    QueryResponse annoFacetStatsJson;
    annoFacetStatsJson = solrService.getStatisticsByField(SolrAnnotationConstants.CREATOR_URI);
    BucketBasedJsonFacet creatorBucketBasedFacets = annoFacetStatsJson.getJsonFacetingResponse()
        .getBucketBasedFacets(SolrAnnotationConstants.CREATOR_URI);
    if (creatorBucketBasedFacets != null) {
      Map<String, Long> annotationStatisticsUsers =
          facetBucketsToMap(creatorBucketBasedFacets.getBuckets());
      annoMetric.setUsersTotal(annotationStatisticsUsers);
    }
  }

  private void setStatisticsPerScenario(AnnotationMetric annoMetric)
      throws AnnotationServiceException {
    QueryResponse annoFacetStatsJson =
        solrService.getStatisticsByField(SolrAnnotationConstants.SCENARIO);
    BucketBasedJsonFacet scenarioBucketBasedFacets = annoFacetStatsJson.getJsonFacetingResponse()
        .getBucketBasedFacets(SolrAnnotationConstants.SCENARIO);
    if (scenarioBucketBasedFacets != null) {
      AnnotationStatisticsScenarios annotationStatisticsScenarios =
          getStatisticsPerScenario(scenarioBucketBasedFacets.getBuckets());
      annoMetric.setScenariosTotal(annotationStatisticsScenarios);
    }
  }

  private AnnotationStatisticsScenarios getStatisticsPerScenario(
      List<BucketJsonFacet> facetJsonBuckets) {
    AnnotationStatisticsScenarios annoStats = new AnnotationStatisticsScenarios();
    for (BucketJsonFacet facetJsonBucket : facetJsonBuckets) {
      switch (facetJsonBucket.getVal().toString()) {
        case AnnotationScenarioTypes.GEO_TAG:
          annoStats.setGeoTag(facetJsonBucket.getCount());
          break;
        case AnnotationScenarioTypes.TRANSCRIPTION:
          annoStats.setTranscription(facetJsonBucket.getCount());
          break;
        case AnnotationScenarioTypes.OBJECT_LINK:
          annoStats.setObjectLink(facetJsonBucket.getCount());
          break;
        case AnnotationScenarioTypes.SEMANTIC_TAG:
          annoStats.setSemanticTag(facetJsonBucket.getCount());
          break;
        case AnnotationScenarioTypes.SIMPLE_TAG:
          annoStats.setSimpleTag(facetJsonBucket.getCount());
          break;
        case AnnotationScenarioTypes.SUBTITLE:
          annoStats.setSubtitle(facetJsonBucket.getCount());
          break;
        case AnnotationScenarioTypes.CAPTION:
          annoStats.setCaption(facetJsonBucket.getCount());
          break;
        case AnnotationScenarioTypes.CONTRIBUTE_LINK:
          annoStats.setContributeLink(facetJsonBucket.getCount());
          break;

        default:
          logger.debug("Scenario not supported in statistics service: {}",
              facetJsonBucket.getVal());
          break;
      }
    }
    return annoStats;
  }

  private Map<String, Long> facetBucketsToMap(List<BucketJsonFacet> facetJsonBuckets) {
    if (facetJsonBuckets == null) {
      return null;
    }
    return facetJsonBuckets.stream()
        .collect(Collectors.toMap(f -> f.getVal().toString(), f -> Long.valueOf(f.getCount())));
    // res = new Map<String, Long>();
    // for (BucketJsonFacet facetJsonBucket : facetJsonBuckets) {
    // annoStatsClientsUsers.put(facetJsonBucket.getVal().toString(), facetJsonBucket.getCount());
    // }
  }

  private List<AnnotationStatisticsClientsScenarios> getStatisticsPerClientScenario(
      Map<String, Map<String, Long>> numAnnotations) {
    List<AnnotationStatisticsClientsScenarios> clientsScenarios =
        new ArrayList<AnnotationStatisticsClientsScenarios>();
    for (Map.Entry<String, Map<String, Long>> numAnnotationsEntry : numAnnotations.entrySet()) {
      AnnotationStatisticsClientsScenarios clientsScenariosElem =
          new AnnotationStatisticsClientsScenarios();
      clientsScenariosElem.setClient(numAnnotationsEntry.getKey());
      if (numAnnotationsEntry.getValue().get(AnnotationScenarioTypes.GEO_TAG) != null) {
        clientsScenariosElem.setGeoTag(
            numAnnotationsEntry.getValue().get(AnnotationScenarioTypes.GEO_TAG).longValue());
      }
      if (numAnnotationsEntry.getValue().get(AnnotationScenarioTypes.TRANSCRIPTION) != null) {
        clientsScenariosElem.setTranscription(
            numAnnotationsEntry.getValue().get(AnnotationScenarioTypes.TRANSCRIPTION).longValue());
      }
      if (numAnnotationsEntry.getValue().get(AnnotationScenarioTypes.OBJECT_LINK) != null) {
        clientsScenariosElem.setObjectLink(
            numAnnotationsEntry.getValue().get(AnnotationScenarioTypes.OBJECT_LINK).longValue());
      }
      if (numAnnotationsEntry.getValue().get(AnnotationScenarioTypes.SEMANTIC_TAG) != null) {
        clientsScenariosElem.setSemanticTag(
            numAnnotationsEntry.getValue().get(AnnotationScenarioTypes.SEMANTIC_TAG).longValue());
      }
      if (numAnnotationsEntry.getValue().get(AnnotationScenarioTypes.SIMPLE_TAG) != null) {
        clientsScenariosElem.setSimpleTag(
            numAnnotationsEntry.getValue().get(AnnotationScenarioTypes.SIMPLE_TAG).longValue());
      }
      if (numAnnotationsEntry.getValue().get(AnnotationScenarioTypes.SUBTITLE) != null) {
        clientsScenariosElem.setSubtitle(
            numAnnotationsEntry.getValue().get(AnnotationScenarioTypes.SUBTITLE).longValue());
      }
      if (numAnnotationsEntry.getValue().get(AnnotationScenarioTypes.CAPTION) != null) {
        clientsScenariosElem.setCaption(
            numAnnotationsEntry.getValue().get(AnnotationScenarioTypes.CAPTION).longValue());
      }
      if (numAnnotationsEntry.getValue().get(AnnotationScenarioTypes.CONTRIBUTE_LINK) != null) {
        clientsScenariosElem.setContributeLink(numAnnotationsEntry.getValue()
            .get(AnnotationScenarioTypes.CONTRIBUTE_LINK).longValue());
      }

      clientsScenarios.add(clientsScenariosElem);
    }
    return clientsScenarios;
  }

  private List<AnnotationStatisticsUsersScenarios> getStatisticsPerUserScenario(
      Map<String, Map<String, Long>> numAnnotations) {
    List<AnnotationStatisticsUsersScenarios> usersScenarios =
        new ArrayList<AnnotationStatisticsUsersScenarios>();
    for (Map.Entry<String, Map<String, Long>> numAnnotationsEntry : numAnnotations.entrySet()) {
      AnnotationStatisticsUsersScenarios usersScenariosElem =
          new AnnotationStatisticsUsersScenarios();
      usersScenariosElem.setUser(numAnnotationsEntry.getKey());
      if (numAnnotationsEntry.getValue().get(AnnotationScenarioTypes.GEO_TAG) != null) {
        usersScenariosElem.setGeoTag(
            numAnnotationsEntry.getValue().get(AnnotationScenarioTypes.GEO_TAG).longValue());
      }
      if (numAnnotationsEntry.getValue().get(AnnotationScenarioTypes.TRANSCRIPTION) != null) {
        usersScenariosElem.setTranscription(
            numAnnotationsEntry.getValue().get(AnnotationScenarioTypes.TRANSCRIPTION).longValue());
      }
      if (numAnnotationsEntry.getValue().get(AnnotationScenarioTypes.OBJECT_LINK) != null) {
        usersScenariosElem.setObjectLink(
            numAnnotationsEntry.getValue().get(AnnotationScenarioTypes.OBJECT_LINK).longValue());
      }
      if (numAnnotationsEntry.getValue().get(AnnotationScenarioTypes.SEMANTIC_TAG) != null) {
        usersScenariosElem.setSemanticTag(
            numAnnotationsEntry.getValue().get(AnnotationScenarioTypes.SEMANTIC_TAG).longValue());
      }
      if (numAnnotationsEntry.getValue().get(AnnotationScenarioTypes.SIMPLE_TAG) != null) {
        usersScenariosElem.setSimpleTag(
            numAnnotationsEntry.getValue().get(AnnotationScenarioTypes.SIMPLE_TAG).longValue());
      }
      if (numAnnotationsEntry.getValue().get(AnnotationScenarioTypes.SUBTITLE) != null) {
        usersScenariosElem.setSubtitle(
            numAnnotationsEntry.getValue().get(AnnotationScenarioTypes.SUBTITLE).longValue());
      }
      if (numAnnotationsEntry.getValue().get(AnnotationScenarioTypes.CAPTION) != null) {
        usersScenariosElem.setCaption(
            numAnnotationsEntry.getValue().get(AnnotationScenarioTypes.CAPTION).longValue());
      }
      if (numAnnotationsEntry.getValue().get(AnnotationScenarioTypes.CONTRIBUTE_LINK) != null) {
        usersScenariosElem.setContributeLink(numAnnotationsEntry.getValue()
            .get(AnnotationScenarioTypes.CONTRIBUTE_LINK).longValue());
      }

      usersScenarios.add(usersScenariosElem);
    }
    return usersScenarios;
  }
}
