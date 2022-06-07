package eu.europeana.annotation.web;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import eu.europeana.annotation.AbstractIntegrationTest;

public class BaseWebControllerTest extends AbstractIntegrationTest {

  protected MockMvc mockMvc;

  @Autowired private WebApplicationContext webApplicationContext;

  @BeforeEach
  protected void setup() throws Exception {
    this.mockMvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext).build();
  }
  
  @Test
  void retrieveConceptJsonExternalShouldBeSuccessful() throws Exception {
    
    ResultActions results =
        mockMvc
            .perform(
                MockMvcRequestBuilders.post("/annotation/")
                    .content(loadFile("/tag/standard.json")))
            .andExpect(status().isCreated());

  }


  protected static String loadFile(String resourcePath) throws IOException {
    InputStream is = BaseWebControllerTest.class.getResourceAsStream(resourcePath);
    assert is != null;
    return IOUtils.toString(is, StandardCharsets.UTF_8).replace("\n", "");
  }
}
