package eu.europeana.annotation.fulltext.subtitles;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;

import com.dotsub.converter.exception.FileFormatException;
import com.dotsub.converter.importer.SubtitleImportHandler;
import com.dotsub.converter.model.Configuration;
import com.dotsub.converter.model.SubtitleItem;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import eu.europeana.annotation.definitions.exception.AnnotationServiceInstantiationException;
import eu.europeana.annotation.definitions.model.impl.SubtitleFormat;
import eu.europeana.annotation.definitions.model.impl.SubtitleFormats;

public class SubtitleHandler {

    static final String SUBTITLE_FORMATS_FILE = "/subtitle-formats.xml";
    Map<String, SubtitleImportHandler> subtitleHandlers = new HashMap<String, SubtitleImportHandler>();

    public SubtitleHandler(ArrayList<SubtitleImportHandler> subtitleImportHandlersParam) throws IOException {
	initSubtitleHandlers();
    }

    void initSubtitleHandlers() {
	try (InputStream inputStream = getClass().getResourceAsStream(SUBTITLE_FORMATS_FILE)) {
	    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
	    String contents = reader.lines().collect(Collectors.joining(System.lineSeparator()));
	    SubtitleFormats subtitlesFormats = (new XmlMapper()).readValue(contents, SubtitleFormats.class);
	    for (SubtitleFormat format : subtitlesFormats.getFormats()) {
		SubtitleImportHandler importHandler = (SubtitleImportHandler) Class.forName(format.getHandler()).getDeclaredConstructor().newInstance();
		subtitleHandlers.put(format.getMimetype(), importHandler);
	    }
	}catch (Exception e) {
	    throw new AnnotationServiceInstantiationException("SubtitleHandler", e);
	}
    }

    public List<SubtitleItem> parseSubtitle(String text, String format) throws FileFormatException, IOException {
	if (StringUtils.isBlank(text)) {
	    return null;
	}

	SubtitleImportHandler subtitleImportHandler = getSubtitleHandlers().get(format);
	if (subtitleImportHandler == null) {
	    throw new FileFormatException("Subtitle format not supported: " + format);
	}

	InputStream stream = new ByteArrayInputStream(text.getBytes(StandardCharsets.UTF_8));
	return subtitleImportHandler.importFile(stream, new Configuration());
    }


    public boolean hasSubtitleFormat(String format) {
	return subtitleHandlers.containsKey(format);
    }

    Map<String, SubtitleImportHandler> getSubtitleHandlers() {
        return subtitleHandlers;
    }

}
