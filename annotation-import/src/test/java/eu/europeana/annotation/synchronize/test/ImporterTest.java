package eu.europeana.annotation.synchronize.test;

import org.junit.jupiter.api.Test;

import eu.europeana.annotation.synchronize.AnnotationSynchronizer;
import junit.framework.TestCase;

public class ImporterTest extends TestCase {

  @Test
  public void testUpdateInEntityAPI() throws Exception{
    
    AnnotationSynchronizer annoSync = new AnnotationSynchronizer();
    annoSync.init();
    annoSync.run("2019-11-22T10:44:30.620Z");
    //assertTrue(importer.getEntitySolrImporter().exists(BNF_ENTITY_ID_URI));
  }
  
 
  
}
