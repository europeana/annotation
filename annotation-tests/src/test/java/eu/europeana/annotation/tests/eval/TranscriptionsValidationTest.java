package eu.europeana.annotation.tests.eval;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.HeadMethod;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import eu.europeana.annotation.tests.config.AnnotationTestsConfiguration;

@SpringBootTest
@AutoConfigureMockMvc
public class TranscriptionsValidationTest {
  
    Logger log = LogManager.getLogger(getClass());
    
    HttpClient httpClient = new HttpClient();
    File outputFile = new File("/tmp/transcriptionseval.csv");

    //commented out until the input files are provided
    //@Test
    public void validateTranscriptionsTest() throws Exception{
	
	//read file 
	File transcriptionsFile = new File(getClass().getResource("/transcriptions.csv").getFile());
	outputFile.getParentFile().mkdirs();
	List<String> entries = FileUtils.readLines(transcriptionsFile, "UTF8");
	String[] values;
	String csvLine;
	int count = 0;
	writeToOutputFile("anno_uri,target_uri.source, sourceHttpCode, target_uri.scope, scopeHttpCode");
	for (String entry : entries) {
	    values = entry.split(",");
	    if(count == 0){
		//ignore header
		count++;
		continue;
	    }
	    csvLine = validateValues(values);
	    writeToOutputFile(csvLine);
	    count++;
	    if(count % 100 == 0) {
		log.debug("Processed items: " + count);
	    }
	}
    }

    private void writeToOutputFile(String csvLine) throws IOException {
	// TODO Auto-generated method stub
	FileUtils.writeStringToFile(outputFile, csvLine+"\n", StandardCharsets.UTF_8, true);
    }

    private String validateValues(String[] values) throws Exception {
	String annoId = values[0];
	String scope="";//europeana item
	String source="";//image
	int sourceHttpCode=-1;
	int scopeHttpCode=-1;
	if(values.length > 2) {
	    source = values[1];
	    scope = values[2];
//	    scope = scope.replace("http:", "https:");
	}else if(values.length == 2){
	    scope = values[1];
//	    scope = scope.replace("http:", "https:");
	}
	
	assertTrue(scope.startsWith(AnnotationTestsConfiguration.getInstance().getPropAnnotationItemDataEndpoint()) || scope.startsWith("https://www.europeana.eu/"));
	assertFalse(source.startsWith(AnnotationTestsConfiguration.getInstance().getPropAnnotationItemDataEndpoint()));
	
	sourceHttpCode = getHttpCode(source, false);
	scopeHttpCode = getHttpCode(scope, true);
		
	//return CSV line
	return annoId + "," + source +"," + sourceHttpCode+ "," + scope + "," + scopeHttpCode;		
    }

    private int getHttpCode(String url, boolean addAccept) throws HttpException, IOException {
	if(StringUtils.isEmpty(url)) {
	    return 404;
	}
	HeadMethod method = new HeadMethod(url);
	method.setFollowRedirects(true);
	if(addAccept) {
	    method.addRequestHeader(new Header("Accept", "application/json"));
	}
	int code = httpClient.executeMethod(method);
	
//	String redirection = method.getResponseHeader("Location");
//	log.debug("redirection: " + redirection);
	return code;
    } 
}
