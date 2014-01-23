package eu.europeana.annotation.mongo.model;

import java.util.List;

import com.google.code.morphia.annotations.Entity;
import com.google.code.morphia.annotations.Polymorphic;

import eu.europeana.annotation.definitions.model.AnnotationId;
import eu.europeana.annotation.definitions.model.AnnotationTypes;
import eu.europeana.annotation.definitions.model.SemanticTag;

@Entity("annotation")
@Polymorphic
public class SemanticTagImpl extends PersistentAnnotationImpl implements SemanticTag {

	
	private String label;
	private List<String> namedEntityIdList;
	private List<String> namedEntityLabelList;

	
	public SemanticTagImpl() {
		super();
		setType(AnnotationTypes.SEMANTIC_TAG.toString());
	}
	
	public SemanticTagImpl(AnnotationId annotationId) {
		super(annotationId);
		setType(AnnotationTypes.SEMANTIC_TAG.toString());
	}

	/* (non-Javadoc)
	 * @see eu.europeana.annotation.model.SemanticTag#getNamedEntityIdList()
	 */
	@Override
	public List<String> getNamedEntityIdList() {
		return namedEntityIdList;
	}

	/* (non-Javadoc)
	 * @see eu.europeana.annotation.model.SemanticTag#setNamedEntityIdList(java.util.List)
	 */
	@Override
	public void setNamedEntityIdList(List<String> namedEntityIdList) {
		this.namedEntityIdList = namedEntityIdList;
	}

	/* (non-Javadoc)
	 * @see eu.europeana.annotation.model.SemanticTag#getNamedEntityLabelList()
	 */
	@Override
	public List<String> getNamedEntityLabelList() {
		return namedEntityLabelList;
	}

	/* (non-Javadoc)
	 * @see eu.europeana.annotation.model.SemanticTag#setNamedEntityLabelList(java.util.List)
	 */
	@Override
	public void setNamedEntityLabelList(List<String> namedEntityLabelList) {
		this.namedEntityLabelList = namedEntityLabelList;
	}

	/* (non-Javadoc)
	 * @see eu.europeana.annotation.model.SemanticTag#getLabel()
	 */
	@Override
	public String getLabel() {
		return label;
	}

	/* (non-Javadoc)
	 * @see eu.europeana.annotation.model.SemanticTag#setLabel(java.lang.String)
	 */
	@Override
	public void setLabel(String label) {
		this.label = label;
	}

}
