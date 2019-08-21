package eu.europeana.annotation.web.context;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;
import org.springframework.web.context.support.MockA9sServletEnvironment;
import org.springframework.web.context.support.StandardServletEnvironment;

public class A9sVcapPropertyLoaderListenerTest {

	Logger logger = LogManager.getLogger(getClass());
	
	@Test
	public void writePropertiesToFile() throws FileNotFoundException, IOException {

		Long startTime = System.currentTimeMillis();
		logger.trace(startTime);
		
		// Properties europeanaProperties =
		String a9sTemplate = getClass().getResource(
				"/generate-config/annotation-test.a9s.properties.template").getFile();
		logger.debug("Test_template location: " + a9sTemplate);
		
		String testGenerateConfigFolder = getClass().getResource(
				"/generate-config").getFile();
		
		logger.debug("Test generate config folder: " + testGenerateConfigFolder);
		
		File a9sTemplateFile = new File(a9sTemplate);
		File a9sPropsFile = new File(a9sTemplateFile.getParentFile(),  "annotation-test.a9s.properties");
		logger.info("Generate properties to file: " + a9sPropsFile);
		
		BasePropertyLoaderListener propertiesLoader = new VcapAnnotationPropertyLoaderListener(
				getMockServletEnvironment(), a9sPropsFile, a9sTemplateFile, true);
		
		logger.debug("Load updated properties in file:"	+	propertiesLoader.getPropertiesFile());
		Properties props = new Properties();
		props.load(new FileInputStream(propertiesLoader.getPropertiesFile()));
		logger.info("Generated properties: " + props);
		String timestamp = props.getProperty("annotation.properties.timestamp");
		assertTrue(Long.parseLong(timestamp) >= startTime);
		

	}


	protected StandardServletEnvironment getMockServletEnvironment() {
		return new MockA9sServletEnvironment();
		
	}

}
