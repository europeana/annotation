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
	"apiKey":"myapikey",
	"name":"meandmyself",
	"provider":"meandmyself",
	"organization":"meandmyself ltd",
	"homepage":"http://meandmyself.org",
	"anonymousUser": {
		"httpUrl":"",
		"internalType":"PERSON",
		"name":"meandmyself-anonymous",
		"email":"",
		"homepage":"",
		"userGroup":"ANONYMOUS"
	},
	"adminOfMeAndMyself": {
		"httpUrl":"",
		"internalType":"PERSON",
		"name":"meandmyself-admin",
		"email":"",
		"homepage":"",
		"userGroup":"USER"
	},
	"authenticatedUsers": {
		"tester": {
			"httpUrl":"http://meandmyself.org#tester",
			"agentType":"",
			"internalType":"PERSON",
			"name":"meandmyself-tester",
			"email":"",
			"homepage":"",
			"userGroup":"TESTER"
		}
	}
}
</textarea>
<br>

<br>			
<%@include file="footer.html"%>