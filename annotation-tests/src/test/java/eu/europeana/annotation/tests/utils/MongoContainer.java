package eu.europeana.annotation.tests.utils;

import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.images.builder.ImageFromDockerfile;

/** This class creates a Mongo container using the dockerfile in the docker-scripts directory. */
public class MongoContainer extends GenericContainer<MongoContainer> {

  private final String annotationDb;
  private final String adminUsername = "admin_user";
  private final String adminPassword = "admin_password";
  private final boolean useFixedPorts = false;

  /**
   * Creates a new Mongo container instance
   *
   * @param annotationDb entity database
   */
  public MongoContainer(String annotationDb) {
    this(
        new ImageFromDockerfile()
            // in test/resources directory
            .withFileFromClasspath("Dockerfile", "mongo-docker/Dockerfile")
            .withFileFromClasspath("init-mongo.sh", "mongo-docker/init-mongo.sh"),
        annotationDb);
  }

  private MongoContainer(
      ImageFromDockerfile dockerImageName, String annotationDb) {
    super(dockerImageName);

    if (useFixedPorts) {
      this.addFixedExposedPort(27018, 27017);
    } else {
      this.withExposedPorts(27017);
    }

    this.withEnv("MONGO_INITDB_ROOT_USERNAME", adminUsername)
        .withEnv("MONGO_INITDB_ROOT_PASSWORD", adminPassword)
        .withEnv("ANNOTATION_DB", annotationDb);

    this.waitingFor(Wait.forLogMessage("(?i).*waiting for connections.*", 1));
    this.annotationDb = annotationDb;
  }

  public String getConnectionUrl() {
    if (!this.isRunning()) {
      throw new IllegalStateException("MongoDBContainer should be started first");
    } else {
//      return String.format(
//          "mongodb://%s:%s@%s:%d",
//          adminUsername, adminPassword, this.getContainerIpAddress(), this.getMappedPort(27017));
//    }
      return String.format(
          "mongodb://%s:%s@%s:%d/%s",
          adminUsername, adminPassword, this.getHost(), this.getMappedPort(27017), this.getAnnotationDb());
      }
  }

  public String getAnnotationDb() {
    return annotationDb;
  }

  public String getAdminUsername() {
    return adminUsername;
  }

  public String getAdminPassword() {
    return adminPassword;
  }
}