/*
* contributor license agreements.  See the NOTICE file distributed with
* this work for additional information regarding copyright ownership.
* The ASF licenses this file to You under the Apache License, Version 2.0
* (the "License"); you may not use this file except in compliance with
* the License.  You may obtain a copy of the License at
*
*     http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package eu.europeana.annotation.jsonld;

import eu.europeana.annotation.definitions.model.WebAnnotationFields;
import eu.europeana.annotation.definitions.model.agent.Agent;
import eu.europeana.annotation.definitions.model.agent.impl.SoftwareAgent;
import eu.europeana.annotation.definitions.model.body.TagBody;
import eu.europeana.annotation.definitions.model.factory.ModelObjectFactory;
import eu.europeana.annotation.definitions.model.impl.BaseObjectTag;
import eu.europeana.annotation.definitions.model.resource.InternetResource;
import eu.europeana.annotation.definitions.model.resource.impl.BaseInternetResource;
import eu.europeana.annotation.definitions.model.resource.selector.Rectangle;
import eu.europeana.annotation.definitions.model.resource.selector.Selector;
import eu.europeana.annotation.definitions.model.resource.selector.impl.SvgRectangleSelector;
import eu.europeana.annotation.definitions.model.resource.style.Style;
import eu.europeana.annotation.definitions.model.resource.style.impl.BaseStyle;
import eu.europeana.annotation.definitions.model.target.Target;
import eu.europeana.annotation.definitions.model.target.impl.ImageTarget;
import eu.europeana.annotation.definitions.model.vocabulary.AgentTypes;
import eu.europeana.annotation.definitions.model.vocabulary.AnnotationPartTypes;
import eu.europeana.annotation.definitions.model.vocabulary.AnnotationTypes;
import eu.europeana.annotation.definitions.model.vocabulary.BodyTypes;
import eu.europeana.annotation.definitions.model.vocabulary.TargetTypes;

/**
 * This class prepares a test Annotation object including all sub types like Body or Target.
 * Created object is intended for testing.
 */
public class AnnotationTestObjectBuilder {

	public final static String TEST_EUROPEANA_ID = "/testCollection/testObject";

	public static TagBody buildSemanticTagBody(String text, String language) {
		
		ModelObjectFactory objectFactory = new ModelObjectFactory();
    	TagBody body = (TagBody) objectFactory.createModelObjectInstance(
    			AnnotationPartTypes.BODY.name() + WebAnnotationFields.SPLITTER + BodyTypes.SEMANTIC_TAG.name());
		
		body.setBodyType("[oa:Tag,cnt:ContentAsText,dctypes:Text,euType:"
				+ AnnotationPartTypes.BODY.name() + WebAnnotationFields.SPLITTER
				+ BodyTypes.SEMANTIC_TAG.name() 
				+ "]"
				);
		
		body.setValue(text);
		body.setLanguage(language);
		body.setContentType("text/plain");
		body.setMediaType("[oa:SemanticTag]");
		body.setHttpUri("https://www.freebase.com/m/035br4");
		
		return body;
	}
    	
	public static Target buildTarget() {
		
		ModelObjectFactory objectFactory = new ModelObjectFactory();
    	Target target = (ImageTarget) objectFactory.createModelObjectInstance(
    			AnnotationPartTypes.TARGET.name() + WebAnnotationFields.SPLITTER + TargetTypes.IMAGE.name());

		target.setTargetType(
				"[oa:SpecificResource,euType:" 
				+ AnnotationPartTypes.TARGET.name() + WebAnnotationFields.SPLITTER
				+ TargetTypes.IMAGE.name() 
				+ "]"
				);
		
		target.setContentType("image/jpeg");
		target.setHttpUri("http://europeanastatic.eu/api/image?uri=http%3A%2F%2Fbilddatenbank.khm.at%2Fimages%2F500%2FGG_8285.jpg&size=FULL_DOC&type=IMAGE");
		target.setEuropeanaId(TEST_EUROPEANA_ID);
		
		Rectangle selector = new SvgRectangleSelector();
		target.setSelector((Selector)selector);
		
		InternetResource source = new BaseInternetResource();
		source.setContentType("text/html");
		source.setHttpUri("http://europeana.eu/portal/record//15502/GG_8285.html");
		source.setMediaType("dctypes:Text");
		target.setSource(source);
		
		return target;
	}

//	public static BaseObjectTag createTestAnnotation() {
//		AnnotationLdTest testClass = new AnnotationLdTest();
//        return testClass.createBaseObjectTagInstance();        		
//	}
	
