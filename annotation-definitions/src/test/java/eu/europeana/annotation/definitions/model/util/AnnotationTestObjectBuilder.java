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
package eu.europeana.annotation.definitions.model.util;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import eu.europeana.annotation.definitions.model.Annotation;
import eu.europeana.annotation.definitions.model.agent.Agent;
import eu.europeana.annotation.definitions.model.body.Body;
import eu.europeana.annotation.definitions.model.factory.impl.AgentObjectFactory;
import eu.europeana.annotation.definitions.model.factory.impl.AnnotationObjectFactory;
import eu.europeana.annotation.definitions.model.factory.impl.BodyObjectFactory;
import eu.europeana.annotation.definitions.model.factory.impl.SelectorObjectFactory;
import eu.europeana.annotation.definitions.model.factory.impl.StyleObjectFactory;
import eu.europeana.annotation.definitions.model.factory.impl.TargetObjectFactory;
import eu.europeana.annotation.definitions.model.resource.InternetResource;
import eu.europeana.annotation.definitions.model.resource.impl.BaseInternetResource;
import eu.europeana.annotation.definitions.model.resource.selector.Selector;
import eu.europeana.annotation.definitions.model.resource.style.Style;
import eu.europeana.annotation.definitions.model.target.Target;
import eu.europeana.annotation.definitions.model.utils.TypeUtils;
import eu.europeana.annotation.definitions.model.vocabulary.AgentTypes;
import eu.europeana.annotation.definitions.model.vocabulary.AnnotationTypes;
import eu.europeana.annotation.definitions.model.vocabulary.BodyTypes;
import eu.europeana.annotation.definitions.model.vocabulary.MotivationTypes;
import eu.europeana.annotation.definitions.model.vocabulary.SelectorTypes;
import eu.europeana.annotation.definitions.model.vocabulary.StyleTypes;
import eu.europeana.annotation.definitions.model.vocabulary.TargetTypes;

/**
 * This class prepares a test Annotation object including all sub types like Body or Target.
 * Created object is intended for testing.
 */
public class AnnotationTestObjectBuilder {

	public final static String TEST_EUROPEANA_ID = "/testCollection/testObject";
	public final static String TEST_COLLECTION   = "testCollection";
	public final static String TEST_OBJECT       = "testObject";

	public static Body buildSemanticTagBody(String text, String language) {
		
		Body body = BodyObjectFactory.getInstance().createModelObjectInstance(
				BodyTypes.SEMANTIC_TAG.name());
		
//		body.setType("[oa:Tag,cnt:ContentAsText,dctypes:Text,euType:"
//				+ BodyTypes.SEMANTIC_TAG.name() 
//				+ "]"
//				);
		
		body.addType("oa:Tag");
		body.addType("cnt:ContentAsText");
		body.addType("dctypes:Text");
//		body.addType(BodyTypes.SEMANTIC_TAG.name());
		
		body.setValue(text);
		body.setLanguage(language);
		body.setContentType("text/plain");
		body.setMediaType("[oa:SemanticTag]");
		body.setHttpUri("https://www.freebase.com/m/035br4");
		
		return body;
	}
    	
	public static Target buildTarget() {
		
		Target target = TargetObjectFactory.getInstance().createModelObjectInstance(
				TargetTypes.IMAGE.name());

		target.setType("oa:" + TargetTypes.IMAGE.name());
		
//		target.setType(
//				"[oa:SpecificResource,euType:" 
//				+ TargetTypes.IMAGE.name() 
//				+ "]"
//				);
		
		target.setContentType("image/jpeg");
		target.setHttpUri("http://europeanastatic.eu/api/image?uri=http%3A%2F%2Fbilddatenbank.khm.at%2Fimages%2F500%2FGG_8285.jpg&size=FULL_DOC&type=IMAGE");
//		target.setEuropeanaId(TEST_EUROPEANA_ID);
		
		Selector selector = SelectorObjectFactory.getInstance().createModelObjectInstance(
				SelectorTypes.SVG_RECTANGLE_SELECTOR.name());
		target.setSelector(selector);
		
		InternetResource source = new BaseInternetResource();
		source.setContentType("text/html");
//		source.setHttpUri("http://europeana.eu/portal/record//15502/GG_8285.html");
		source.setHttpUri("http://europeana.eu/portal/record/" + TEST_EUROPEANA_ID + ".html");
		source.setMediaType("dctypes:Text");
		target.setSource(source);
		
		return target;
	}
    
