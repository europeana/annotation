package eu.europeana.annotation.fulltext.subtitles;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import com.dotsub.converter.exception.FileFormatException;
import com.dotsub.converter.importer.SubtitleImportHandler;
import com.dotsub.converter.model.Configuration;
import com.dotsub.converter.model.SubtitleItem;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import eu.europeana.annotation.config.SubtitleFormat;
import eu.europeana.annotation.config.SubtitleFormats;

@Component("subtitleHandler")
public class SubtitleHandler {
	
	public SubtitleHandler(String subtitleFormatsXMLConfigFile, XmlMapper xmlJacksonMapper) throws IOException {
		try (InputStream inputStream = getClass().getResourceAsStream(subtitleFormatsXMLConfigFile);
	    		BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
	    	    String contents = reader.lines().collect(Collectors.joining(System.lineSeparator()));
	    	    subtitlesFormats = xmlJacksonMapper.readValue(contents, SubtitleFormats.class);  
    	}
	}

	SubtitleFormats subtitlesFormats;
	
	public List<SubtitleItem> parseSubtitle (String text, String format) throws FileFormatException, IOException, ClassNotFoundException, NoSuchMethodException, SecurityException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		if (StringUtils.isBlank(text)) {
			return null;
		}		
				
		Optional<SubtitleFormat> subtitleFormat = subtitlesFormats.getFormats().stream().filter(s -> format.equals(s.getMimetype())).findFirst();
		if (subtitleFormat.isEmpty()) return null;
		
		Class<?> subtitleHandlerClass = Class.forName(subtitleFormat.get().getHandler());
		Constructor<?> constructorSubtitleHandlerClass = subtitleHandlerClass.getConstructor();
		SubtitleImportHandler subtitleImportHandler = (SubtitleImportHandler) constructorSubtitleHandlerClass.newInstance(new Object[] {});
		InputStream stream = new ByteArrayInputStream(text.getBytes(StandardCharsets.UTF_8));
		subtitleImportHandler.importFile(stream, new Configuration());
		
		List<SubtitleItem> items = new ArrayList<SubtitleItem>();
		
		return items;
	}
	
	public boolean hasSubtitleFormat(String format) {
		return subtitlesFormats.getFormats().stream().anyMatch(s -> format.equals(s.getMimetype()));
	}
	
}
