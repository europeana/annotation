package eu.europeana.annotation.fulltext;

import java.io.InputStream;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.dotsub.converter.exception.FileFormatException;
import com.dotsub.converter.importer.SubtitleImportHandler;
import com.dotsub.converter.importer.impl.SrtImportHandler;
import com.dotsub.converter.model.Configuration;
import com.dotsub.converter.model.SubtitleItem;

class SrtImportHandlerTest extends FulltextTests {

    private SubtitleImportHandler srtImportHandler = new SrtImportHandler();

    @Test
    void testImportSrtFile() throws Exception {
        List<SubtitleItem> subtitleItemList = srtImportHandler.importFile(getFile("Autumn_hats.fr.srt"), new Configuration());
        Assertions.assertEquals(28, subtitleItemList.size());

        //mock item
        SubtitleItem mockItem = new SubtitleItem(0, 5780, "(musique)");
        //check an item in the file.
        SubtitleItem subtitleItem = subtitleItemList.get(0);
        Assertions.assertEquals(mockItem.getStartTime(), subtitleItem.getStartTime());
        Assertions.assertEquals(mockItem.getDuration(), subtitleItem.getDuration());
        Assertions.assertEquals(mockItem.getContent(), subtitleItem.getContent());
        Assertions.assertNotNull(subtitleItem.toString());
        Assertions.assertEquals(mockItem, subtitleItem);
        //make sure the hours were properly dropped
        subtitleItemList.forEach(
                item -> Assertions.assertTrue(item.getStartTime() <= 69660)
        );
    }

    @Test
    void testImportFileWrongFormat() throws Exception {
    	InputStream inputFile = getFile("Autumn_hats.fr.vtt");
        Assertions.assertThrows(FileFormatException.class, () -> {
        	srtImportHandler.importFile(inputFile, new Configuration());
        });
    }
}
