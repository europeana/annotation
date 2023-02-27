package eu.europeana.annotation.fulltext;

import java.io.InputStream;

abstract class FulltextTests {

    protected InputStream getFile(String fileName) {
        ClassLoader classLoader = getClass().getClassLoader();
        return classLoader.getResourceAsStream(fileName);
    }
}
