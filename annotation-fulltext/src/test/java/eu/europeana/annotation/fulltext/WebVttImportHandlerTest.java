package eu.europeana.annotation.fulltext;

import java.io.InputStream;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.dotsub.converter.exception.FileFormatException;
import com.dotsub.converter.importer.SubtitleImportHandler;
import com.dotsub.converter.importer.impl.WebVttImportHandler;
import com.dotsub.converter.model.Configuration;
import com.dotsub.converter.model.SubtitleItem;

class WebVttImportHandlerTest extends FulltextTests {

    private SubtitleImportHandler webVttImportHandler = new WebVttImportHandler();

    @Test
    void testImportVttFile() throws Exception {
        List<SubtitleItem> subtitleItemList = webVttImportHandler.importFile(getFile("Autumn_hats.fr.vtt"), new Configuration());
        Assertions.assertEquals(28, subtitleItemList.size());

    }

    @Test
    void testImportFileWrongFormat() throws Exception {
    	InputStream inputFile = getFile("Autumn_hats.fr.srt");
        Assertions.assertThrows(FileFormatException.class, () -> {
        	webVttImportHandler.importFile(inputFile, new Configuration());
        });
    }
}
