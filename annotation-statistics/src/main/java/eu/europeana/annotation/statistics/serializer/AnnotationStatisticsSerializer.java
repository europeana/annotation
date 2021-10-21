package eu.europeana.annotation.statistics.serializer;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Locale;

import com.fasterxml.jackson.databind.ObjectMapper;

import eu.europeana.annotation.statistics.model.AnnotationMetric;

public class AnnotationStatisticsSerializer {

    ObjectMapper mapper = new ObjectMapper();

    public static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss'Z'";
    
    public AnnotationStatisticsSerializer() {
    	SimpleDateFormat df = new SimpleDateFormat(DATE_FORMAT, Locale.ENGLISH);
    	mapper.setDateFormat(df);
    }

    /**
     * This method provides full serialization of a Metric View (Usage stats results)
     *
     * @param annoStats
     * @return the annotation serialization
     * @throws IOException
     */
    public String serialize(AnnotationMetric annoStats) throws IOException {
        return mapper.writer().writeValueAsString(annoStats);
    }



}