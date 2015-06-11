package eu.europeana.annotation.web.context;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import org.junit.Test;
import org.springframework.web.context.support.MockServletEnvironment;
import org.springframework.web.context.support.StandardServletEnvironment;

public class VcapPropertyLoaderListenerTest {

	@Test
	public void writePropertiesToFile() throws FileNotFoundException, IOException {

		Long startTime = System.currentTimeMillis();
		System.out.println(startTime);
		
		// Properties europeanaProperties =
		// loadProperties("/generate-config/europeana-test.properties");
		String outFile = getClass().getResource(
				"/generate-config/annotation-test.properties").getFile();

		VcapAnnotationPropertyLoaderListener propertiesLoader = new VcapAnnotationPropertyLoaderListener(
				getMockServletEnvironment(), new File(outFile));
		
		System.out.println("Update properties in file:"	+	propertiesLoader.propertiesFile);
		Properties props = new Properties();
		props.load(new FileInputStream(propertiesLoader.propertiesFile));
		System.out.println(props);
		String timestamp = props.getProperty("annotation.properties.timestamp");
		assertTrue(Long.parseLong(timestamp) > startTime);
		

	}


	protected StandardServletEnvironment getMockServletEnvironment() {
		return new MockServletEnvironment();
	}

}
