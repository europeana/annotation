package eu.europeana.annotation.definitions.model.impl;

import java.util.Date;
import org.apache.commons.lang3.StringUtils;
import eu.europeana.annotation.definitions.model.Annotation;
import eu.europeana.annotation.definitions.model.agent.Agent;
import eu.europeana.annotation.definitions.model.body.Body;
import eu.europeana.annotation.definitions.model.resource.style.Style;
import eu.europeana.annotation.definitions.model.target.Target;
import eu.europeana.annotation.definitions.model.view.AnnotationView;
import eu.europeana.annotation.definitions.model.vocabulary.AnnotationStates;
import eu.europeana.annotation.definitions.model.vocabulary.MotivationTypes;

public abstract class AbstractAnnotation implements Annotation, AnnotationView {

  // protected AnnotationId annotationId = null;
  protected long identifier;
  private String type;
  protected String internalType;
  private Agent creator;
  private Agent generator;
  private Date created;
  private Date generated;
  private Body body;
  private Target target;
  private String motivation;
  private Style styledBy;
  protected MotivationTypes motivationType;
  private Date disabled;
  private String sameAs;
  private String equivalentTo;
  private String status;
  private Date lastUpdate;
  private String canonical;
  private String[] via;

  public AbstractAnnotation() {
    super();
  }

  @Override
  public boolean equals(Object other) {
    // TODO: change implementation This
    if (!(other instanceof Annotation)) {
      return false;
    }

    Annotation that = (Annotation) other;

    boolean res = true;

    /**
     * equality check for all relevant fields.
     */
    if (this.getIdentifier() != that.getIdentifier()) {
      res = false;
    }

    if ((this.getType() != null) && (that.getType() != null)
        && (!this.getType().equals(that.getType()))) {
      res = false;
    }

    if ((this.getCreated() != null) && (that.getCreated() != null)
        && (!this.getCreated().equals(that.getCreated()))) {
      res = false;
    }

    if ((this.getCreator() != null) && (that.getCreator() != null)
        && (!this.getCreator().equals(that.getCreator()))) {
      res = false;
    }

    if ((this.getBody() != null) && (that.getBody() != null)
        && (!this.getBody().equals(that.getBody()))) {
      res = false;
    }

    if ((this.getTarget() != null) && (that.getTarget() != null)
        && (!this.getTarget().equals(that.getTarget()))) {
      res = false;
    }

    if ((this.getMotivation() != null) && (that.getMotivation() != null)
        && (!this.getMotivation().equals(that.getMotivation()))) {
      res = false;
    }

    if ((this.getGenerated() != null) && (that.getGenerated() != null)
        && (!this.getGenerated().equals(that.getGenerated()))) {
      res = false;
    }

    if ((this.getGenerator() != null) && (that.getGenerator() != null)
        && (!this.getGenerator().equals(that.getGenerator()))) {
      res = false;
    }

    if ((this.getStyledBy() != null) && (that.getStyledBy() != null)
        && (!this.getStyledBy().equals(that.getStyledBy()))) {
      res = false;
    }

    return res;
  }

  @Override
  public int hashCode() {
    //generate code by identifier if known annotation
    if(getIdentifier() > 0) {
      return (Long.hashCode(getIdentifier()));
    }
    
    //if identifier is not availably generate based on the mandatory motivation on field
    //note, would be good to use also the target in hashcode, but this require to implement a hascode for normal and SpecificTargets 
    return getMotivation().hashCode();
  }

  @Override
  public boolean equalsContent(Object other) {
    if (!(other instanceof Annotation)) {
      return false;
    }

    Annotation that = (Annotation) other;

    boolean res = true;

    /**
     * equality check for all relevant fields.
     */
    if ((this.getType() != null) && (that.getType() != null)
        && (!this.getType().equals(that.getType()))) {
      res = false;
    }

    if ((this.getCreated() != null) && (that.getCreated() != null)
        && (!this.getCreated().equals(that.getCreated()))) {
      res = false;
    }

    if ((this.getCreator() != null) && (that.getCreator() != null)
        && (!this.getCreator().equalsContent(that.getCreator()))) {
      res = false;
    }

    if ((this.getBody() != null) && (that.getBody() != null)
        && (!this.getBody().equalsContent(that.getBody()))) {
      res = false;
    }

    if ((this.getTarget() != null) && (that.getTarget() != null)
        && (!this.getTarget().equalsContent(that.getTarget()))) {
      res = false;
    }

    if ((this.getMotivation() != null) && (that.getMotivation() != null)
        && (!this.getMotivation().equals(that.getMotivation()))) {
      res = false;
    }

    if ((this.getGenerated() != null) && (that.getGenerated() != null)
        && (!this.getGenerated().equals(that.getGenerated()))) {
      res = false;
    }

    if ((this.getGenerator() != null) && (that.getGenerator() != null)
        && (!this.getGenerator().equalsContent(that.getGenerator()))) {
      res = false;
    }

    if ((this.getStyledBy() != null) && (that.getStyledBy() != null)
        && (!this.getStyledBy().equalsContent(that.getStyledBy()))) {
      res = false;
    }

    return res;
  }

