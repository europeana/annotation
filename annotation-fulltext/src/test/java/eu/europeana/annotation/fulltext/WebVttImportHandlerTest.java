package eu.europeana.annotation.fulltext;

import com.dotsub.converter.exception.FileFormatException;
import com.dotsub.converter.importer.SubtitleImportHandler;
import com.dotsub.converter.importer.impl.WebVttImportHandler;
import com.dotsub.converter.model.Configuration;
import com.dotsub.converter.model.SubtitleItem;

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class WebVttImportHandlerTest extends FulltextTests {

    private SubtitleImportHandler webVttImportHandler = new WebVttImportHandler();

    @Test
    public void testImportVttFile() throws Exception {
        List<SubtitleItem> subtitleItemList = webVttImportHandler.importFile(getFile("Autumn_hats.fr.vtt"), new Configuration());
        Assertions.assertEquals(28, subtitleItemList.size());

    }

    @Test
    public void testImportFileWrongFormat() throws Exception {
        Assertions.assertThrows(FileFormatException.class, () -> {
        	webVttImportHandler.importFile(getFile("Autumn_hats.fr.srt"), new Configuration());
            });
    }
}
