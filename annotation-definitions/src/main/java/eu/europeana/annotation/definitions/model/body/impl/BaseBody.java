package eu.europeana.annotation.definitions.model.body.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import eu.europeana.annotation.definitions.model.WebAnnotationFields;
import eu.europeana.annotation.definitions.model.body.Body;
import eu.europeana.annotation.definitions.model.concept.Concept;
import eu.europeana.annotation.definitions.model.resource.impl.BaseSpecificResource;
import eu.europeana.annotation.definitions.model.vocabulary.BodyTypes;

public abstract class BaseBody extends BaseSpecificResource implements Body {
	
//	private String bodyType;
// TODO : it seems to be an old OA specification forcing the type of the body to be a list. I believe that in WA is not the case anymore   	
	private List<String> bodyType = new ArrayList<String>(2);
	private String internalId;
	
	@Override
	public String getInternalId() {
		return internalId;
	}

	@Override
	public void setInternalId(String internalId) {
		this.internalId = internalId;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	//	@Override
//	public String getBodyType() {
//		return bodyType;
//	}
	public void setTypeEnum(BodyTypes bodyType) {
//		this.bodyType = bodyType.name();
		setInternalType(bodyType.name());
	}
//	@Override
//	public void setBodyType(String bodyTypeStr) {
//		this.bodyType = bodyTypeStr.replace(" ", "");
//	}

	public void addType(String newType) {
		if (!bodyType.contains(newType)) {
			bodyType.add(newType);
		}
	}
	
	public List<String> getType() {
		return bodyType;
	}
	
	public void setType(List<String> bodyTypeList) {
		this.bodyType = bodyTypeList;
	}
	
//	public String getTypeStr() {
//		String listStr = "";
//		if (bodyType.size() > 0) {
//			listStr = "[";
//			for (String s : bodyType)
//			{
//				if (listStr.equals("[")) {
//				    listStr += s;
//				} else {
//					listStr += "," + s;
//				}
//			}
//			listStr += "]";
//		}
//		return listStr;
//	}
	
	private Concept concept;
	
	@Override
	public Concept getConcept() {
		return concept;
	}
	
	@Override
	public void setConcept(Concept concept) {
		this.concept = concept;
	}
	
	protected BaseBody(){} 
	
	protected Map<String, String> multilingual;

	@Override
	public Map<String, String> getMultilingual() {
	    if(this.multilingual == null) {
	        this.multilingual = new HashMap<String, String>();
	    }
		return multilingual;
	}

	@Override
	public void setMultilingual(Map<String, String> multilingual) {
		this.multilingual = multilingual;
	}
	
	@Override
	public void addLabelInMapping(String language, String label) {
	    if(this.multilingual == null) {
	        this.multilingual = new HashMap<String, String>();
	    }
	    this.multilingual.put(language + "_" + WebAnnotationFields.MULTILINGUAL, label);
	}

	@Override
	public boolean equals(Object other) {
	    if (!(other instanceof Body)) {
	        return false;
	    }

	    Body that = (Body) other;

	    boolean res = true;
	    
	    /**
	     * equality check for all relevant fields.
	     */
	    if ((this.getType() != null) && (that.getType() != null) &&
	    		(!this.getType().equals(that.getType()))) {
	    	System.out.println("Body objects have different body types.");
	    	res = false;
	    }
	    
	    if ((this.getContentType() != null) && (that.getContentType() != null) &&
	    		(!this.getContentType().equals(that.getContentType()))) {
	    	System.out.println("Body objects have different content types.");
	    	res = false;
	    }
	    
	    if ((this.getHttpUri() != null) && (that.getHttpUri() != null) &&
	    		(!this.getHttpUri().equals(that.getHttpUri()))) {
	    	System.out.println("Body objects have different httpUris.");
	    	res = false;
	    }
	    
	    if ((this.getLanguage() != null) && (that.getLanguage() != null) &&
	    		(!this.getLanguage().equals(that.getLanguage()))) {
	    	System.out.println("Body objects have different languages.");
	    	res = false;
	    }
		    
	    if ((this.getMediaType() != null) && (that.getMediaType() != null) &&
	    		(!this.getMediaType().equals(that.getMediaType()))) {
	    	System.out.println("Body objects have different media types.");
	    	res = false;
	    }
		    
	    if ((this.getValue() != null) && (that.getValue() != null) &&
	    		(!this.getValue().equals(that.getValue()))) {
	    	System.out.println("Body objects have different values.");
	    	res = false;
	    }
	    
	    if ((this.getMultilingual() != null) && (that.getMultilingual() != null) &&
	    		(!this.getMultilingual().toString().equals(that.getMultilingual().toString()))) {
	    	System.out.println("Body objects have different multilingual values.");
	    	res = false;
	    }
	    
	    if ((this.getConcept() != null) && (that.getConcept() != null) &&
	    		(!this.getConcept().equals(that.getConcept()))) {
	    	System.out.println("Body objects have different concept objects.");
	    	res = false;
	    }

	    if ((this.getInternalId() != null) && (that.getInternalId() != null) &&
	    		(!this.getInternalId().equals(that.getInternalId()))) {
	    	System.out.println("Body objects have different body IDs.");
	    	res = false;
	    }
	    
	    if ((this.getSource() != null) && (that.getSource() != null) &&
	    		(!this.getSource().equals(that.getSource()))) {
	    	System.out.println("Body objects have different body sources.");
	    	res = false;
	    }
	    
	    if ((this.getRole() != null) && (that.getRole() != null) &&
	    		(!this.getRole().equals(that.getRole()))) {
	    	System.out.println("Body objects have different body roles.");
	    	res = false;
	    }
	    
	    return res;
	}
		
	public boolean equalsContent(Object other) {
		return equals(other);
	}
	
	@Override
	public String toString() {
		String res = "\t### Body ###\n";
		
		if (getType() != null) 
			res = res + "\t\t" + "bodyType:" + getType().toString() + "\n";
		if (getContentType() != null) 
			res = res + "\t\t" + "contentType:" + getContentType().toString() + "\n";
		if (getMediaType() != null) 
			res = res + "\t\t" + "mediaType:" + getMediaType() + "\n";
		if (getHttpUri() != null) 
			res = res + "\t\t" + "httpUri:" + getHttpUri() + "\n";
		if (getLanguage() != null) 
			res = res + "\t\t" + "language:" + getLanguage().toString() + "\n";
		if (getValue() != null) 
			res = res + "\t\t" + "value:" + getValue().toString() + "\n";
		if (getMultilingual() != null) 
			res = res + "\t\t" + "multilingual:" + getMultilingual().toString() + "\n";
		if (getConcept() != null) 
			res = res + "\n\t\t" + "Concept:" + getConcept().toString() + "\n";
		if (getInternalId() != null) 
			res = res + "\t\t" + "bodyId:" + getInternalId().toString() + "\n";
		if (getSource() != null) 
			res = res + "\t\t" + "bodySource:" + getSource().toString() + "\n";
		if (getRole() != null) 
			res = res + "\t\t" + "bodyRole:" + getRole().toString() + "\n";
		return res;
	}	
}
