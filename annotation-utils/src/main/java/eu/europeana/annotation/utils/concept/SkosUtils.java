package eu.europeana.annotation.utils.concept;

import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.util.FileManager;

import eu.europeana.annotation.definitions.model.concept.Concept;
import eu.europeana.annotation.definitions.model.concept.impl.BaseConcept;
import eu.europeana.annotation.definitions.model.vocabulary.ConceptTypes;
import eu.europeana.annotation.definitions.model.vocabulary.fields.WebAnnotationFields;


/**
 * This class implements methods for working with SKOS. 
 *
 */
public class SkosUtils {
	
	private static Logger log = Logger.getRootLogger();

	
    /**
     * This method performs parsing of the SKOS RDF in XML format to Europeana Annotation Concept object using Jena library.
     * @param inputFileName
     * @return Concept object
     */
    public Concept parseSkosRdfXmlToConcept(String inputFileName) {
    	
    	// create an empty model
   	    Model model = ModelFactory.createDefaultModel();

    	// use the FileManager to find the input file
    	InputStream in = FileManager.get().open( inputFileName );
    	
    	if (in == null) {
    	    throw new IllegalArgumentException(
    	                                 "File: " + inputFileName + " not found");
    	}

    	// read the RDF/XML file
    	model.read(in, null);

    	// write it to standard out
    	model.write(System.out);    
    	
    	// retrieve the statements from the model
    	return retrieveStatements(model);
    }
	
    
    /**
     * This method performs parsing of the SKOS RDF in XML format to Europeana Annotation Concept collection using Jena library.
     * @param inputFileName
     * @return A collection of the Concept objects
     */
    public List<Concept> parseSkosRdfXmlToConceptCollection(String inputFileName) {
    	
    	// create an empty model
   	    Model model = ModelFactory.createDefaultModel();

    	// use the FileManager to find the input file
    	InputStream in = FileManager.get().open( inputFileName );
    	
    	if (in == null) {
    	    throw new IllegalArgumentException(
    	                                 "File: " + inputFileName + " not found");
    	}

    	// read the RDF/XML file
    	model.read(in, null);

    	// write it to standard out
    	model.write(System.out);    
    	
    	// retrieve the statements from the model
    	return retrieveConcepts(model);
    }
	
    
	/**
	 * @param orig
	 * @return
	 */
	public String setPredicate(String orig) {
		String res = "";
		res = matchPredicate(orig);
		return res;
	}
	
	/**
	 * The predicate value is obtained from RDF statement. This method retrieves the field value of the Concept object. E.g. 'prefLabel'.
	 * Sample: http://www.w3.org/2004/02/skos/core#prefLabel
	 * @param parsed predicate value
	 * @return field name of the Concept object
	 */
	private String matchPredicate(String value) {
		String res = "";
		String regex = "#(.*)$"; // we find any character sequence at the end of the line, after '#'
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(value);
		if (matcher.find()) {
		    res = matcher.group(0).substring(1); // cut first element
		}
		return res;
	}
	
	
	/**
	 * @param concept
	 * @param predicate
	 * @param conceptFieldName
	 * @param value
	 */
	@SuppressWarnings("unchecked")
	public void fillConcept(
			BaseConcept concept, String predicate, String conceptFieldName, String value) {

    	String methodName = WebAnnotationFields.ADD + conceptFieldName.substring(0, 1).toUpperCase() + conceptFieldName.substring(1);
    	String getMethodName = WebAnnotationFields.GET + conceptFieldName.substring(0, 1).toUpperCase() + conceptFieldName.substring(1);

    	Method method = null;
		Method getMethod = null;

	    try {
		    getMethod = concept.getClass().getMethod(getMethodName);
		    Class<?> returnType = getMethod.getReturnType();
		    if (returnType.equals(Map.class)) {
				method = concept.getClass().getMethod(methodName + WebAnnotationFields.IN_MAPPING, String.class, String.class);
			} else {
				method = concept.getClass().getMethod(methodName, String.class);
			}

		    if (method != null) {
			    if (returnType.equals(Map.class)) {
		    		Object valueMap = getMethod.invoke(concept);
		    		// counter is necessary to have different IDs in map for the same linguistic key
		    		int counter = 0;
		    		if (valueMap != null) {
		    			counter = ((Map<String,String>) valueMap).size();
		    		}
				    method.invoke(concept, predicate + counter, value);
		    	} else {
		  		    method.invoke(concept, value);            		
		    	}
		    }
		} catch (IllegalArgumentException e) {
        	log.error("IllegalArgumentException error by filling Concept object from SKOS RDF/XML model for Concept field: " + conceptFieldName);
		} catch (IllegalAccessException e) {
        	log.error("IllegalAccessException error by filling Concept object from SKOS RDF/XML model for Concept field: " + conceptFieldName);
		} catch (InvocationTargetException e) {
        	log.error("InvocationTargetException error by filling Concept object from SKOS RDF/XML model for Concept field: " + conceptFieldName);
		} catch (SecurityException e) {
        	log.error("SecurityException error by filling Concept object from SKOS RDF/XML model for Concept field: " + conceptFieldName);
		} catch (NoSuchMethodException e) {
        	log.error("NoSuchMethodException error by filling Concept object from SKOS RDF/XML model for Concept field: " + conceptFieldName);
        }
		
	}

	
	/**
	 * @param model
	 * @return
	 */
	public BaseConcept retrieveStatements(Model model) {
		StmtIterator itr = model.listStatements();
    	BaseConcept concept = new BaseConcept();
    	concept.addType(ConceptTypes.SKOS_CONCEPT.name());
    	    	    	
    	while (itr.hasNext()) {
    		Statement statement = itr.next();
        	Triple triple = statement.asTriple();
        	if (StringUtils.isEmpty(concept.getUri())) {
        		concept.setUri(triple.getSubject().toString());
        	}

        	String predicate = triple.getPredicate().toString();
        	String conceptFieldName = setPredicate(predicate);
        	
        	String value = triple.getObject().toString();
        	log.info("Statement: " + predicate + " = " + value);
        	
    		fillConcept(concept, predicate, conceptFieldName, value);
    	}
		return concept;
	}

	
	/**
	 * This method retrieves concepts from a model.
	 * @param model
	 * @return
	 */
	public List<Concept> retrieveConcepts(Model model) {
		List<Concept> res = new ArrayList<Concept>();
		
		ResIterator itrConcepts = model.listSubjects();
		while (itrConcepts.hasNext()) {
			Resource rdfNode = itrConcepts.next();
        	System.out.println("rdfNode: " + rdfNode.toString());
        	Property p = null;
        	String object = null;
			StmtIterator itr = model.listStatements(rdfNode, p, object);

	    	BaseConcept concept = new BaseConcept();
	    	concept.addType(ConceptTypes.SKOS_CONCEPT.name());

	    	while (itr.hasNext()) {
	    		Statement statement = itr.next();
	        	Triple triple = statement.asTriple();
	        	if (StringUtils.isEmpty(concept.getUri())) {
	        		concept.setUri(triple.getSubject().toString());
	        	}
	
	        	String predicate = triple.getPredicate().toString();
	        	String conceptFieldName = setPredicate(predicate);
	        	
	        	String value = triple.getObject().toString();
	        	log.info("Statement: " + predicate + " = " + value);
	        	System.out.println("Statement: " + predicate + " = " + value);
	        	
	    		fillConcept(concept, predicate, conceptFieldName, value);
	    	}
	    	res.add(concept);
		}
		return res;
	}

	
}
