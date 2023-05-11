package eu.europeana.annotation.client.integration.webanno.subtitle;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.stanbol.commons.exception.JsonParseException;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import eu.europeana.annotation.client.integration.webanno.BaseWebAnnotationTest;
import eu.europeana.annotation.definitions.model.Annotation;
import eu.europeana.annotation.definitions.model.vocabulary.MotivationTypes;

public class SubtitleImportTest extends BaseWebAnnotationTest {

  protected static Logger logger = LogManager.getLogger(SubtitleImportTest.class);

  static final String FOLDER_CROWD = "/app/annotation/import/crowd";
  static final String FOLDER_PROF = "/app/annotation/import/prof";
  static final String FOLDER_AUTO = "/app/annotation/import/auto";

  String createdLogFile = "created.log";
  String updatedLogFile = "updated.log";
  String dupplicatesLogFile = "dupplicates.log";
  String errorsLogFile = "errors.log";


  protected Annotation parseSubtitle(String jsonString) throws JsonParseException {
    MotivationTypes motivationType = MotivationTypes.SUBTITLING;
    return parseAnnotation(jsonString, motivationType);
  }



  @Test
  @Disabled
  public void importOneCrowdSubtitle() throws IOException, JsonParseException,
      IllegalAccessException, IllegalArgumentException, InvocationTargetException, JSONException {

    String inputFile = "100.json";
    String creator = "publisher_crowd";


    String folderCrowd = FOLDER_CROWD;
    createAnnotationFromFile(creator, new File(folderCrowd, inputFile));
  }

  @Test
  @Disabled
  public void importAllCrowdSubtitles() throws IOException, JsonParseException,
      IllegalAccessException, IllegalArgumentException, InvocationTargetException, JSONException {

    File crowdFolder = new File(FOLDER_CROWD);
    String creator = "publisher_crowd";
    String skipToFile = "36177.json";

    importAllFilesFromFolder(crowdFolder, creator, skipToFile);
  }

  @Test
  @Disabled
  public void updateCrowdSubtitles() throws IOException, JsonParseException, IllegalAccessException,
      IllegalArgumentException, InvocationTargetException, JSONException {

    File dupplicates = new File("/app/annotation/import/update_crowd/dupplicates.log");
    String creator = "publisher_crowd";

    List<String> lines = FileUtils.readLines(dupplicates, StandardCharsets.UTF_8);
    String externalId;
    String annotationId;
    for (String line : lines) {
      System.out.println("updating annotation: " + line);
      externalId = StringUtils.substringBefore(line, ';').trim();
      annotationId = StringUtils.substringAfter(line, ';').trim();
      updateAnnotationFromFile(creator, externalId, FOLDER_CROWD, annotationId);

    }
    System.out.println("finished");

  }

  @Test
  @Disabled
  public void importAllProfSubtitles() throws IOException, JsonParseException,
      IllegalAccessException, IllegalArgumentException, InvocationTargetException, JSONException {

    File crowdFolder = new File(FOLDER_PROF);
    String creator = "publisher_prof";
    String skipToFile = "";

    importAllFilesFromFolder(crowdFolder, creator, skipToFile);
  }

  @Test
  @Disabled
  public void importAllAutoSubtitles() throws IOException, JsonParseException,
      IllegalAccessException, IllegalArgumentException, InvocationTargetException, JSONException {

    File crowdFolder = new File(FOLDER_AUTO);
    String creator = "publisher_auto";
    // first import run successfully up to 1477
    String skipToFile = "4529.json";

    importAllFilesFromFolder(crowdFolder, creator, skipToFile);
  }

  private void importAllFilesFromFolder(File crowdFolder, String creator, String skipToFile) {
    File[] inputFiles = crowdFolder.listFiles();
    Arrays.sort(inputFiles);
    int cnt = 0;
    System.out.println("creating annotations from folder: " + FOLDER_CROWD);
    System.out.println("with user: " + creator);
    long start = System.currentTimeMillis();
    // change value to limit to a smaller set
    final int LIMIT = Integer.MAX_VALUE;
    boolean skip = true;
    try {
      for (File inputFile : inputFiles) {
        // handle skipped files
        if (skip && StringUtils.isBlank(skipToFile)) {
          // nothing to skip, import all
          skip = false;
        } else if (StringUtils.isNotBlank(skipToFile) && skipToFile.equals(inputFile.getName())) {
          // first file to import
          skip = false;
        }
        if (skip || isLogFile(inputFile)) {
          System.out.println("skipped file: " + inputFile);
          continue;
        }


        // if not skipped, import files
        System.out.println("importing file: " + inputFile);
        createAnnotationFromFile(creator, inputFile);

        cnt++;
        if (cnt % 100 == 0) {
          System.out.println("imported annotations: " + cnt);
        }
        if (cnt == LIMIT) {
          System.out.println("imported annotations: " + cnt);
          break;
        }
      }
    } catch (Exception e) {
      System.out.println("import failed with exception");
      e.printStackTrace();
    }
    System.out.println("Import finished in ms: " + (System.currentTimeMillis() - start));
  }



