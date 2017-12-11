<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%@include file="header.html"%>
<% 
String pageDescription="JSON templates for creation of annotation objects";
String withType = request.getParameter("withType");
boolean hasType = withType != null;		
%>	
<%@include file="description.jspf"%>

<ul id="toc">
	<li><a href="#client_application">Create client application</a></li>
</ul>

<h3 id="client_application">Create client application</h3>
The json serialization available in the following box is a valid input to be used for the creation of <b>client application</b>.
&nbsp;&nbsp;&nbsp; <a href="#top">top</a> 
<br>
<textarea rows="18" cols="120" name="json-client-application">
{
	"apiKey":"phVKTQ8g9F",
	"name":"collections",
	"provider":"collections",
	"organization":"Europeana Collections",
	"homepage":"",
	"anonymousUser": {
		"httpUrl":"",
		"agentType":"",
		"internalType":"PERSON",
		"name":"collections-anonymous",
		"email":"",
		"homepage":"",
		"userGroup":"ANONYMOUS"
	},
	"adminUser": {
		"httpUrl":"",
		"agentType":"",
		"internalType":"PERSON",
		"name":"collections-admin",
		"email":"",
		"homepage":"",
		"userGroup":"USER"
	},
	"authenticatedUsers": {
		"tester3": {
			"httpUrl":"http://labs.europeana.eu/appcategories/curation-annotation#tester3",
			"agentType":"",
			"internalType":"PERSON",
			"name":"collections-tester3",
			"email":"",
			"homepage":"",
			"userGroup":"TESTER"
		},
		"tester2": {
			"httpUrl":"http://labs.europeana.eu/appcategories/curation-annotation#tester2",
			"agentType":"",
			"internalType":"PERSON",
			"name":"collections-tester2",
			"email":"",
			"homepage":"",
			"userGroup":"TESTER"
		},
		"tester1": {
			"httpUrl":"http://labs.europeana.eu/appcategories/curation-annotation#tester1",
			"agentType":"",
			"internalType":"PERSON",
			"name":"collections-tester1",
			"email":"",
			"homepage":"",
			"userGroup":"TESTER"
		},
		"wdenrich": {
			"httpUrl":"",
			"agentType":"",
			"internalType":"SOFTWARE",
			"name":"Wikidata",
			"email":"",
			"homepage":"",
			"userGroup":"USER"
		},
		"prDAHRgSDH": {
			"httpUrl":"",
			"agentType":"",
			"internalType":"SOFTWARE",
			"name":"Europeana.eu Gallery",
			"email":"",
			"homepage":"",
			"userGroup":"USER"
		},
		"pyU4HCDWfS": {
			"httpUrl":"http://labs.europeana.eu/appcategories/curation-annotation",
			"agentType":"",
			"internalType":"PERSON",
			"name":"collections-Europeana Collections Curator",
			"email":"",
			"homepage":"",
			"userGroup":"USER"
		}
	}
}
</textarea>
<br>

<br>			
<%@include file="footer.html"%>