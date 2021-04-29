package eu.europeana.annotation.fulltext.subtitles;

import com.dotsub.converter.exception.FileFormatException;
import com.dotsub.converter.importer.SubtitleImportHandler;
import com.dotsub.converter.importer.impl.DfxpImportHandler;
import com.dotsub.converter.model.Configuration;
import com.dotsub.converter.model.SubtitleItem;

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class DfxpImportHandlerTest extends SubtitleConverterTests {

    private SubtitleImportHandler dfxpImportHandler = new DfxpImportHandler();

    @Test
    public void testImportDfxpFile() throws Exception {

        List<SubtitleItem> subtitleItemList = dfxpImportHandler.importFile(getFile("Autumn_hats.fr.dfxp"), new Configuration());
        Assertions.assertEquals(28, subtitleItemList.size());
    }    
    
    @Test
    public void testImportFileWrongFormat() throws Exception {
        Assertions.assertThrows(FileFormatException.class, () -> {
            	dfxpImportHandler.importFile(getFile("Autumn_hats.fr.vtt"), new Configuration());
            });
    }
    
}
