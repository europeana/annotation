package eu.europeana.annotation.solr.server;

import java.net.MalformedURLException;
import java.util.Properties;

import org.apache.solr.client.solrj.SolrServer;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @author Willem-Jan Boogerd <www.eledge.net/contact>
 */
public class SolrStarter {

	SolrServer server;

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
		server = context.getBean(org.apache.solr.client.solrj.SolrServer.class);
		
	}

	public static void main(String[] args) throws Exception {
		SolrStarter starter = new SolrStarter();
		starter.start();

	}

}
