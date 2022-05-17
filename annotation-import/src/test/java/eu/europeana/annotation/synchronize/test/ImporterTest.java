package eu.europeana.annotation.synchronize.test;

import eu.europeana.annotation.synchronize.AnnotationSynchronizer;
import eu.europeana.annotation.synchronize.BaseAnnotationSynchronizer;

public class ImporterTest{

  //@Test
  public void testUpdateInEntityAPI() throws Exception{
    
    AnnotationSynchronizer annoSync = new AnnotationSynchronizer();
    annoSync.init();
    annoSync.run(BaseAnnotationSynchronizer.parseDate("2019-11-22 10:44:30"));
    //assertTrue(importer.getEntitySolrImporter().exists(BNF_ENTITY_ID_URI));
  }
  
 
  
}