	public static BaseObjectTag createBaseObjectTagInstance() {
		
		ModelObjectFactory objectFactory = new ModelObjectFactory();
    	BaseObjectTag baseObjectTag = (BaseObjectTag) objectFactory.createModelObjectInstance(
    			AnnotationPartTypes.ANNOTATION.name() + WebAnnotationFields.SPLITTER + AnnotationTypes.OBJECT_TAG.name());
		
		baseObjectTag.setType("oa:Annotation");
        baseObjectTag.setAnnotatedAt(AnnotationLd.convertStrToDate("2012-11-10T09:08:07"));
        baseObjectTag.setSerializedAt(AnnotationLd.convertStrToDate("2012-11-10T09:08:07"));

        // set target
		Target target = buildTarget();
		baseObjectTag.setTarget(target);
			
		//set Body
		String comment = "Vlad Tepes";
		TagBody body = buildSemanticTagBody(comment, "ro");
		baseObjectTag.setBody(body);
				
		// set annotatedBy
		Agent creator = new SoftwareAgent();
		creator.setName("annonymous web user");
//		creator.setHomepage("http://www.pro.europeana.eu/web/europeana-creative");
		baseObjectTag.setAnnotatedBy(creator);
		
		// set serializedBy
		creator = new SoftwareAgent();
		creator.setAgentType(AgentTypes.SOFTWARE_AGENT.name());
//		creator.setAgentType("prov:SoftwareAgent");
		creator.setName("Annotorious");
		creator.setHomepage("http://annotorious.github.io/");
		baseObjectTag.setSerializedBy(creator);
				
		// motivation
		baseObjectTag.setMotivatedBy("oa:tagging");
		
		// set styledBy
		Style style = new BaseStyle();
		style.setMediaType("oa:CssStyle");
		style.setContentType("annotorious-popup");
		style.setValue("http://annotorious.github.io/latest/themes/dark/annotorious-dark.css");
		baseObjectTag.setStyledBy(style);
		
		return baseObjectTag;
	}
	 
	public static BaseObjectTag createEmptyBaseObjectTagInstance() {
		
    	ModelObjectFactory objectFactory = new ModelObjectFactory();
    	BaseObjectTag baseObjectTag = (BaseObjectTag) objectFactory.createModelObjectInstance(
    			AnnotationPartTypes.ANNOTATION.name() + WebAnnotationFields.SPLITTER + AnnotationTypes.OBJECT_TAG.name());    	
		
		baseObjectTag.setType(null);
        baseObjectTag.setAnnotatedAt(null);
        baseObjectTag.setSerializedAt(null);

        // set target
		baseObjectTag.setTarget(null);
			
		//set Body
		TagBody body = buildSemanticTagBody(null, null);
		baseObjectTag.setBody(body);
				
		// set annotatedBy
		Agent creator = new SoftwareAgent();
		creator.setName(null);
		baseObjectTag.setAnnotatedBy(creator);
		
		// set serializedBy
		creator = new SoftwareAgent();
		creator.setAgentType(AgentTypes.SOFTWARE_AGENT.name());
		creator.setName(null);
		creator.setHomepage(null);
		baseObjectTag.setSerializedBy(creator);
				
		// motivation
		baseObjectTag.setMotivatedBy(null);
		
		// set styledBy
		Style style = new BaseStyle();
		style.setMediaType(null);
		style.setContentType(null);
		style.setValue(null);
		baseObjectTag.setStyledBy(style);
		
		return baseObjectTag;
	}
	         
}

