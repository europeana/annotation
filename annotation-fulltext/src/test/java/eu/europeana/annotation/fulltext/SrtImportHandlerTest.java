package eu.europeana.annotation.fulltext;

import com.dotsub.converter.exception.FileFormatException;
import com.dotsub.converter.importer.SubtitleImportHandler;
import com.dotsub.converter.importer.impl.SrtImportHandler;
import com.dotsub.converter.model.Configuration;
import com.dotsub.converter.model.SubtitleItem;

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class SrtImportHandlerTest extends FulltextTests {

    private SubtitleImportHandler srtImportHandler = new SrtImportHandler();

    @Test
    public void testImportSrtFile() throws Exception {
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
        Assertions.assertNotNull(subtitleItem.hashCode());
        Assertions.assertEquals(mockItem, subtitleItem);
        //make sure the hours were properly dropped
        subtitleItemList.forEach(
                item -> Assertions.assertTrue(item.getStartTime() <= 69660)
        );
    }

    @Test
    public void testImportFileWrongFormat() throws Exception {
        Assertions.assertThrows(FileFormatException.class, () -> {
        	srtImportHandler.importFile(getFile("Autumn_hats.fr.vtt"), new Configuration());
            });
    }
}
