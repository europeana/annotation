package eu.europeana.annotation.tests;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URLEncoder;
import org.apache.commons.lang3.StringUtils;
import org.apache.stanbol.commons.exception.JsonParseException;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import eu.europeana.annotation.definitions.model.Annotation;
import eu.europeana.annotation.definitions.model.search.result.AnnotationPage;
import eu.europeana.annotation.definitions.model.vocabulary.MotivationTypes;
import eu.europeana.annotation.definitions.model.vocabulary.WebAnnotationFields;
import eu.europeana.annotation.tests.config.AnnotationTestsConfiguration;
import eu.europeana.annotation.tests.constants.AnnotationTestsConstants;
import eu.europeana.annotation.utils.parse.AnnotationLdParser;
import eu.europeana.annotation.utils.parse.AnnotationPageParser;

public class AnnotationTestUtils {

  public static String getJsonStringInput(String resource) throws IOException {
      InputStream resourceAsStream = AnnotationTestUtils.class.getResourceAsStream(resource);
      StringBuilder out = new StringBuilder();
      BufferedReader br = new BufferedReader(new InputStreamReader(resourceAsStream));
      for (String line = br.readLine(); line != null; line = br.readLine())
          out.append(line);
      br.close();
      return out.toString();
  }

  public static ResponseEntity<String> buildResponseEntity(ResultActions mockMvcResult) throws IOException {
    
    MockHttpServletResponse mockHttpResponse = mockMvcResult.andReturn().getResponse();
    MultiValueMap<String, String> headers = new LinkedMultiValueMap<String, String>(15);
    for (String headerName : mockHttpResponse.getHeaderNames()) {
          headers.add(headerName, mockHttpResponse.getHeader(headerName));
    }

    ResponseEntity<String> responseEntity = new ResponseEntity<String>(
            mockHttpResponse.getContentAsString()
            , headers
            , HttpStatus.valueOf(mockHttpResponse.getStatus())
            );
    return responseEntity;
  
  }

  public static Annotation parseResponseBody(ResponseEntity<String> response) throws JsonParseException {
  AnnotationLdParser europeanaParser = new AnnotationLdParser();
  String jsonLdStr = response.getBody();
  return europeanaParser.parseAnnotation(null, jsonLdStr);
  }
 
  public static Annotation parseAndVerifyTestAnnotation(ResponseEntity<String> response) throws JsonParseException {
    return parseAndVerifyTestAnnotation(response, HttpStatus.CREATED);
  }

  public static Annotation parseAndVerifyTestAnnotation(ResponseEntity<String> response, HttpStatus status) throws JsonParseException {
      assertEquals(status.value(), response.getStatusCode().value());
      Annotation annotation = parseResponseBody(response);
      assertNotNull(annotation.getCreator());
      return annotation;
  }
  
  public static Annotation parseAndVerifyTestAnnotationUpdate(ResponseEntity<String> response) throws JsonParseException {
      return parseAndVerifyTestAnnotation(response, HttpStatus.OK);
  }
  
  public static Annotation parseAndVerifyTestAnnotationUpdate(ResponseEntity<String> response, HttpStatus status) throws JsonParseException {
      assertEquals(""+status.value(), ""+response.getStatusCode().value());
      Annotation annotation = parseResponseBody(response);
      return annotation;
  }

  public static Annotation parseAnnotation(String jsonString, MotivationTypes motivationType) throws JsonParseException {
    AnnotationLdParser europeanaParser = new AnnotationLdParser();
    return europeanaParser.parseAnnotation(motivationType, jsonString);
  }

  public static void validateOutputAgainstInput(Annotation storedAnno, Annotation inputAnno)
    throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
    
    Method[] methods = inputAnno.getClass().getMethods();
    Method currentMethod;
    Object inputProp;
    Object storedProp;
    
