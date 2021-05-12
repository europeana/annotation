package eu.europeana.annotation.fulltext.subtitles;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import com.dotsub.converter.exception.FileFormatException;
import com.dotsub.converter.importer.SubtitleImportHandler;
import com.dotsub.converter.importer.impl.DfxpImportHandler;
import com.dotsub.converter.importer.impl.QtTextImportHandler;
import com.dotsub.converter.importer.impl.WebVttImportHandler;
import com.dotsub.converter.model.Configuration;
import com.dotsub.converter.model.SubtitleItem;

public class SubtitleHandler {
	
	private static final String INTERNET_MEDIA_TYPE_VTT_FORMAT = "text/vtt";
	private static final String INTERNET_MEDIA_TYPE_DFXP_FORMAT = "application/ttml+xml";
	private static final String INTERNET_MEDIA_TYPE_QT_FORMAT = "video/quicktime";
	
    private static final SubtitleImportHandler QT_HANDLER = new QtTextImportHandler();
    private static final SubtitleImportHandler DFXP_HANDLER = new DfxpImportHandler();
    private static final SubtitleImportHandler VTT_HANDLER = new WebVttImportHandler();
	
	public List<SubtitleItem> parseSubtitle (String text, String format) throws FileFormatException, IOException {
		
		InputStream stream = new ByteArrayInputStream(text.getBytes(StandardCharsets.UTF_8));		
		List<SubtitleItem> items = new ArrayList<SubtitleItem>();
		switch(format) {
		  case INTERNET_MEDIA_TYPE_QT_FORMAT:
			  QT_HANDLER.importFile(stream, new Configuration());		    
			  break;
		  case INTERNET_MEDIA_TYPE_DFXP_FORMAT:
		      DFXP_HANDLER.importFile(stream, new Configuration());
		      break;
		  case INTERNET_MEDIA_TYPE_VTT_FORMAT:
		      VTT_HANDLER.importFile(stream, new Configuration());
		      break;
		  default:
			  throw new FileFormatException("The Internet media subtitle format does not match any of the expected formats: "+ 
				  INTERNET_MEDIA_TYPE_QT_FORMAT+","+INTERNET_MEDIA_TYPE_DFXP_FORMAT+","+INTERNET_MEDIA_TYPE_VTT_FORMAT+".");
		}		
		return items;
	}
}