	public Annotation createAnnotationJsonInstance() {
		
		Annotation annotation = AnnotationObjectFactory.getInstance().createModelObjectInstance(
				AnnotationTypes.OBJECT_TAG.name());
		
		annotation.setType(AnnotationTypes.OBJECT_TAG.name());
        
		annotation.setAnnotatedAt(TypeUtils.convertStrToDate("2012-11-10T09:08:07"));
        annotation.setSerializedAt(TypeUtils.convertStrToDate("2012-11-10T09:08:07"));

        // set target
		Target target = buildTarget();
		annotation.setTarget(target);
			
		//set Body
		String comment = "Vlad Tepes";
		Body body = buildSemanticTagBody(comment, "ro");
		annotation.setBody(body);
				
		// set annotatedBy
		Agent annotatedByAgent = buildAnnotatedByAgent();
		annotation.setAnnotatedBy(annotatedByAgent);
		
		// set serializedBy
		Agent agent = buildSerializedByAgent();
		annotation.setSerializedBy(agent);
				
		// motivation
		annotation.setMotivation(MotivationTypes.TAGGING.getOaType());
		
		// set styledBy
		Style style = buildStyledBy();
		annotation.setStyledBy(style);
		
        /**
		 * Check types and replace if necessary 
		 */
		annotation.setType(AnnotationTypes.OBJECT_TAG.name());
		annotation.setMotivation(MotivationTypes.TAGGING.name());
        
		return annotation;
	}

	public Annotation createBaseObjectTagInstance() {
	    return createBaseObjectTagInstanceWithSameAs(null);
	}
		
	public Annotation createBaseObjectTagInstanceWithSameAs(String sameAs) {
		
		Annotation annotation = AnnotationObjectFactory.getInstance().createModelObjectInstance(
				AnnotationTypes.OBJECT_TAG.name());
		
		if (StringUtils.isNotEmpty(sameAs)) {
			annotation.setSameAs(sameAs);
		}

		annotation.setEquivalentTo("http://historypin.com/annotation/1234");
		
		annotation.setType(AnnotationTypes.OBJECT_TAG.name());
        
		annotation.setAnnotatedAt(TypeUtils.convertStrToDate("2012-11-10T09:08:07"));
        annotation.setSerializedAt(TypeUtils.convertStrToDate("2012-11-10T09:08:07"));

        // set target
		Target target = buildTarget();
		annotation.setTarget(target);
			
		//set Body
		String comment = "Vlad Tepes";
		Body body = buildSemanticTagBody(comment, "ro");
		annotation.setBody(body);
				
		// set annotatedBy
		Agent annotatedByAgent = buildAnnotatedByAgent();
		annotation.setAnnotatedBy(annotatedByAgent);
		
		// set serializedBy
		Agent agent = buildSerializedByAgent();
		annotation.setSerializedBy(agent);
				
		// motivation
		annotation.setMotivation(MotivationTypes.TAGGING.getOaType());
		
		// set styledBy
		Style style = buildStyledBy();
		annotation.setStyledBy(style);
		
        /**
		 * Check types and replace if necessary 
		 */
		annotation.setType(AnnotationTypes.OBJECT_TAG.name());
		annotation.setMotivation(MotivationTypes.TAGGING.name());
        
		return annotation;
	}

