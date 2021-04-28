package eu.europeana.annotation.fulltext.subtitles;

import java.io.InputStream;

public abstract class SubtitleConverterTests {

    protected InputStream getFile(String fileName) {
        ClassLoader classLoader = getClass().getClassLoader();
        return classLoader.getResourceAsStream(fileName);
    }
}
