<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%@include file="header.html"%>
<% 
String pageDescription="JSONLD templates for creation of annotation objects";
String withType = request.getParameter("withType");
//String motivation = request.getParameter("motivation");
boolean hasType = withType != null;		
%>	
<%@include file="description.jspf"%>

<p>
The following properties are optional in all annotations:
<b>context, type, creator, created, generated, generator, format.</b> 
</p>
					<ul id="toc">
						<li><a href="#tag_bodyValue">Create (Object) Tag - bodyValue</a></li>
						<li><a href="#tag_TextualBody">Create (Object) Tag - TextualBody</a></li>
						<li><a href="#tag_geoTag">Create (Object) GeoTag</a></li>
						<li><a href="#semantictag_simple_minimal">Create Semantic Tag - minimal representation</a></li>
						<li><a href="#semantictag_simple">Create (Semantic) Tag</a></li>
						<li><a href="#semantictag_specific">Create (Semantic) Tag - Specific Resource</a></li>
						<li><a href="#objectlink">Create Object Link</a></li>
						<li><a href="#objectlink_specificRelation">Create Object Link - by specifying a relation type</a></li>
						<li><a href="#transcription">Create Transcription</a></li>
						<li><a href="#tag_webResource">Create (Object) tag - web resource</a></li>
					</ul>

<h3 id="tag_bodyValue">Create (Object) Tag</h3>
The json-ld serialization available in the following box is a valid input to be used for the creation of simple <b>tags</b> using the bodyValue.
The response will be however, expanded to the TextualBody representation. see also <a href="#tag_TextualBody">Create (Object) Tag - TextualBody</a>
&nbsp;&nbsp;&nbsp; <a href="#top">top</a> 
<br>
(motivation:tagging)
<textarea rows="18" cols="120" name="jsonldtag">
{
    "@context": "http://www.w3.org/ns/anno.jsonld",
    "type": "Annotation",
<% if(!hasType){ %>    "motivation": "tagging",<% }//endif%>	
    "bodyValue": "church",
    "target": "http://data.europeana.eu/item/123/xyz",
    "creator": {
        "id": "https://www.historypin.org/en/person/55376/",
        "type": "Person",
        "name": "John Smith"
    },
    "created": "2015-02-27T12:00:43Z",
    "generated": "2015-02-28T13:00:34Z",
    "generator": "http://www.historypin.org"
}
</textarea>
<br>

<h3 id="tag_TextualBody">Create (Object) Tag - TextualBody</h3>
The json-ld serialization available in the following box is a valid input to be used for the creation of (simple) <b>tags</b>. 
&nbsp;&nbsp;&nbsp; <a href="#top">top</a> 
<br>
(motivation:tagging)
<textarea rows="19" cols="120" name="jsonldtag">
{
    "@context": "http://www.w3.org/ns/anno.jsonld",
    "type": "Annotation",
<% if(!hasType){ %>    "motivation": "tagging",<% }//endif%>	
    "body": {
		"value": "biserica",
		"language": "ro"
    },
    "target": "http://data.europeana.eu/item/123/xyz",
    "creator": {
        "id": "https://www.historypin.org/en/person/55376/",
        "type": "Person",
        "name": "John Smith"
    },
    "created": "2015-02-27T12:00:43Z",
    "generated": "2015-02-28T13:00:34Z",
    "generator": "http://www.historypin.org"
}
</textarea>
<br>

<h3 id="tag_getTag">Create (Object) GeoTag</h3>
The json-ld serialization available in the following box is a valid input to be used for the creation of <b>geo tags</b>. 
&nbsp;&nbsp;&nbsp; <a href="#top">top</a> 
<br>
(motivation:tagging)
<textarea rows="12" cols="120" name="jsonldtag">
 {
    "@context": "http://www.w3.org/ns/anno.jsonld",
    "type": "Annotation",
<% if(!hasType){ %>    "motivation": "tagging",<% }//endif%>	
    "body": {
   		"@context": "http://www.europeana.eu/schemas/context/entity.jsonld",   
   		"type": "Place",
   		"lat": "48.85341",
   		"long": "2.3488"
 	},
 	"target": "http://data.europeana.eu/item/09102/_UEDIN_214"
}
</textarea>
<br>

<h3 id="semantictag_simple_minimal">Create Semantic Tag - minimal representation</h3>
The json-ld serialization available in the following box is a valid input to be used for the creation of (semantic) <b>tags</b>. 
&nbsp;&nbsp;&nbsp; <a href="#top">top</a> 
<br>
(motivation:tagging)
<textarea rows="7" cols="40" name="jsonldtag">
{
    "@context": "http://www.w3.org/ns/anno.jsonld",  
<% if(!hasType){ %>    "motivation": "tagging",<% }//endif%>	
    "body": "http://www.geonames.org/2988507",
    "target": "http://data.europeana.eu/item/09102/_UEDIN_214"
}
</textarea>
<br>

