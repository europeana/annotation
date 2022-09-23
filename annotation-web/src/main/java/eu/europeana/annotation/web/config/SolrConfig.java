package eu.europeana.annotation.web.config;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.impl.LBHttpSolrClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import eu.europeana.annotation.config.AnnotationConfiguration;

@Configuration
@PropertySource(
    value = {"classpath:config/annotation.properties", "classpath:config/annotation.user.properties"},
    ignoreResourceNotFound = true)
public class SolrConfig {

  private static final Logger logger = LogManager.getLogger(SolrConfig.class);
  
  @Value("${solr.annotation.url}")
  private String solrUrl;

  @Value("${solr.annotation.timeout}")
  private int solrTimeout;

  @Bean(AnnotationConfiguration.BEAN_ANNO_SOLR_CLIENT)
  public SolrClient annoSolrClient() {
    logger.info("Configuring annotation solr client at the url: {}", solrUrl);
    if (solrUrl.contains(",")) {
      LBHttpSolrClient.Builder builder = new LBHttpSolrClient.Builder();
      return builder
          .withBaseSolrUrls(solrUrl.split(","))
          .withConnectionTimeout(solrTimeout)
          .build();
    } else {
      HttpSolrClient.Builder builder = new HttpSolrClient.Builder();
      return builder
          .withBaseSolrUrl(solrUrl)
          .withConnectionTimeout(solrTimeout)
          .build();
    }
  }
}
