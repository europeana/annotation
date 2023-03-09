package eu.europeana.annotation.fulltext.transcription;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import eu.europeana.annotation.definitions.model.impl.MediaFormat;

public class XsdSources implements AutoCloseable {

  private final MediaFormat mediaFormat;
  List<InputStream> xsdLocationStreams;
  Source[] xsdSources;
  
  protected Logger logger = LogManager.getLogger(XsdSources.class);

  public XsdSources(MediaFormat mediaFormat) {
    this.mediaFormat = mediaFormat;
    initSources();
  }

  protected void initSources() {
    String[] xsdLocations = mediaFormat.getValidationResource().split(",");
    xsdLocationStreams = new ArrayList<>(xsdLocations.length);
    xsdSources = new Source[xsdLocations.length];
    InputStream xsdLocationStream;
    for (int i = 0; i < xsdLocations.length; i++) {
      xsdLocationStream = getClass().getResourceAsStream(xsdLocations[i]);
      xsdLocationStreams.add(xsdLocationStream);
      xsdSources[i] = new StreamSource(xsdLocationStream);
    }
  }


  @Override
  public void close() {
    for (InputStream xsdLocationStream : xsdLocationStreams) {
      try{
        xsdLocationStream.close();
      }catch (Exception e) {
        logger.warn("Cannot close input stream for xsd schema!", e);
      }
    }
  }

  public Source[] getXsdSources() {
    return xsdSources;
  }



}
