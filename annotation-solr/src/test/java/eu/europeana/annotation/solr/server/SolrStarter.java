package eu.europeana.annotation.solr.server;

import java.net.MalformedURLException;
import java.util.Properties;

import org.apache.solr.client.solrj.SolrClient;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @author Willem-Jan Boogerd <www.eledge.net/contact>
 */
public class SolrStarter {

	SolrClient solrClient;

	ApplicationContext context;

	Properties props;

	public SolrStarter() {
		this(new ClassPathXmlApplicationContext(new String[] { "annotation-solr-context.xml"}));
	}

	@SuppressWarnings("resource")
	public SolrStarter(ApplicationContext context) {
		context = new ClassPathXmlApplicationContext(
				new String[] {"annotation-solr-context.xml"});
		props = context.getBean("europeanaProperties", Properties.class);
	}

	public void start() throws MalformedURLException {
		//org.apache.solr.client.solrj.embedded.EmbeddedSolrServer
		//server = context.getBean(org.apache.solr.client.solrj.SolrServer.class);
		solrClient = context.getBean(org.apache.solr.client.solrj.embedded.EmbeddedSolrServer.class);
	}

	public static void main(String[] args) throws Exception {
		SolrStarter starter = new SolrStarter();
		starter.start();

	}

}