	private Style buildStyledBy() {
		Style style = StyleObjectFactory.getInstance().createModelObjectInstance(
				StyleTypes.CSS.name());
//		style.setHttpUri("[oa:CssStyle,euType:" 
//				+ StyleTypes.CSS.name() 
//				+ "]");
		style.setHttpUri("oa:" + StyleTypes.CSS.name());
		style.setContentType("annotorious-popup");
		style.setValue("http://annotorious.github.io/latest/themes/dark/annotorious-dark.css");
		return style;
	}

	private Agent buildAnnotatedByAgent() {
		Agent agent = AgentObjectFactory.getInstance().createModelObjectInstance(
				AgentTypes.SOFTWARE.name());
//		agent.addType("foaf:Person");
		agent.setType("foaf:Person");
//		agent.addType(WebAnnotationFields.EU_TYPE + ":"
//				+ AgentTypes.SOFTWARE.name());
		agent.setName("annonymous web user");
		agent.setOpenId("open_id_1");
		//		agent.setHomepage("http://www.pro.europeana.eu/web/europeana-creative");
		return agent;
	}

	private Agent buildSerializedByAgent() {
		Agent agent = AgentObjectFactory.getInstance().createModelObjectInstance(
				AgentTypes.SOFTWARE.name());
//		agent.addType("prov:SoftwareAgent");
//		agent.setType("prov:SoftwareAgent");
		agent.setType("prov:Software");
//		agent.addType(WebAnnotationFields.EU_TYPE + ":"
//				+ AgentTypes.SOFTWARE.name());
		agent.setName("Annotorious");
		agent.setHomepage("http://annotorious.github.io/");
		agent.setOpenId("open_id_2");
		return agent;
	}
	 
	public Annotation createEmptyBaseObjectTagInstance() {
		
		Annotation annotation = AnnotationObjectFactory.getInstance().createModelObjectInstance(
				AnnotationTypes.OBJECT_TAG.name());
		
		annotation.setType(null);
        annotation.setAnnotatedAt(null);
        annotation.setSerializedAt(null);

        // set target
		annotation.setTarget(null);
			
		//set Body
		Body body = buildSemanticTagBody(null, null);
		annotation.setBody(body);
				
		// set annotatedBy
		Agent annotatedByAgent = AgentObjectFactory.getInstance().createModelObjectInstance(
				AgentTypes.SOFTWARE.name());
		annotatedByAgent .setName(null);
		annotation.setAnnotatedBy(annotatedByAgent);
		
		// set serializedBy
		Agent serializedByAgent = AgentObjectFactory.getInstance().createModelObjectInstance(
				AgentTypes.SOFTWARE.name());
		serializedByAgent.setName(null);
		serializedByAgent.setHomepage(null);
		annotation.setSerializedBy(serializedByAgent);
				
		// motivation
		annotation.setMotivation(null);
		
		// set styledBy
		Style style = StyleObjectFactory.getInstance().createModelObjectInstance(
				StyleTypes.CSS.name());
		style.setContentType(null);
		style.setValue(null);
		annotation.setStyledBy(style);
		
		return annotation;
	}
	         
	public static Selector buildSelector() {
		
		Selector selector = SelectorObjectFactory.getInstance().createModelObjectInstance(
				SelectorTypes.SVG_RECTANGLE_SELECTOR.name());
		
		selector.setSelectorType("oa:" + SelectorTypes.SVG_RECTANGLE_SELECTOR.name());
		
//		selector.setSelectorType("[oa:SvgRectangle,euType:"
//				+ SelectorTypes.SVG_RECTANGLE_SELECTOR.name() 
//				+ "]"
//				);
//		
		Map<String, Integer> dimensionMap = new HashMap<String, Integer>();
		dimensionMap.put("left", 5);
		dimensionMap.put("right", 3);
		selector.setDimensionMap(dimensionMap);
		
		return selector;
	}
    	
}

