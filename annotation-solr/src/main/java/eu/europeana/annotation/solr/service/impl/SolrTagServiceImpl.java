package eu.europeana.annotation.solr.service.impl;

import java.io.IOException;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.response.UpdateResponse;

import eu.europeana.annotation.solr.exceptions.TagServiceException;
import eu.europeana.annotation.solr.model.internal.SolrTag;
import eu.europeana.annotation.solr.model.internal.SolrTagImpl;
import eu.europeana.annotation.solr.service.SolrTagService;

public class SolrTagServiceImpl implements SolrTagService{

	SolrServer solrServer;

	public void setSolrServer(SolrServer solrServer) {
		this.solrServer = solrServer;
	}

	@Override
	public void store(SolrTag solrTag) {
	    try {
	    	Logger.getLogger(getClass().getName()).info("store: " + solrTag.getCreator());
	    	System.out.println("store: " + solrTag.getCreator());
	        UpdateResponse rsp = solrServer.addBean(solrTag);
	        System.out.println("update response: " + rsp.toString());
	        solrServer.commit();
	    } catch (SolrServerException ex) {
	        Logger.getLogger(getClass().getName()).error(ex.getMessage());
	        System.out.println("update error (SolrServer): " + ex.getMessage());
	    } catch (IOException ex) {
	        Logger.getLogger(getClass().getName()).error(ex.getMessage());
	        System.out.println("update error (IOException): " + ex.getMessage());
	    }		
	}

	@Override
	public List<? extends SolrTag> search(String term) throws TagServiceException {
			
			List<? extends SolrTag> res = null;
			
		    /**
		     * Construct a SolrQuery 
		     */
		    SolrQuery query = new  SolrQuery(term );
		    
		    /**
		     * Query the server 
		     */
		    try {
		    	QueryResponse rsp =  solrServer.query( query );
		    	res = rsp.getBeans(SolrTagImpl.class);
			} catch (SolrServerException e) {
				throw new TagServiceException("Unexpected exception occured when searching tags for: " + term, e);
	        }
		    
		    return res;
		}

	@Override
	public List<? extends SolrTag> search(SolrTag queryObject) {
		return null;
	}

	@Override
	public List<? extends SolrTag> searchByLabel(String searchTerm) {
		return null;
	}

}
