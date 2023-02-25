package eu.europeana.annotation.fulltext;

import java.io.InputStream;

public abstract class FulltextTests {

    protected InputStream getFile(String fileName) {
        ClassLoader classLoader = getClass().getClassLoader();
        return classLoader.getResourceAsStream(fileName);
    }
}