  private boolean isLogFile(File inputFile) {
    return inputFile.isDirectory();
  }



  private void createAnnotationFromFile(String creator, File inputFile)
      throws IOException, JsonParseException, JSONException {

    String requestBody = FileUtils.readFileToString(inputFile, StandardCharsets.UTF_8);
    String externalId = inputFile.getName().replace(".json", "");
    String logEntry;


    ResponseEntity<String> response =
        getApiProtocolClient().createAnnotation(true, requestBody, null, creator);
    String responseBody = response.getBody();
    if (response.getStatusCode().is2xxSuccessful()) {
      // new anno created
      Annotation storedAnno = parseSubtitle(responseBody);
      logEntry = externalId + ";" + storedAnno.getIdentifier();
      writeLogs(logEntry, inputFile.getParent(), createdLogFile);
    } else if (response.getStatusCode().is4xxClientError()) {
      // client errors, dupplicates or formatting errors
      String DUPPLICATE_MESSAGE = "Found duplicate annotations with the ids:";
      if (responseBody.contains(DUPPLICATE_MESSAGE)) {
        String dupplicateId = responseBody.substring(
            responseBody.indexOf(DUPPLICATE_MESSAGE) + DUPPLICATE_MESSAGE.length(),
            responseBody.lastIndexOf('"'));
        logEntry = externalId + ";" + dupplicateId;
        writeLogs(logEntry, inputFile.getParent(), dupplicatesLogFile);
      } else {
        // user input errors
        // TODO: handle the token expirantion issues
        writeErrorLog(externalId, inputFile.getParent(), responseBody);
      }
    } else {
      writeErrorLog(externalId, inputFile.getParent(), responseBody);
    }
  }

  private void updateAnnotationFromFile(String creator, String externalId, String folder,
      String annotationIdentifier) throws IOException, JsonParseException, JSONException {

    File inputFile = new File(folder, externalId + ".json");
    String requestBody = FileUtils.readFileToString(inputFile, StandardCharsets.UTF_8);
    String logEntry;


    ResponseEntity<String> response = getApiProtocolClient()
        .updateAnnotation(Long.parseLong(annotationIdentifier), requestBody, creator);
    String responseBody = response.getBody();
    if (response.getStatusCode().is2xxSuccessful()) {
      // new anno created
      Annotation storedAnno = parseSubtitle(responseBody);
      logEntry = externalId + ";" + storedAnno.getIdentifier();
      writeLogs(logEntry, inputFile.getParent(), updatedLogFile);
    } else if (response.getStatusCode().is4xxClientError()) {
      // client errors, dupplicates or formatting errors
      String DUPPLICATE_MESSAGE = "Found duplicate annotations with the ids:";
      if (responseBody.contains(DUPPLICATE_MESSAGE)) {
        String dupplicateId = responseBody.substring(
            responseBody.indexOf(DUPPLICATE_MESSAGE) + DUPPLICATE_MESSAGE.length(),
            responseBody.lastIndexOf('"'));
        logEntry = externalId + ";" + dupplicateId;
        writeLogs(logEntry, inputFile.getParent(), dupplicatesLogFile);
      } else {
        // user input errors
        writeErrorLog(externalId, inputFile.getParent(), responseBody);
      }
    } else {
      writeErrorLog(externalId, inputFile.getParent(), responseBody);
    }
  }


  private void writeErrorLog(String externalId, String parentFolder, String responseBody)
      throws JSONException, IOException {
    String logEntry = externalId + "\n" + getErrorMessage(responseBody);
    writeLogs(logEntry, parentFolder, errorsLogFile);
  }

  private String getErrorMessage(String error) throws JSONException {
    if (error.contains("\"message\"")) {
      JSONObject json = new JSONObject(error);
      return json.get("message").toString();
    } else if (error.contains("\"error\"")) {
      JSONObject json = new JSONObject(error);
      return json.get("error").toString();
    } else {
      return error;
    }
  }



  private void writeLogs(String entry, String parentFolder, String logFilename) throws IOException {
    File createdLogs = new File(parentFolder + "/logs/", logFilename);
    if (!createdLogs.exists()) {
      createdLogs.getParentFile().mkdirs();
    }
    FileUtils.write(createdLogs, entry + "\n", StandardCharsets.UTF_8, true);
  }



}
