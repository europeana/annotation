package eu.europeana.annotation.fulltext;

import java.io.InputStream;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.dotsub.converter.exception.FileFormatException;
import com.dotsub.converter.importer.SubtitleImportHandler;
import com.dotsub.converter.importer.impl.DfxpImportHandler;
import com.dotsub.converter.model.Configuration;
import com.dotsub.converter.model.SubtitleItem;

class DfxpImportHandlerTest extends FulltextTests {

    private SubtitleImportHandler dfxpImportHandler = new DfxpImportHandler();

    @Test
    void testImportDfxpFile() throws Exception {

        List<SubtitleItem> subtitleItemList = dfxpImportHandler.importFile(getFile("Autumn_hats.fr.dfxp"), new Configuration());
        Assertions.assertEquals(28, subtitleItemList.size());
    }    
    
    @Test
    void testImportFileWrongFormat() throws Exception {
    	InputStream inputFile = getFile("Autumn_hats.fr.vtt");
        Assertions.assertThrows(FileFormatException.class, () -> {
        		dfxpImportHandler.importFile(inputFile, new Configuration());
            });
    }
    
}