<h3 id="semantictag_simple">Create Semantic Tag - Simple Resource</h3>
The json-ld serialization available in the following box is a valid input to be used for the creation of (semantic) <b>tags</b>. Format is optional. 
&nbsp;&nbsp;&nbsp; <a href="#top">top</a> 
<br>
(motivation:tagging)
<textarea rows="10" cols="120" name="jsonldtag">
{
    "@context": "http://www.w3.org/ns/anno.jsonld",  
<% if(!hasType){ %>    "motivation": "tagging",<% }//endif%>	
    "body": {
        "id": "http://www.geonames.org/2988507",
        "format": "application/rdf+xml"
    },
    "target": "http://data.europeana.eu/item/09102/_UEDIN_214"
}
</textarea>
<!--         ,  
        "language": "en",
        "format": "application/rdf+xml"
       --> 

<br>

<h3 id="semantictag_specific">Create Semantic Tag - Specific Resource</h3>
The json-ld serialization available in the following box is a valid input to be used for the creation of (semantic) <b>tags</b>. 
&nbsp;&nbsp;&nbsp; <a href="#top">top</a> 
<br>
(motivation:tagging)
<textarea rows="12" cols="120" name="jsonldtag">
{
    "@context": "http://www.w3.org/ns/anno.jsonld",
    "type": "Annotation",
<% if(!hasType){ %>    "motivation": "tagging",<% }//endif%>	
    "body": {
        "type": "SpecificResource",
        "source": "http://www.geonames.org/2988507",
        "purpose": "tagging"   
    },
    "target": "http://data.europeana.eu/item/09102/_UEDIN_214"
}
</textarea>
<br>

<h3 id="objectlink">Create Object Link</h3>
The json-ld serialization available in the following box is a valid input to be used for the creation of generic <b>Object Links</b>. 
&nbsp;&nbsp;&nbsp; <a href="#top">top</a> 

<br>
(motivation:linking)  
<textarea rows="15" cols="120" name="jsonldobjectlink">
{
  "@context": "http://www.w3.org/ns/anno.jsonld",
  <% if(!hasType){ %>    "motivation": "linking",<% }//endif %>
  "target": [ "http://data.europeana.eu/item/2059207/data_sounds_T471_5",
              "http://data.europeana.eu/item/2059207/data_sounds_T471_4" ] 
  
}
</textarea>

<h3 id="objectlink_specificRelation">Create Object Link - by specifying a relation type </h3>
The json-ld serialization available in the following box is a valid input to be used for the creation of <b>Object Links with specification of concrete relationships</b>. 
&nbsp;&nbsp;&nbsp; <a href="#top">top</a> 

<br>
(motivation:linking)  
<textarea rows="15" cols="120" name="jsonldobjectlinkspeciicrelation">
{
  "@context": "http://www.w3.org/ns/anno.jsonld",
  <% if(!hasType){ %>    "motivation": "linking",<% }//endif %>
  "body": {
    "@graph": {
      "@context" : "http://www.europeana.eu/schemas/context/edm.jsonld",
      "id": "http://data.europeana.eu/item/2059207/data_sounds_T471_5",
      "isSimilarTo": {
        "id": "http://thesession.org/tunes/52",
        "format": "text/html",
        "title": "The Kid On The Mountain (slip jig) on The Session"
      }
    }
  },
  "target": "http://data.europeana.eu/item/2059207/data_sounds_T471_5"
}
</textarea>

<h3 id="transcription">Create annotation for transcription</h3>
The json-ld serialization available in the following box is a valid input to be used for the creation of <b>Transcription annotation</b>. The fields: "language", "value" and "edmRights" are mandatory, and "source" becomes mandatory as soon as "scope" field is in the target.
&nbsp;&nbsp;&nbsp; <a href="#top">top</a> 

<br>
(motivation:transcribing)  
<textarea rows="15" cols="120" name="jsonldobjecttranscription">
{
  <% if(!hasType){ %>    "motivation": "transcribing",<% }//endif %>
  "body": {
    "id": "https://transcribathon.com/en/documents/id-20841/item-235882/",
    "language": "de",
    "format": "text/html",
    "value": "... complete transcribed text in HTML ...",
    "edmRights": "http://creativecommons.org/licenses/by-sa/1.0/"
  },
  "target": {
    "scope": "http://data.europeana.eu/item/2020601/contributions_20841",
    "source": 
    "http://www.europeana1914-1918.eu/attachments/2020601/20841.235882.full.jpg"
  }
}
</textarea>

<h3 id="tag_webResource">Create (Object) tag - web resource </h3>
The json-ld serialization available in the following box is a valid input to be used for the creation of <b>Web resource annotation</b>. 
&nbsp;&nbsp;&nbsp; <a href="#top">top</a> 

<br>
(motivation:tagging)  
<textarea rows="15" cols="120" name="jsonldobjecttaggingwebresource">
{
  <% if(!hasType){ %>    "motivation": "tagging",<% }//endif %>
  "bodyValue": "MyTag",
  "target": {
    "type": "SpecificResource",
    "scope": "http://data.europeana.eu/item/2059207/data_sounds_T471_5",
    "source": "http://comhaltasarchive.ie/tracks/12535"
  }
}
</textarea>

<br>			
<%@include file="footer.html"%>