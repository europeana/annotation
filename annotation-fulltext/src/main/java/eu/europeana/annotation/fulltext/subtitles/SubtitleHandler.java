package eu.europeana.annotation.fulltext.subtitles;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;

import com.dotsub.converter.exception.FileFormatException;
import com.dotsub.converter.importer.SubtitleImportHandler;
import com.dotsub.converter.importer.impl.DfxpImportHandler;
import com.dotsub.converter.importer.impl.QtTextImportHandler;
import com.dotsub.converter.importer.impl.SbvImportHandler;
import com.dotsub.converter.importer.impl.SccImportHandler;
import com.dotsub.converter.importer.impl.SrtImportHandler;
import com.dotsub.converter.importer.impl.SsaImportHandler;
import com.dotsub.converter.importer.impl.StlImportHandler;
import com.dotsub.converter.importer.impl.WebVttImportHandler;
import com.dotsub.converter.model.Configuration;
import com.dotsub.converter.model.SubtitleItem;

public class SubtitleHandler {
	private static final String VTT_FORMAT = "vtt";
	private static final String STL_FORMAT = "stl";
	private static final String DFXP_FORMAT = "dfxp";
	private static final String QT_FORMAT = "qt";
	private static final String SBV_FORMAT = "sbv";
	private static final String SCC_FORMAT = "scc";
	private static final String SRT_FORMAT = "srt";
	private static final String SSA_FORMAT = "ssa";
	
    private SubtitleImportHandler QT_HANDLER;
    private SubtitleImportHandler SRT_HANDLER;
    private SubtitleImportHandler STL_HANDLER;
    private SubtitleImportHandler DFXP_HANDLER;
    private SubtitleImportHandler SSA_HANDLER;
    private SubtitleImportHandler VTT_HANDLER;
    private SubtitleImportHandler SBV_HANDLER;
    private SubtitleImportHandler SCC_HANDLER;
	
	public List<SubtitleItem> parseSubtitle (String text, String format) {
		List<SubtitleItem> items = new ArrayList<SubtitleItem>();
		switch(format) {
		  case QT_FORMAT:
			  if (QT_HANDLER==null) {
				  QT_HANDLER = new QtTextImportHandler();
			  }
			  QT_HANDLER.parseSubtitle(items, IOUtils.lineIterator(new StringReader(text)), new Configuration());		    
			  break;
		  case SRT_FORMAT:
			  if (SRT_HANDLER==null) {
				SRT_HANDLER = new SrtImportHandler();
			  }
			  SRT_HANDLER.parseSubtitle(items, IOUtils.lineIterator(new StringReader(text)), new Configuration());
			  break;
		  case STL_FORMAT:
		      if (STL_HANDLER==null) {
		    	  STL_HANDLER = new StlImportHandler();
		      }
		      STL_HANDLER.parseSubtitle(items, IOUtils.lineIterator(new StringReader(text)), new Configuration());
		      break;
		  case DFXP_FORMAT:
		      if (DFXP_HANDLER==null) {
		    	  DFXP_HANDLER = new DfxpImportHandler();
		      }
		      DFXP_HANDLER.parseSubtitle(items, IOUtils.lineIterator(new StringReader(text)), new Configuration());
		      break;
		  case SSA_FORMAT:
		      if (SSA_HANDLER==null) {
		    	  SSA_HANDLER = new SsaImportHandler();
		      }
		      SSA_HANDLER.parseSubtitle(items, IOUtils.lineIterator(new StringReader(text)), new Configuration());
		      break;
		  case VTT_FORMAT:
		      if (VTT_HANDLER==null) {
		    	  VTT_HANDLER = new WebVttImportHandler();
		      }
		      VTT_HANDLER.parseSubtitle(items, IOUtils.lineIterator(new StringReader(text)), new Configuration());
		      break;
		  case SBV_FORMAT:
			  if (SBV_HANDLER==null) {
				  SBV_HANDLER = new SbvImportHandler();
			  }
			  SBV_HANDLER.parseSubtitle(items, IOUtils.lineIterator(new StringReader(text)), new Configuration());
			  break;
		  case SCC_FORMAT:
		      if (SCC_HANDLER==null) {
		    	  SCC_HANDLER = new SccImportHandler();
		    	  SCC_HANDLER.parseSubtitle(items, IOUtils.lineIterator(new StringReader(text)), new Configuration());
		      }
		      break;
		  default:
			  throw new FileFormatException("File does not match any of the expected formats: "+ 
				  QT_FORMAT+","+SRT_FORMAT+","+STL_FORMAT+","+DFXP_FORMAT+","+SSA_FORMAT+","+VTT_FORMAT+","+SBV_FORMAT+","+SCC_FORMAT+".");
		}		
		return items;
	}
}
