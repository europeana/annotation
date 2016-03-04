<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%@include file="header.html"%>
<% 
String pageDescription="JSONLD templates for creation of annotation objects";
String withType = request.getParameter("withType");
//String motivation = request.getParameter("motivation");
boolean hasType = withType != null;		
%>	
<%@include file="description.jspf"%>

					<ul id="toc">
						<li><a href="#tag">Create (Object) Tag</a></li>
						<li><a href="#semantictag_simple">Create (Semantic) Tag</a></li>
						<li><a href="#semantictag_simple">Create (Semantic) Tag</a></li>
						<li><a href="#objectlink">Create Object Link</a></li>
					</ul>

<h3 id="tag">Create (Object) Tag</h3>
The json-ld serialization available in the following box is a valid input to be used for the creation of (simple) <b>tags</b>. 
&nbsp;&nbsp;&nbsp; <a href="#top">top</a> 
<br>
(motivation:tagging)
<textarea rows="20" cols="120" name="jsonldtag">
{
    "@context": "http://www.w3.org/ns/anno.jsonld",
    "@type": "oa:Annotation",
<% if(!hasType){ %>    "motivation": "tagging",<% }//endif%>	
    "creator": {
        "@id": "https://www.historypin.org/en/person/55376/",
        "@type": "Person",
        "name": "John Smith"
    },
    "created": "2015-02-27T12:00:43Z",
    "generated": "2015-02-28T13:00:34Z",
    "generator": "http://www.historypin.org",
    "body": "church",
    "target": "http://data.europeana.eu/item/123/xyz",
    "oa:equivalentTo": "https://www.historypin.org/en/item/456"
}
</textarea>
<br>

<!-- 
<h3 id="semantictag_simple_minimal">Create Semantic Tag - Simple Resource - minimal representation</h3>
The json-ld serialization available in the following box is a valid input to be used for the creation of (semantic) <b>tags</b>. 
&nbsp;&nbsp;&nbsp; <a href="#top">top</a> 
<br>
(motivation:tagging)
<textarea rows="20" cols="120" name="jsonldtag">
{
    "@context": "http://www.w3.org/ns/anno.jsonld",  
<%// if(!hasType){ %>    "motivation": "tagging",<%// }//endif%>	
    "body": "http://www.geonames.org/2988507",
    "target": "http://data.europeana.eu/item/09102/_UEDIN_214"
}
</textarea>
<br>
 -->

<h3 id="semantictag_simple">Create Semantic Tag - Simple Resource - extended representation</h3>
The json-ld serialization available in the following box is a valid input to be used for the creation of (semantic) <b>tags</b>. 
&nbsp;&nbsp;&nbsp; <a href="#top">top</a> 
<br>
(motivation:tagging)
<textarea rows="20" cols="120" name="jsonldtag">
{
    "@context": "http://www.w3.org/ns/anno.jsonld",  
<% if(!hasType){ %>    "motivation": "tagging",<% }//endif%>	
    "body": {
        "@id": "http://www.geonames.org/2988507"
<!--         ,  
        "language": "en",
        "format": "application/rdf+xml"
       --> 
    },
    "target": "http://data.europeana.eu/item/09102/_UEDIN_214"
}
</textarea>
<br>

<h3 id="semantictag_specific_minimal">Create Semantic Tag - Specific Resource - minimal representation</h3>
The json-ld serialization available in the following box is a valid input to be used for the creation of (semantic) <b>tags</b>. 
&nbsp;&nbsp;&nbsp; <a href="#top">top</a> 
<br>
(motivation:tagging)
<textarea rows="20" cols="120" name="jsonldtag">
{
    "@context": "http://www.w3.org/ns/anno.jsonld",
    "@type": "oa:Annotation",
<% if(!hasType){ %>    "motivation": "tagging",<% }//endif%>	
    "body": {
        "@type": "SpecificResource",
        "source": "http://www.geonames.org/2988507",
        "purpose": "tagging"   
    },
    "target": "http://data.europeana.eu/item/09102/_UEDIN_214"
}
</textarea>
<br>

<!-- 
<h3 id="semantictag_specific_extended">Create Semantic Tag - Specific Resource - extended representation</h3>
The json-ld serialization available in the following box is a valid input to be used for the creation of (semantic) <b>tags</b>. 
&nbsp;&nbsp;&nbsp; <a href="#top">top</a> 
<br>
(motivation:tagging)
<textarea rows="20" cols="120" name="jsonldtag">
{
    "@context": "http://www.w3.org/ns/anno.jsonld",
    "@type": "oa:Annotation",
<%// if(!hasType){ %>    "motivation": "tagging",<%// }//endif%>	
    "body": {
        "@type": "SpecificResource",
        "@id": "http://sws.geonames.org/2988506",  
        "source": "http://www.geonames.org/2988507",
        "purpose": "SEMANTIC_TAG",
        "language": "en",
        "format": "application/rdf+xml" 
    },
    "target": "http://data.europeana.eu/item/09102/_UEDIN_214"
}
</textarea>
<br>
 -->

<h3 id="objectlink">Create Object Link </h3>
The json-ld serialization available in the following box is a valid input to be used for the creation of <b>Object Links</b>. 
&nbsp;&nbsp;&nbsp; <a href="#top">top</a> 

<br>
(motivation:linking)  
<textarea rows="22" cols="120" name="jsonldobjectlink">
{
    "@context": "http://www.w3.org/ns/anno.jsonld",
    "@type": "oa:Annotation",
<% if(!hasType){ %>    "motivation": "linking",<% }//endif %>	
    "creator": {
        "@id": "https://www.historypin.org/en/person/55376/",
        "@type": "foaf:Person",
        "name": "John Smith"
    },
    "created": "2015-02-27T12:00:43Z",
    "generated": "2015-02-28T13:00:34Z",
    "generator": "http://www.historypin.org",
    "target": [
        "http://www.europeana.eu/portal/record/123/xyz", 
        "http://www.europeana.eu/portal/record/333/xxx"
    ],
    "oa:equivalentTo": "https://www.historypin.org/en/item/789"
}
</textarea>

<br>			
<%@include file="footer.html"%>