    for (int i = 0; i < methods.length; i++) {
        currentMethod = methods[i];
        if(currentMethod.getName().startsWith("get") && !isTechnicalMethod(currentMethod.getName())
                && !isCreator(currentMethod.getName()) && !isGenerator(currentMethod.getName()) 
                && !isIdentifier(currentMethod.getName())){
            inputProp = currentMethod.invoke(inputAnno, (Object[]) null);
            
            //compare non null fields only
            if(inputProp != null){
                storedProp = currentMethod.invoke(storedAnno, (Object[])null);
                
                if(inputProp instanceof String[])
                    assertArrayEquals((String[])inputProp, (String[])storedProp);
                else
                    assertEquals(inputProp, storedProp);
            }           
        }           
    }   
  }
 
  public static boolean isTechnicalMethod(String name) {
    return "getIdAsString".equals(name);
  }

  public static boolean isGenerator(String name) {
      return "getGenerator".equals(name);
  }
  
  public static boolean isCreator(String name) {
      return "getCreator".equals(name);
  }
  
  public static boolean isIdentifier(String name) {
     return "getIdentifier".equals(name);
  }  
  
  public static void validateResponse(ResponseEntity<String> response) throws JsonParseException {
    validateResponse(response, HttpStatus.CREATED);
  }

  public static void validateResponse(ResponseEntity<String> response, HttpStatus status) throws JsonParseException {
      assertNotNull(response.getBody());
      assertEquals(response.getStatusCode(), status);     
      Annotation storedAnno = parseResponseBody(response);
      assertNotNull(storedAnno.getIdentifier());
  }
  
  public static void validateModerationReportResponse(ResponseEntity<String> response) 
      throws JsonParseException, JSONException {
  
    assertNotNull(response.getBody());
    JSONObject jsonObject = new JSONObject(response.getBody());
  
    String reportSumStr = jsonObject.getString(AnnotationTestsConstants.TEST_REPORT_SUMMARY_FIELD);
    int summary = Integer.parseInt(reportSumStr);
    assertEquals(response.getStatusCode(), HttpStatus.OK);
    assertTrue(summary == 1);
  }
  
  public static void validateResponseForTrimming(ResponseEntity<String> response) throws JsonParseException {
    assertNotNull(response.getBody());
    assertEquals(response.getStatusCode(), HttpStatus.CREATED);
    
    Annotation storedAnno = parseResponseBody(response);
    assertNotNull(storedAnno.getBody());
    assertTrue(storedAnno.getBody().getValue().length() == AnnotationTestsConstants.BODY_VALUE_AFTER_TRIMMING.length());
  }



  public static String addFieldToQuery(String query, String field, String language) {
    if (StringUtils.isNotEmpty(field)) {
      String prefix = WebAnnotationFields.DEFAULT_LANGUAGE + WebAnnotationFields.UNDERSCORE;
      if (field.equals(WebAnnotationFields.MULTILINGUAL)) {
          if (StringUtils.isNotEmpty(language) 
                  && field.equals(WebAnnotationFields.MULTILINGUAL)) {
              prefix = language.toUpperCase() + WebAnnotationFields.UNDERSCORE;
          }
      } else {
          prefix = "";
      }
      query = prefix + field + "*:*" + query;
    } else {
      query = "*:*";
    }
    return query;
  }

  public static String buildUrl(String query, String qf, String sort, String sortOrder, String page, String pageSize,
      String profile, String language) throws IOException {
  String url = AnnotationTestsConfiguration.getInstance().getServiceUri();
  if (!url.endsWith(WebAnnotationFields.SLASH))
    url += WebAnnotationFields.SLASH;
  
  url += "search?wskey=" + AnnotationTestsConfiguration.getInstance().getApiKey();

  if (StringUtils.isNotEmpty(query)) {
      url += "&profile=" + profile;
  }

  if (StringUtils.isNotEmpty(query)) {
//      url += "&query=" + encodeUrl(query);
    url += "&query=" + query;
  }
  if (StringUtils.isNotEmpty(qf)) {
//      url += "&qf=" + encodeUrl(qf);
    url += "&qf=" + qf;
  }
  if (StringUtils.isNotEmpty(sort)) {
      url += "&sort=" + sort;
  }
  if (StringUtils.isNotEmpty(sortOrder)) {
      url += "&sortOrder=" + sortOrder;
  }
  if (StringUtils.isNotEmpty(page)) {
      url += "&page=" + page;
  } else {
      url += "&page=0";
  }

  if (StringUtils.isNotEmpty(pageSize)) {
      url += "&pageSize=" + pageSize;
  } else {
      url += "&pageSize=10";
  }

  if (StringUtils.isNotEmpty(language)) {
      url += "&language=" + language;
  }
  return url;
  }

  public static AnnotationPage getAnnotationPage(String json) throws JsonParseException {
  AnnotationPageParser apParser = new AnnotationPageParser();
  AnnotationPage ap = apParser.parseAnnotationPage(json);
  return ap;
  }

  

}