  @Override
  public String getType() {
    return type;
  }

  @Override
  public void setType(String type) {
    this.type = type;

  }

  @Override
  public String getInternalType() {
    return internalType;
  }

  @Override
  public void setInternalType(String internalType) {
    this.internalType = internalType;
  }

  @Override
  public Date getCreated() {
    return created;
  }

  @Override
  public Agent getCreator() {
    return creator;
  }

  @Override
  public void setCreator(Agent annotatedBy) {
    this.creator = annotatedBy;
  }

  @Override
  public Body getBody() {
    return body;
  }

  @Override
  public void setBody(Body body) {
    this.body = body;
  }

  @Override
  public Target getTarget() {
    return target;
  }

  @Override
  public void setTarget(Target target) {
    this.target = target;
  }

  @Override
  public String getMotivation() {
    return motivation;
  }

  @Override
  public MotivationTypes getMotivationType() {
    return MotivationTypes.getType(getMotivation());
  }

  @Override
  public final void setMotivation(String motivation) {
    this.motivation = motivation;
  }

  @Override
  public Date getGenerated() {
    return generated;
  }


  @Override
  public Style getStyledBy() {
    return styledBy;
  }

  @Override
  public void setStyledBy(Style styledBy) {
    this.styledBy = styledBy;
  }

  @Override
  public Agent getGenerator() {
    return generator;
  }

  @Override
  public void setGenerator(Agent serializedBy) {
    this.generator = serializedBy;
  }

  @Override
  public void setCreated(Date created) {
    this.created = created;
  }

  @Override
  public void setGenerated(Date generated) {
    this.generated = generated;
  }

  @Override
  public boolean isDisabled() {
    return disabled != null;
  }

  @Override
  public void setDisabled(Date disabled) {
    this.disabled = disabled;
  }

  @Override
  public Date getDisabled() {
    return disabled;
  }

  public String getSameAs() {
    return sameAs;
  }

  public void setSameAs(String sameAs) {
    this.sameAs = sameAs;
  }

  public String getEquivalentTo() {
    return equivalentTo;
  }

  public void setEquivalentTo(String equivalentTo) {
    this.equivalentTo = equivalentTo;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public Date getLastUpdate() {
    return this.lastUpdate;
  }

  public void setLastUpdate(Date lastUpdate) {
    this.lastUpdate = lastUpdate;
  }

  public String getCanonical() {
    return canonical;
  }

  public void setCanonical(String canonical) {
    this.canonical = canonical;
  }

  public String[] getVia() {
    return via;
  }

  public void setVia(String[] via) {
    this.via = via;
  }

  @Override
  public String toString() {
    String res = "### Annotation ###\n";
    if (identifier != 0)
      res = res + "\t" + "identifier:" + identifier + "\n";
    if (type != null)
      res = res + "\t" + "type:" + type + "\n";
    if (StringUtils.isNotEmpty(canonical))
      res = res + "\t" + "canonical:" + canonical + "\n";
    if (via != null)
      res = res + "\t" + "via:" + via + "\n";
    if (created != null)
      res = res + "\t" + "annotatedAt:" + created + "\n";
    if (creator != null)
      res = res + "\t" + "annotatedBy:" + creator.toString() + "\n";
    if (generator != null)
      res = res + "\t" + "serializedBy:" + generator.toString() + "\n";
    if (styledBy != null)
      res = res + "\t" + "styledBy:" + styledBy.toString() + "\n";
    if (motivation != null)
      res = res + "\t" + "motivation:" + motivation.toString() + "\n";
    if (target != null)
      res = res + "\t" + "target:" + target.toString() + "\n";
    if (body != null)
      res = res + "\t" + "body:" + body.toString() + "\n";
    if (StringUtils.isNotEmpty(sameAs))
      res = res + "\t" + "sameAs:" + sameAs + "\n";
    if (StringUtils.isNotEmpty(equivalentTo))
      res = res + "\t" + "equivalentTo:" + equivalentTo + "\n";
    if (StringUtils.isNotEmpty(status))
      res = res + "\t" + "status:" + status + "\n";
    return res;
  }

  @Override
  public boolean isPrivate() {
    // TODO: change the usage of status to the usage of visibility when the specification is
    // complete
    return AnnotationStates.PRIVATE.equals(getStatus());
  }

  public long getIdentifier() {
    return identifier;
  }

  public void setIdentifier(long identifier) {
    this.identifier = identifier;
  }

  public String getIdentifierAsUriString() {
    return null;
  }

  public long getIdentifierAsNumber() {
    return 0;
  }
}
