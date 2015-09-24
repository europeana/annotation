<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<jsp:include page="header.jspf" />
<% 
String pageDescription="JSONLD templates for creation of annotation objects";
String withType = request.getParameter("withType");
boolean hasType = withType != null;		
%>
	
<%@ include file="description.jspf" %>

					<ul id="toc">
						<li><a href="#tag">Create (Object) Tag</a></li>
						<li><a href="#objectlink">Create Object Link</a></li>
					</ul>

					<h3 id="tag">Create (Object) Tag <a href="#top">top</a></h3>

<textarea rows="20" cols="120" name="jsonldtag" >
{
    "@context": "http://www.europeana.eu/annotation/context.jsonld",
    "@type": "oa:Annotation",
<%if(!hasType){%>    "motivation": "oa:tagging",<%}%>	
    "annotatedBy": {
        "@id": "https://www.historypin.org/en/person/55376/",
        "@type": "foaf:Person",
        "name": "John Smith"
    },
    "annotatedAt": "2015-02-27T12:00:43Z",
    "serializedAt": "2015-02-28T13:00:34Z",
    "serializedBy": "http://www.historypin.org",
    "body": "church",
    "target": "http://data.europeana.eu/item/123/xyz",
    "oa:equivalentTo": "https://www.historypin.org/en/item/456"
}
</textarea>
<br>

					<h3 id="objectlink">Create Object Link <a href="#top">top</a></h3>

<textarea rows="22" cols="120" name="jsonldobjectlink" >
{
    "@context": "http://www.europeana.eu/annotation/context.jsonld",
    "@type": "oa:Annotation",
<%if(!hasType){%>    "motivation": "oa:linking",<%}%>	
    "annotatedBy": {
        "@id": "https://www.historypin.org/en/person/55376/",
        "@type": "foaf:Person",
        "name": "John Smith"
    },
    "annotatedAt": "2015-02-27T12:00:43Z",
    "serializedAt": "2015-02-28T13:00:34Z",
    "serializedBy": "http://www.historypin.org",
    "target": [
        "http://www.europeana.eu/portal/record/123/xyz.html", 
        "http://www.europeana.eu/portal/record/333/xxx.html"
    ],
    "oa:equivalentTo": "https://www.historypin.org/en/item/789"
}
</textarea>
<br>			
					
<jsp:include page="footer.jspf" />
			
