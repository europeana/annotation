The class eu.europeana.annotation.synchronize.AnnotationSynchronizer.java contains a main method. 
The main method requires the optional parameter for importing only annotations created or modified after the provided date formatted as ISO 8601 format (e.g. "2019-11-22T10:44:30.620Z"). 
This class performs the synchronization of annotations with the fulltext Solr index in the following way:
 * It checks all annotations that are created, modified, or deleted after the given provided date. The annotations are retrieved from the annotation API using 
 the Java client embedded through the .jar file as a maven dependency
 * All annotations that have the same resourceId (belong to the same europeana item) are grouped together and their metadata is fetched from the metadata API using 
 the Java client embedded through the .jar file as a maven dependency
 * Those annotations that do not have the corresponding metadata are deleted from the fulltext index, as well as all the annotations that are marked as deleted in the annotation API
 and those that have the metadata are updated correspondingly in the fulltext        

Please note that the required .jar file for the fulltext API needs to be uploaded to the .m2 maven repository where the application is running. Also please note to configure the metadata and fulltext Solr indexes
in the cloud mode. For more details about the configurations, please refer to the Documentation.docx file.